package com.revify.monolith.shoplift.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.result.UpdateResult;
import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.commons.finance.Currency;
import com.revify.monolith.commons.finance.Price;
import com.revify.monolith.commons.items.Category;
import com.revify.monolith.commons.models.bid.AuctionChangesRequest;
import com.revify.monolith.commons.models.orders.OrderAdditionalStatus;
import com.revify.monolith.commons.models.orders.OrderCreationDTO;
import com.revify.monolith.commons.models.orders.OrderShipmentParticle;
import com.revify.monolith.commons.models.orders.OrderShipmentStatus;
import com.revify.monolith.currency_reader.service.CurrencyService;
import com.revify.monolith.geo.model.GeoLocation;
import com.revify.monolith.geo.model.UserGeolocation;
import com.revify.monolith.geo.service.GeolocationService;
import com.revify.monolith.history.HistoryService;
import com.revify.monolith.history.model.ShopliftHistoryEvent;
import com.revify.monolith.history.model.ShopliftItemEvent;
import com.revify.monolith.items.model.item.Item;
import com.revify.monolith.items.service.item.ItemReadService;
import com.revify.monolith.orders.service.OrderService;
import com.revify.monolith.shoplift.model.Filter;
import com.revify.monolith.shoplift.model.Shop;
import com.revify.monolith.shoplift.model.Shoplift;
import com.revify.monolith.shoplift.model.ShopliftEvent;
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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.revify.monolith.commons.messaging.KafkaTopic.AUCTION_CHANGES;
import static com.revify.monolith.commons.messaging.KafkaTopic.ITEM_ADD_SHOPLIFT;
import static com.revify.monolith.commons.messaging.WsQueues.SHOPLIFT_EVENTS;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopliftService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final MongoTemplate mongoTemplate;

    private final GeolocationService geolocationService;

    private final ItemReadService itemReadService;

    private final CurrencyService currencyService;

    private final ShopRepository shopRepository;

    private final ReadUserService readUserService;

    private final HistoryService historyService;

    private final OrderService orderService;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final Gson gson = new GsonBuilder().create();

    @KafkaListener(topics = ITEM_ADD_SHOPLIFT)
    public void addItem(@Payload String itemId) {
        Item currentItem = itemReadService.findById(itemId);
        if (currentItem == null) {
            throw new RuntimeException("Item not found");
        }
        BigDecimal bigDecimal = currencyService.convertTo(currentItem.getPrice(), Currency.EUR);
        Query findForCategoriesQuery = createFindForCategoriesQuery(
                currentItem,
                Price.builder().withAmount(bigDecimal)
                        .withCurrency(Currency.EUR)
                        .build());
        findForCategoriesQuery.addCriteria(Criteria.where("allowedSystemAppend").is(true)
                .and("creatorId").ne(currentItem.getCreatorId()));

        Update update = new Update().push("connectedItems", itemId);

        UpdateResult result = mongoTemplate.updateMulti(findForCategoriesQuery, update, Shoplift.class);

        log.debug("Modified {} elements on [addItem|{}]", result.getModifiedCount(), itemId);

        Set<Category> categories = currentItem.getItemDescription().getCategories();
        if (categories.isEmpty()) {
            throw new RuntimeException("Category not found");
        }
    }

    public void addShopliftItem(ObjectId itemId, ObjectId shopliftId) {
        addShopliftItem(itemReadService.findById(itemId), shopliftId.toHexString());
    }

    public void addShopliftItem(Item item, String shopliftId) {
        if (item == null) {
            throw new RuntimeException("Item cannot be null");
        }

        Shoplift currentShoplift = getById(shopliftId);
        if (currentShoplift == null) {
            throw new RuntimeException("Shoplift cannot be found");
        }

        if (item.getItemDescription().getCategories().stream().noneMatch(currentShoplift.getPresentCategories()::contains)) {
            throw new RuntimeException("Item categories does not satisfy shoplift boundaries");
        }

        Update update = new Update().push("connectedItems", item.getId());

        mongoTemplate.updateMulti(Query.query(Criteria.where("_id").is(shopliftId)), update, Shoplift.class);
    }

    public void acceptShoplifting(Accept_Shoplift req) {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing 'items' field");
        }
        Shoplift shoplift = mongoTemplate.findById(new ObjectId(req.getShopliftId()), Shoplift.class);
        if (shoplift == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing 'shopliftId' field");
        }
        if (shoplift.getCreatorId() != UserUtils.getUserId() && !shoplift.getInvolvedCourierIds().contains(UserUtils.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not allowed to accept shoplifting");
        }

        List<Item> existingItems = itemReadService.findForIds(req.getItems());

        createShopliftingOrders(shoplift, existingItems, UserUtils.getUserId());

        historyService.event(ShopliftHistoryEvent.builder()
                .shopliftId(shoplift.getId().toHexString())
                .actorId(UserUtils.getUserId())
                .description(shoplift.toString())
                .build());
    }

    public void createShopliftingOrders(Shoplift shoplift, List<Item> involvedItems, Long courierId) {
        for (Item item : involvedItems) {
            Price finalPrice = shoplift.getMinEntryDeliveryPrice();
            if (shoplift.getMinEntryDeliveryPrice().getCurrency() != item.getPrice().getCurrency()) {
                finalPrice.add(currencyService.convertTo(item.getPrice(), finalPrice.getCurrency()));
            }

            orderService.createOrder(OrderCreationDTO.builder()
                    .receivers(Collections.singletonList(item.getCreatorId()))
                    .items(Collections.singletonList(item.getId().toHexString()))
                    .status(OrderShipmentStatus.CREATED)
                    .additionalStatus(OrderAdditionalStatus.CLIENT_PAYMENT_AWAIT)
                    .shipmentParticle(prepareShopliftParticle(courierId, item, finalPrice))
                    .deliveryTimeEnd(shoplift.getDeliveryCutoffTime())
                    .isShoplift(true)
                    .build());

            historyService.event(ShopliftItemEvent.builder()
                    .actorId(courierId)
                    .itemId(item.getId().toHexString())
                    .shopliftId(shoplift.getId().toHexString())
                    .timestamp(Instant.now().toEpochMilli())
                    .type(ShopliftItemEvent.EventType.ORDER_CREATION)
                    .build());

            //saving item relation and picked-state from shoplift and disable auction for that item
            {
                item.setShopliftId(shoplift.getId().toHexString());
                mongoTemplate.save(item);

                //disable item auction
                kafkaTemplate.send(AUCTION_CHANGES,
                        gson.toJson(
                                AuctionChangesRequest.builder()
                                        .changeValidUntil(item.getValidUntil())
                                        .isActive(false)
                                        .itemId(item.getId().toHexString())
                                        .build()
                        )
                );
            }
        }

        if (!involvedItems.isEmpty()) {
            simpMessagingTemplate.convertAndSend(SHOPLIFT_EVENTS + shoplift.getId().toHexString(), ShopliftEvent.builder()
                    .activeAt(Instant.now().toEpochMilli())
                    .type(ShopliftEvent.ShopliftEventType.ITEMS_CHANGED)
                    .build());
        }
    }

    public Query createFindForCategoriesQuery(Item currentItem, Price EURmaxRequiredPrice) {
        if (!EURmaxRequiredPrice.getCurrency().equals(Currency.EUR)) {
            throw new RuntimeException("Method is operating on EUR only");
        }

        Query query = new Query();
        Criteria criteria = new Criteria().andOperator(
                Criteria.where("destination.location").near(currentItem.getItemDescription().getDestination()
                        .getLocation()).maxDistance(10000.0),
                Criteria.where("category").in(currentItem.getItemDescription().getCategories()),
                Criteria.where("EURminEntryDeliveryPrice.amount").lte(EURmaxRequiredPrice.getAmount()),
                Criteria.where("allowedSystemAppend").is(true),
                Criteria.where("deliveryCutoffTime").lte(currentItem.getValidUntil()),
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
        newShoplift.setEURminEntryDeliveryPrice(Price.builder()
                .withAmount(currencyService.convertTo(shoplift.getMinEntryDeliveryPrice(), Currency.EUR))
                .withCurrency(Currency.EUR)
                .build());

        newShoplift.setProjectedCategories(projectedCategories);
        newShoplift.setPresentCategories(shoplift.getPresentCategories().stream()
                .map(Category::valueOf).collect(Collectors.toSet()));
        newShoplift.setInvolvedCourierIds(Set.of(UserUtils.getUserId()));

        newShoplift.setCreatorId(UserUtils.getUserId());
        newShoplift.setDestination(destination);
        newShoplift.setCreatedAt(Instant.now().toEpochMilli());
        newShoplift.setUpdatedAt(Instant.now().toEpochMilli());
        newShoplift.setIsRecurrent(false);
        newShoplift.setConnectedItems(new HashSet<>());

        return mongoTemplate.save(newShoplift);
    }

    public Shop findShop(String shopId) {
        return shopRepository.findById(UUID.fromString(shopId)).orElse(null);
    }

    public Boolean shopExists(String shopId) {
        return mongoTemplate.exists(Query.query(Criteria.where("_id").is(shopId)), Shop.class);
    }

    public void disable(String shopliftId, boolean state) {
        Shoplift byId = getById(shopliftId);
        if (byId == null || byId.getCreatorId() != UserUtils.getUserId()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot modify shoplift");
        }

        byId.setIsActive(state);
        byId.setUpdatedAt(Instant.now().toEpochMilli());
        mongoTemplate.save(byId);
    }

    private OrderShipmentParticle prepareShopliftParticle(Long courierId, Item item, Price finalPrice) {
        OrderShipmentParticle.OrderShipmentParticleBuilder builder = OrderShipmentParticle.builder();
        builder.price(finalPrice);
        builder.courierId(courierId);
        builder.to(item.getItemDescription().getDestination());

        UserGeolocation latestUserGeolocation = geolocationService.findLatestUserGeolocation(courierId);
        if (latestUserGeolocation != null) {
            builder.from(latestUserGeolocation.getGeoLocation());
        }

        builder.deliveryTimeEstimated(1000L * 60 * 60 * 24 * 7);

        return builder.build();
    }
}
