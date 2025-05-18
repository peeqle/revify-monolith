package com.revify.monolith.items;

import com.revify.monolith.items.model.item.composite.CompositeItem;
import com.revify.monolith.items.service.composite.CompositeItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/item/composite")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class ItemCompositeController {

    private final CompositeItemService compositeItemService;

    @GetMapping
    public ResponseEntity<CompositeItem> fetchCompositeItemInstance(@RequestParam("compositeItemId") String compositeItemId) {
        CompositeItem compositeItem = compositeItemService.findById(compositeItemId);
        if (compositeItem == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(compositeItem);
    }

    @PostMapping("/create-from-item")
    public ResponseEntity<CompositeItem> createCompositeItemInstance(@RequestParam("initialItem") String itemId) {
        CompositeItem compositeItem = compositeItemService.createCompositeInstance(itemId);
        if (compositeItem == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(compositeItem);
    }

    @PatchMapping
    public ResponseEntity<CompositeItem> updateCompositeItemInstance(@RequestParam("initialItem") String itemId, @RequestBody Map<String, Object> updates) {
        if (itemId == null || updates == null) return ResponseEntity.badRequest().build();

        CompositeItem compositeItem = compositeItemService.updateCompositeItem(itemId, updates);
        if (compositeItem == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(compositeItem);
    }

    @DeleteMapping
    public ResponseEntity<Boolean> deleteCompositeItemInstance(@RequestParam("compositeItemId") String compositeItemId) {
        return ResponseEntity.ok(compositeItemService.deleteCompositeInstance(compositeItemId));
    }
}
