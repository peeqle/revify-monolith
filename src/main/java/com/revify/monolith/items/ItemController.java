package com.revify.monolith.items;

import com.revify.monolith.commons.items.ItemCreationDTO;
import com.revify.monolith.commons.items.ItemDTO;
import com.revify.monolith.commons.items.ItemUpdatesDTO;
import com.revify.monolith.items.service.item.ItemReadService;
import com.revify.monolith.items.service.item.ItemService;
import com.revify.monolith.items.service.item.ItemWriteService;
import com.revify.monolith.items.utils.ItemUtils;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/item")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")

@Consumes(MediaType.APPLICATION_JSON)
public class ItemController {

    private final ItemWriteService itemWriteService;

    private final ItemReadService itemReadService;

    private final ItemService itemService;

    @PostMapping("/create")
    public Mono<ItemDTO> createItem(@RequestBody ItemCreationDTO creationDTO) {
        return itemWriteService.createItem(creationDTO)
                .mapNotNull(ItemUtils::from);
    }

    @PatchMapping("/change")
    public Mono<ItemDTO> changeItem(@RequestBody ItemUpdatesDTO updates) {
        return itemService.updateItem(updates)
                .mapNotNull(ItemUtils::from);
    }

    @GetMapping("/{itemId}")
    public Mono<ResponseEntity<ItemDTO>> getItem(@PathVariable ObjectId itemId) {
        return itemReadService.findById(itemId)
                .map(item -> ResponseEntity.ok(ItemUtils.from(item)))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .onErrorResume(e -> {
                    log.error("Error fetching item with id: {}", itemId, e);
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    @PostMapping("/toggle-active")
    public Mono<ItemDTO> toggleActiveStatus(@RequestParam ObjectId itemId, @RequestParam boolean active) {
        return itemWriteService.deactivateItem(itemId, active)
                .map(ItemUtils::from);
    }

    @GetMapping("/fetch-paged")
    public Flux<ItemDTO> fetchItemsPageForCurrentUser(@RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                        @RequestParam(value = "offset", defaultValue = "0") Integer offset) {
        return itemReadService.findUserItems(offset, limit)
                .mapNotNull(ItemUtils::from);
    }

    @GetMapping("/fetch-items-paged")
    public Flux<ItemDTO> fetchItemsPage(@RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                        @RequestParam(value = "offset", defaultValue = "0") Integer offset) {
        return itemReadService.findForUser(offset, limit)
                .mapNotNull(ItemUtils::from);
    }

    @GetMapping("/fetch-geo-paged")
    public Flux<ItemDTO> fetchItemsPageWithGeolocation(
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude,
            @RequestParam(value = "distance", defaultValue = "1000", required = false) Double distance,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit,
            @RequestParam(value = "offset", defaultValue = "0") Integer offset) {
        return itemReadService.findForUserDestination(offset, limit, latitude, longitude, distance)
                .mapNotNull(ItemUtils::from);
    }

    @GetMapping("/count-for-location")
    public Mono<Long> countForLocationBlockingUser(
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude,
            @RequestParam(value = "distance", defaultValue = "1000", required = false) Double distance
    ) {
        return itemReadService.countForLocationRemovingBlockedBy(latitude, longitude, distance);
    }
}
