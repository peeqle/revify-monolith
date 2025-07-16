package com.revify.monolith.items.service.item;

import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.commons.items.Category;
import com.revify.monolith.items.model.item.Item;
import com.revify.monolith.user.service.ReadUserService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ItemReadService {

    private final MongoTemplate mongoTemplate;

    private final ReadUserService readUserService;

    public Item findById(String id) {
        return findById(new ObjectId(id));
    }

    public Item findById(ObjectId id) {
        return mongoTemplate.findById(id, Item.class);
    }


    public List<Item> findForIds(Collection<String> ids) {
        Query query = Query.query(Criteria.where("_id").in(ids.stream().map(ObjectId::new).toArray())
                .and("isActive").is(true));
        return mongoTemplate.find(query, Item.class);
    }

    public Item findByIdAndUser(ObjectId id, Long userId) {
        Query query = Query.query(Criteria.where("_id").is(id).and("creatorId").is(userId))
                .maxTime(Duration.ofMillis(3000));
        return mongoTemplate.findOne(query, Item.class);
    }


    public List<Item> findUserItems(Integer offset, Integer limit, Set<Category> categories) {
        Query query = new Query()
                .addCriteria(Criteria.where("creatorId").is(UserUtils.getUserId()))
                .skip((long) offset * limit)
                .limit(limit);

        if(categories != null && !categories.isEmpty()) {
            query.addCriteria(Criteria.where("itemDescription.categories").in(categories));
        }

        return mongoTemplate.find(query, Item.class);
    }

    public List<Item> findForUser(Integer offset, Integer limit) {
        Query query = new Query()
                .addCriteria(Criteria.where("creatorId").ne(UserUtils.getUserId()))
                .addCriteria(Criteria.where("shopliftId").isNull())
                .addCriteria(Criteria.where("isActive").is(true))
                .skip((long) offset * limit)
                .limit(limit);

        return mongoTemplate.find(query, Item.class);
    }

    public List<Item> findForUserDestination(Integer offset, Integer limit, Double latitude, Double longitude, Double distance) {
        Query query = genericItemQuery()
                .addCriteria(filterByGeolocation(latitude, longitude, distance))
                .skip((long) offset * limit)
                .limit(limit);
        return mongoTemplate.find(query, Item.class);
    }

    public Long countForLocationRemovingBlockedBy(Double latitude, Double longitude, Double distance) {
        Criteria criteria = removeBlockedByCreatorsCriteria();

        Query query = genericItemQuery()
                .addCriteria(filterByGeolocation(latitude, longitude, distance))
                .addCriteria(criteria);
        return mongoTemplate.count(query, Item.class);
    }

    public Criteria filterByGeolocation(Double latitude, Double longitude, Double distance) {
        return Criteria.where("itemDescription.destination.location")
                .nearSphere(new Point(longitude, latitude)).maxDistance(distance);
    }

    public Criteria removeBlockedByCreatorsCriteria() {
        return Criteria.where("creatorId").not().in(readUserService.findAllBlockedBy(UserUtils.getUserId()));
    }

    private Query genericItemQuery() {
        Criteria criteria = removeBlockedByCreatorsCriteria();
        return new Query()
                .addCriteria(Criteria.where("isPicked").is(false))
                .addCriteria(Criteria.where("isActive").is(true))
                .addCriteria(criteria);
    }
}
