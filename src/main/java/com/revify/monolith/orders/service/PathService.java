package com.revify.monolith.orders.service;

import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.commons.finance.Price;
import com.revify.monolith.commons.models.orders.OrderShipmentParticle;
import com.revify.monolith.geo.GeolocationUtils;
import com.revify.monolith.geo.model.GeoLocation;
import com.revify.monolith.geo.model.shared.Destination;
import com.revify.monolith.geo.service.GeolocationService;
import com.revify.monolith.notifications.models.Notification;
import com.revify.monolith.notifications.service.NotificationService;
import com.revify.monolith.orders.models.Order;
import com.revify.monolith.orders.models.PathSegment;
import io.vavr.Tuple2;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static com.revify.monolith.orders.models.utils.OrderUtils.findShipmentParticle;

@Service
@RequiredArgsConstructor
public class PathService {

    private final GeolocationService geolocationService;

    private final OrderService orderService;

    private final MongoTemplate mongoTemplate;

    private final NotificationService notificationService;

    public Boolean userHasSplitPath() {
        Query query = Query.query(Criteria.where("courierId").is(UserUtils.getUserId()));
        return mongoTemplate.exists(query, Order.class);
    }

    public List<PathSegment> findPathsFromPoint(Destination nearEnd, Destination start, Integer limit, Integer offset) {
        Query query = Query.query(
                Criteria.where("particle.from.location").near(new GeoJsonPoint(start.getLon(), start.getLat())).maxDistance(1000.0)
                        .and("courierId").ne(UserUtils.getUserId())
        );
        if (nearEnd != null) {
            query.addCriteria(Criteria.where("particle.to.location").near(new GeoJsonPoint(nearEnd.getLon(), nearEnd.getLat())).maxDistance(10000.0));
        }
        query.with(Sort.by(Sort.Direction.ASC, "validUntil"));
        query.skip((long) offset * limit).limit(limit);
        return mongoTemplate.find(query, PathSegment.class);
    }

    public List<PathSegment> getCurrentUserPaths() {
        Query query = Query.query(Criteria.where("acceptedCourierId").is(UserUtils.getUserId()));

        query.with(Sort.by(Sort.Direction.ASC, "validUntil").and(Sort.by(Sort.Direction.DESC, "isArchived")));
        return mongoTemplate.find(query, PathSegment.class);
    }

    public List<PathSegment> getCreatedByUserPaths() {
        Query query = Query.query(Criteria.where("courierId").is(UserUtils.getUserId()));

        query.with(Sort.by(Sort.Direction.ASC, "validUntil").and(Sort.by(Sort.Direction.DESC, "isArchived")));
        return mongoTemplate.find(query, PathSegment.class);
    }

    public List<PathSegment> getUserItemsInvolvedInPaths() {
        Query query = Query.query(Criteria.where("receiverId").is(UserUtils.getUserId()));

        query.with(Sort.by(Sort.Direction.ASC, "validUntil").and(Sort.by(Sort.Direction.DESC, "isArchived")));
        return mongoTemplate.find(query, PathSegment.class);
    }

    public void changePathAcceptanceStatus(ObjectId fragmentId, Map<String, Boolean> statusChanges) {
        PathSegment pathSegment = mongoTemplate.findOne(Query.query(Criteria.where("_id").is(fragmentId)), PathSegment.class);
        if (pathSegment != null) {
            Order pathRelatedOrder = pathSegment.getOrder();
            if (pathRelatedOrder.getReceivers() == null || pathRelatedOrder.getReceivers().isEmpty() ||
                    !pathRelatedOrder.getReceivers().contains(UserUtils.getUserId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            if (statusChanges.containsKey("isAcceptedByCustomer")) {
                pathSegment.setIsAcceptedByCustomer(statusChanges.get("isAcceptedByCustomer"));
            }
            if (statusChanges.containsKey("isArchived")) {
                pathSegment.setIsAcceptedByCustomer(statusChanges.get("isArchived"));
            }
            if (statusChanges.containsKey("isCompleted")) {
                pathSegment.setIsAcceptedByCustomer(statusChanges.get("isCompleted"));
            }
            mongoTemplate.save(pathSegment);

            //notify courier
            {
                notificationService.createNotification(Notification.builder()
                        .build());
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Path not found");
    }

    //courier could have only 1 path?
    public PathSegment createNewSplitOrder(ObjectId orderId, Destination endPoint) {
        if (userHasSplitPath()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You are already split path");
        }

        Order orderById = orderService.findOrderById(orderId);
        if (orderById == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order not found");
        }

        OrderShipmentParticle.OrderShipmentParticleBuilder newShipmentParticle = OrderShipmentParticle.builder();

        BigDecimal priceCoefficient;

        Tuple2<Integer, OrderShipmentParticle> shipmentParticle = findShipmentParticle(UserUtils.getUserId(), orderById.getShipmentParticle());
        var current = shipmentParticle._2;
        var next = shipmentParticle._2.getNext();
        if (next != null) {
            //calculate distance between two points if new one is significantly bigger than previous - restrict
            GeoJsonPoint currentLocation = current.getFrom().getLocation();
            GeoJsonPoint nextLocation = next.getFrom().getLocation();

            double existingLegDistance = GeolocationUtils.haversineDistance(
                    currentLocation.getY(), currentLocation.getX(),
                    nextLocation.getY(), nextLocation.getX());

            double newFirstLegDistance = GeolocationUtils.haversineDistance(
                    currentLocation.getY(), currentLocation.getX(),
                    endPoint.getLat(), endPoint.getLon());

            double newSecondLegDistance = GeolocationUtils.haversineDistance(
                    endPoint.getLat(), endPoint.getLon(),
                    nextLocation.getY(), nextLocation.getX());

            double toleranceFactor = 1.45;
            if (newFirstLegDistance + newSecondLegDistance > existingLegDistance * toleranceFactor) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "New split point would create an unreasonably long detour > 25%");
            }

            double minimumLegDistance = 1000;
            if (newFirstLegDistance < minimumLegDistance || newSecondLegDistance < minimumLegDistance) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "New split point is too close to an existing point");
            }

            double maxDeviationDistance = 250000;
            double deviation = calculateDeviationFromPath(
                    currentLocation, nextLocation, endPoint);
            if (deviation > maxDeviationDistance) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "New split point is too far from the original route");
            }
            priceCoefficient = new BigDecimal(existingLegDistance / newSecondLegDistance).setScale(2, RoundingMode.HALF_UP);
            newShipmentParticle.to(next.getFrom());
        } else {
            GeoJsonPoint currentLocation = current.getFrom().getLocation();
            GeoJsonPoint nextLocation = next.getFrom().getLocation();

            double existingLegDistance = GeolocationUtils.haversineDistance(
                    currentLocation.getY(), currentLocation.getX(),
                    nextLocation.getY(), nextLocation.getX());

            double newFirstLegDistance = GeolocationUtils.haversineDistance(
                    currentLocation.getY(), currentLocation.getX(),
                    endPoint.getLat(), endPoint.getLon());

            priceCoefficient = new BigDecimal(existingLegDistance / newFirstLegDistance).setScale(2, RoundingMode.HALF_UP);
        }

        GeoLocation geoLocation = geolocationService.resolveLocation(endPoint.getLat(), endPoint.getLon());
        if (geoLocation == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Cannot resolve geolocation for %s, %s", endPoint.getLat(), endPoint.getLon()));
        }

        newShipmentParticle.from(geoLocation);
        {
            var currentPrice = current.getPrice();
            Price newParticlePrice = Price.builder()
                    .withAmount(priceCoefficient.multiply(currentPrice.getAmount()))
                    .withCurrency(currentPrice.getCurrency())
                    .build();
            newShipmentParticle.price(newParticlePrice);

            current.setPreviousPrice(current.getPrice());
            currentPrice.setAmount(currentPrice.getAmount().subtract(newParticlePrice.getAmount()));
        }
        current.setTo(geoLocation);

        newShipmentParticle.isSplit(true);
        newShipmentParticle.next(next);
        OrderShipmentParticle built = newShipmentParticle.build();
        current.setNext(built);

        PathSegment pathSegment = new PathSegment();
        pathSegment.setCreatedAt(Instant.now().toEpochMilli());
        pathSegment.setValidUntil(Instant.now().plus(2, ChronoUnit.DAYS).toEpochMilli());
        pathSegment.setOrder(orderById);
        pathSegment.setParticle(built);
        pathSegment.setCourierId(UserUtils.getUserId());


        mongoTemplate.save(orderById);
        pathSegment.setReceivers(orderById.getReceivers());
        return mongoTemplate.save(pathSegment);
    }

    private double calculateDeviationFromPath(GeoJsonPoint start, GeoJsonPoint end, Destination point) {
        // Calculate the shortest distance from the new point to the original path
        // This is a simplified version - you might want a more accurate calculation
        double pathDistance = GeolocationUtils.haversineDistance(
                start.getY(), start.getX(), end.getY(), end.getX());

        double distanceToStart = GeolocationUtils.haversineDistance(
                start.getY(), start.getX(), point.getLat(), point.getLon());

        double distanceToEnd = GeolocationUtils.haversineDistance(
                end.getY(), end.getX(), point.getLat(), point.getLon());

        double a = distanceToStart;
        double b = distanceToEnd;
        double c = pathDistance;

        double angle = Math.acos((a * a + c * c - b * b) / (2 * a * c));
        double deviation = a * Math.sin(angle);

        return deviation;
    }
}
