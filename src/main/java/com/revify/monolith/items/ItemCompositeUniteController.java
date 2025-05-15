package com.revify.monolith.items;

import com.revify.monolith.items.model.item.composite.CompositeItem;
import com.revify.monolith.items.model.item.composite.CompositeItemUniteRequest;
import com.revify.monolith.items.service.composite.CompositeItemUniteRequestService;
import com.revify.monolith.items.service.composite.CompositionService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/item/composite-unite")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class ItemCompositeUniteController {

    private final CompositionService compositionService;

    private final CompositeItemUniteRequestService compositeItemUniteRequestService;

    @GetMapping("/for-item")
    public Flux<CompositeItemUniteRequest> findForItem(@RequestParam("itemId") String itemId) {
        return compositeItemUniteRequestService.findAllForItem(itemId);
    }

    @PostMapping("/accept")
    public Mono<CompositeItem> acceptComposition(@RequestParam("compositeItemRequestId") String compositeItemRequestId) {
        if (!ObjectId.isValid(compositeItemRequestId)) {
            return Mono.error(new IllegalArgumentException("Invalid compositeItemRequestId"));
        }
        return compositionService.acceptComposition(compositeItemRequestId);
    }

    /*
    create request to composite item instance creator
     */
    @PostMapping("/create-compose-request")
    public Mono<CompositeItemUniteRequest> createCompositionRequest(@RequestParam("itemId") String itemId, @RequestParam("compositeItemId") String compositeItemId) {
        return compositeItemUniteRequestService.createComposeItemRequest(itemId, compositeItemId);
    }

    @DeleteMapping("/delete-compose-request")
    public Mono<Boolean> deleteCompositionRequest(@RequestParam("compositeItemId") String compositeItemId) {
        if (!ObjectId.isValid(compositeItemId)) {
            return Mono.error(new IllegalArgumentException("Invalid compositeItemId"));
        }
        return compositeItemUniteRequestService.deleteComposeItemRequest(new ObjectId(compositeItemId));
    }
}
