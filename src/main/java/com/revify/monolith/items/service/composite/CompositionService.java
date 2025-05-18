package com.revify.monolith.items.service.composite;

import com.revify.monolith.items.model.item.composite.CompositeItem;
import com.revify.monolith.items.model.item.composite.CompositeItemUniteRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompositionService {

    private final CompositeItemUniteRequestService compositeItemUniteRequestService;

    private final CompositeItemService compositeItemService;

    private final MongoTemplate mongoTemplate;

    public CompositeItem acceptComposition(String compositeItemRequestId) {
        CompositeItemUniteRequest composeItemRequest = compositeItemUniteRequestService.getComposeItemRequest(new ObjectId(compositeItemRequestId));

        if (composeItemRequest == null) {
            throw new ResponseStatusException(NOT_FOUND, "Cannot find a request for composite item");
        }

        CompositeItem compositeItem = compositeItemService.findById(composeItemRequest.getItemId());
        if (compositeItem == null) {
            throw new ResponseStatusException(NOT_FOUND, "Cannot find a composite item");
        }

        Set<String> itemsInvolved = compositeItem.getItemsInvolved();
        itemsInvolved.add(composeItemRequest.getItemId());
        compositeItem.setItemsInvolved(itemsInvolved);

        return mongoTemplate.save(compositeItem);
    }
}
