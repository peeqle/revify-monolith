package com.revify.monolith.items;

import com.revify.monolith.items.model.item.composite.CompositeItem;
import com.revify.monolith.items.service.composite.CompositeItemService;
import lombok.RequiredArgsConstructor;
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
    public Mono<CompositeItem> fetchCompositeItemInstance(@RequestParam("compositeItemId") String compositeItemId) {
        return compositeItemService.findById(compositeItemId);
    }

    @PostMapping("/create-from-item")
    public Mono<CompositeItem> createCompositeItemInstance(@RequestParam("initialItem") String itemId) {
        return compositeItemService.createCompositeInstance(itemId);
    }

    @PatchMapping
    public Mono<CompositeItem> updateCompositeItemInstance(@RequestParam("initialItem") String itemId, @RequestBody Map<String, Object> updates) {
        if (itemId == null || updates == null) return Mono.empty();

        return compositeItemService.updateCompositeItem(itemId, updates);
    }

    @DeleteMapping
    public Mono<Boolean> deleteCompositeItemInstance(@RequestParam("compositeItemId") String compositeItemId) {
        return compositeItemService.deleteCompositeInstance(compositeItemId);
    }
}
