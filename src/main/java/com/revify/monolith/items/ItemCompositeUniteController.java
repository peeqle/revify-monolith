package com.revify.monolith.items;

import com.revify.monolith.items.model.item.composite.CompositeItem;
import com.revify.monolith.items.model.item.composite.CompositeItemUniteRequest;
import com.revify.monolith.items.service.composite.CompositeItemUniteRequestService;
import com.revify.monolith.items.service.composite.CompositionService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/item/composite-unite")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class ItemCompositeUniteController {

    private final CompositionService compositionService;

    private final CompositeItemUniteRequestService compositeItemUniteRequestService;

    @GetMapping("/for-item")
    public ResponseEntity<List<CompositeItemUniteRequest>> findForItem(@RequestParam("itemId") String itemId) {
        return ResponseEntity.ok(compositeItemUniteRequestService.findAllForItem(itemId));
    }

    @PostMapping("/accept")
    public ResponseEntity<CompositeItem> acceptComposition(@RequestParam("compositeItemRequestId") String compositeItemRequestId) {
        if (!ObjectId.isValid(compositeItemRequestId)) {
            return ResponseEntity.badRequest().build();
        }
        CompositeItem compositeItem = compositionService.acceptComposition(compositeItemRequestId);
        if (compositeItem == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(compositeItem);
    }

    /*
    create request to composite item instance creator
     */
    @PostMapping("/create-compose-request")
    public ResponseEntity<CompositeItemUniteRequest> createCompositionRequest(@RequestParam("itemId") String itemId, @RequestParam("compositeItemId") String compositeItemId) {
        CompositeItemUniteRequest composeItemRequest = compositeItemUniteRequestService.createComposeItemRequest(itemId, compositeItemId);
        if (composeItemRequest == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(composeItemRequest);
    }

    @DeleteMapping("/delete-compose-request")
    public ResponseEntity<Boolean> deleteCompositionRequest(@RequestParam("compositeItemId") String compositeItemId) {
        if (!ObjectId.isValid(compositeItemId)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(compositeItemUniteRequestService.deleteComposeItemRequest(new ObjectId(compositeItemId)));
    }
}
