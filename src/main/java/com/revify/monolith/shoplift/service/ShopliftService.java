package com.revify.monolith.shoplift.service;

import com.mongodb.client.result.UpdateResult;
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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static com.revify.monolith.commons.messaging.KafkaTopic.ITEM_ADD_SHOPLIFT;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopliftService {

    private final MongoTemplate mongoTemplate;

    private final GeolocationService geolocationService;

    private final ItemReadService itemReadService;

    private final CurrencyService currencyService;

    private final KafkaTemplate<String, String> kafkaTemplate;

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

    public void acceptShoplifting(Accept_Shoplift req) {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing 'items' field");
        }
        Shoplift shoplift = mongoTemplate.findById(new ObjectId(req.getShopliftId()), Shoplift.class);
        if (shoplift == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing 'shopliftId' field");
        }

        List<Item> existingItems = itemReadService.findForIds(req.getItems());
        for (Item item : existingItems) {
            shoplift.getConnectedItems().add(item.getId().toHexString());

            item.setPicked(true);
            item.setShopliftId(shoplift.getId().toHexString());
            mongoTemplate.save(item);
        }

        mongoTemplate.save(shoplift);

        {

        }
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
                new Criteria().orOperator(
                        Criteria.where("deliveryCutoffTime").lt(Instant.now().toEpochMilli()),
                        Criteria.where("isRecurrent").is(true)
                )
        );
        query.addCriteria(criteria);
        return query;
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
            query.addCriteria(Criteria.where("destination.location").near(new GeoJsonPoint(filter.getLat(), filter.getLon())));
        }

        query.with(Sort.by(Sort.Direction.DESC, "deliveryCutoffTime"));
        query.skip((long) filter.getLimit() * filter.getOffset())
                .limit(filter.getLimit());

        return mongoTemplate.find(query, Shoplift.class);
    }

    public Shoplift createNew(Create_Shoplift shoplift) {
        Shop shop = findShop(shoplift.getShopId());
        if (shop == null) {
            throw new RuntimeException("Shop not found");
        }

        GeoLocation destination = geolocationService.resolveLocation(shoplift.getDestination());

        Shoplift newShoplift = Shoplift.from(shoplift);
        newShoplift.setDestination(destination);
        return mongoTemplate.save(newShoplift);
    }

    public Shop findShop(String shopId) {
        return mongoTemplate.findOne(Query.query(Criteria.where("_id").is(shopId)), Shop.class);
    }
}
