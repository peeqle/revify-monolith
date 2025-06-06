package com.revify.monolith.items.service.composite;

import com.revify.monolith.items.model.item.composite.CompositeItemUniteRequest;
import com.revify.monolith.items.utils.CompositeItemUniteRequestCriteriaUtil;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;


@Service
@RequiredArgsConstructor
public class CompositeItemUniteRequestService {

    private final MongoTemplate mongoTemplate;

    public CompositeItemUniteRequest createComposeItemRequest(String itemId, String compositeItemId) {

        Query query = Query.query(CompositeItemUniteRequestCriteriaUtil.containsItemId(itemId))
                .addCriteria(CompositeItemUniteRequestCriteriaUtil.hasCompositeItemId(compositeItemId));

        boolean exists = mongoTemplate.exists(query, CompositeItemUniteRequest.class);
        if (exists) {
            throw new ResponseStatusException(BAD_REQUEST, "Composite item unite request for provided items already exists");
        }

        CompositeItemUniteRequest compositeItemUniteRequest = new CompositeItemUniteRequest();
        compositeItemUniteRequest.setItemId(itemId);
        compositeItemUniteRequest.setCompositeItemId(compositeItemId);

        return mongoTemplate.save(compositeItemUniteRequest);
    }

    public List<CompositeItemUniteRequest> findAllForItem(String itemId) {
        Query query = Query.query(CompositeItemUniteRequestCriteriaUtil.containsItemId(itemId));
        return mongoTemplate.find(query, CompositeItemUniteRequest.class);
    }

    public CompositeItemUniteRequest getComposeItemRequest(ObjectId uniteId) {
        Query query = Query.query(CompositeItemUniteRequestCriteriaUtil.hasId(uniteId));

        return mongoTemplate.findOne(query, CompositeItemUniteRequest.class);
    }

    public Boolean deleteComposeItemRequest(ObjectId uniteId) {
        Query query = Query.query(CompositeItemUniteRequestCriteriaUtil.hasId(uniteId));

        return mongoTemplate.remove(query, CompositeItemUniteRequest.class).getDeletedCount() > 0;
    }
}
