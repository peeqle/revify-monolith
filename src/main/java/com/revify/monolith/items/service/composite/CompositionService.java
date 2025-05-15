package com.revify.monolith.items.service.composite;

import com.revify.monolith.items.model.item.composite.CompositeItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompositionService {

    private final CompositeItemUniteRequestService compositeItemUniteRequestService;

    private final CompositeItemService compositeItemService;

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<CompositeItem> acceptComposition(String compositeItemRequestId) {
        return compositeItemUniteRequestService.getComposeItemRequest(new ObjectId(compositeItemRequestId))
                .flatMap(request ->
                        compositeItemService.findById(request.getCompositeItemId())
                                .flatMap(compositeItem -> {
                                    Set<String> itemsInvolved = compositeItem.getItemsInvolved();
                                    itemsInvolved.add(request.getItemId());
                                    compositeItem.setItemsInvolved(itemsInvolved);

                                    return reactiveMongoTemplate.save(compositeItem)
                                            .doOnSuccess(updatedComposite -> log.debug("Updated composite item: {}", updatedComposite.getId()));
                                })
                );
    }
}
