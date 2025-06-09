package com.revify.monolith.shoplift.service;

import com.mongodb.client.result.UpdateResult;
import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.commons.finance.Currency;
import com.revify.monolith.commons.finance.Price;
import com.revify.monolith.commons.items.Category;
import com.revify.monolith.currency_reader.service.CurrencyService;
import com.revify.monolith.geo.model.GeoLocation;
import com.revify.monolith.geo.service.GeolocationService;
import com.revify.monolith.items.model.item.Item;
import com.revify.monolith.items.service.item.ItemReadService;
import com.revify.monolith.shoplift.model.Filter;
import com.revify.monolith.shoplift.model.Shop;
import com.revify.monolith.shoplift.model.Shoplift;
import com.revify.monolith.shoplift.model.req.Accept_Shoplift;
import com.revify.monolith.shoplift.model.req.Create_Shoplift;
import com.revify.monolith.shoplift.repo.ShopRepository;
import com.revify.monolith.user.service.ReadUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.revify.monolith.commons.messaging.KafkaTopic.ITEM_ADD_SHOPLIFT;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopliftService {

    private final MongoTemplate mongoTemplate;

    private final GeolocationService geolocationService;

    private final ItemReadService itemReadService;

    private final CurrencyService currencyService;

    private final ShopRepository shopRepository;

    private final ReadUserService readUserService;

    @KafkaListener(topics = ITEM_ADD_SHOPLIFT)
    public void addItem(@Payload String itemId) {
        Item byId = itemReadService.findById(itemId);
        if (byId == null) {
            throw new RuntimeException("Item not found");
        }
        BigDecimal bigDecimal = currencyService.convertTo(byId.getPrice(), Currency.EUR);
        Query findForCategoriesQuery = createFindForCategoriesQuery(byId.getItemDescription().getCategories(),
                byId.getItemDescription().getDestination(),
                Price.builder().withAmount(bigDecimal)
                        .withCurrency(Currency.EUR)
                        .build());
        findForCategoriesQuery.addCriteria(Criteria.where("allowedSystemAppend").is(true));

        Update update = new Update().push("connectedItems", itemId);

        UpdateResult result = mongoTemplate.updateMulti(findForCategoriesQuery, update, Shoplift.class);

        log.debug("Modified {} elements on [addItem|{}]", result.getModifiedCount(), itemId);

        Set<Category> categories = byId.getItemDescription().getCategories();
        if (categories.isEmpty()) {
            throw new RuntimeException("Category not found");
        }
    }

    public void addItem(Item item) {
        if (item.getShopliftId() == null || item.getShopliftId().isEmpty()) {
            throw new RuntimeException("Item not found");
        }

        Shoplift byId = getById(item.getShopliftId());
        if (byId == null) {
            throw new RuntimeException("Item not found");
        }

        Update update = new Update().push("connectedItems", item.getId());

        mongoTemplate.updateMulti(Query.query(Criteria.where("_id").is(item.getShopliftId())), update, Shoplift.class);
    }

    public void acceptShoplifting(Accept_Shoplift req) {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing 'items' field");
        }
        Shoplift shoplift = mongoTemplate.findById(new ObjectId(req.getShopliftId()), Shoplift.class);
        if (shoplift == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing 'shopliftId' field");
        }
        if (shoplift.getCourierId() != UserUtils.getUserId() && !shoplift.getAdditionalCourierIds().contains(UserUtils.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not allowed to accept shoplifting");
        }

        List<Item> existingItems = itemReadService.findForIds(req.getItems());
        for (Item item : existingItems) {
            shoplift.getConnectedItems().add(item.getId().toHexString());

            item.setPicked(true);
            item.setShopliftId(shoplift.getId().toHexString());
            mongoTemplate.save(item);
        }

        mongoTemplate.save(shoplift);
    }

    public Query createFindForCategoriesQuery(Set<Category> categories, GeoLocation itemDestination, Price EURmaxRequiredPrice) {
        if (!EURmaxRequiredPrice.getCurrency().equals(Currency.EUR)) {
            throw new RuntimeException("Method is operating on EUR only");
        }

        Query query = new Query();
        Criteria criteria = new Criteria().andOperator(
                Criteria.where("destination.location").near(itemDestination.getLocation()).maxDistance(10000.0),
                Criteria.where("category").in(categories),
                Criteria.where("EURminEntryDeliveryPrice.amount").lte(EURmaxRequiredPrice.getAmount()),
                Criteria.where("allowedSystemAppend").is(true),
                new Criteria().orOperator(
                        Criteria.where("deliveryCutoffTime").lt(Instant.now().toEpochMilli()),
                        Criteria.where("isRecurrent").is(true)
                )
        );
        query.addCriteria(criteria);
        return query;
    }

    public Shoplift getById(String id) {
        if (!ObjectId.isValid(id)) {
            throw new IllegalArgumentException("Invalid Shoplift ID");
        }
        return mongoTemplate.findById(new ObjectId(id), Shoplift.class);
    }

    public List<Shoplift> find(Filter filter) {
        Query query = new Query();

        if (filter.getCategories() != null && !filter.getCategories().isEmpty()) {
            query.addCriteria(Criteria.where("projectedCategories").in(filter.getCategories()));
        }

        if (filter.getItemsFilter() != null && !filter.getItemsFilter().isEmpty()) {
            query.addCriteria(Criteria.where("connectedItems").in(filter.getItemsFilter()));
        }
        if (filter.getTimeSort() != null) {
            if (filter.getTimeSort().equals("ASC")) {
                query.with(Sort.by(Sort.Direction.ASC, "deliveryCutoffTime"));
            }
        }
        if (filter.getLat() != null && filter.getLon() != null) {
            query.addCriteria(Criteria.where("destination.location").near(new GeoJsonPoint(filter.getLon(), filter.getLat())));
        }

        query.with(Sort.by(Sort.Direction.DESC, "deliveryCutoffTime"));
        query.skip((long) filter.getLimit() * filter.getOffset())
                .limit(filter.getLimit());

        return mongoTemplate.find(query, Shoplift.class);
    }

    public Shoplift createNew(Create_Shoplift shoplift) {
        if (readUserService.isClient()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot create a new shoplift");
        }
        shoplift.getShopIds().removeIf(this::shopExists);

        GeoLocation destination = geolocationService.resolveLocation(shoplift.getDestination());

        Set<Category> projectedCategories = new HashSet<>();
        for (String shopId : shoplift.getShopIds()) {
            Shop byId = shopRepository.getReferenceById(UUID.fromString(shopId));
            projectedCategories.addAll(byId.getCategories());
        }

        Shoplift newShoplift = Shoplift.from(shoplift);
        newShoplift.setProjectedCategories(projectedCategories);
        newShoplift.setPresentCategories(shoplift.getPresentCategories().stream()
                .map(Category::valueOf).collect(Collectors.toSet()));
        newShoplift.setCourierId(UserUtils.getUserId());
        newShoplift.setDestination(destination);
        newShoplift.setCreatedAt(Instant.now().toEpochMilli());
        newShoplift.setUpdatedAt(Instant.now().toEpochMilli());
        newShoplift.setIsRecurrent(false);
        return mongoTemplate.save(newShoplift);
    }

    public Shop findShop(String shopId) {
        return shopRepository.findById(UUID.fromString(shopId)).orElse(null);
    }

    public Boolean shopExists(String shopId) {
        return mongoTemplate.exists(Query.query(Criteria.where("_id").is(shopId)), Shop.class);
    }

    public void disable(String shopliftId) {
        Shoplift byId = getById(shopliftId);
        if (byId == null || byId.getCourierId() != UserUtils.getUserId()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot modify shoplift");
        }

        byId.setIsActive(false);
        byId.setUpdatedAt(Instant.now().toEpochMilli());
        mongoTemplate.save(byId);
    }
}
