package com.revify.monolith.items;

import com.revify.monolith.commons.items.ItemCreationDTO;
import com.revify.monolith.commons.items.ItemDTO;
import com.revify.monolith.commons.items.ItemUpdatesDTO;
import com.revify.monolith.items.model.item.Item;
import com.revify.monolith.items.service.item.ItemReadService;
import com.revify.monolith.items.service.item.ItemService;
import com.revify.monolith.items.service.item.ItemWriteService;
import com.revify.monolith.items.utils.ItemUtils;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<ItemDTO> createItem(@RequestBody ItemCreationDTO creationDTO) {
        Item item = itemWriteService.createItem(creationDTO);
        if (item != null) {
            return ResponseEntity.ok(ItemDTO.from(item));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PatchMapping("/change")
    public ResponseEntity<ItemDTO> changeItem(@RequestBody ItemUpdatesDTO updates) {
        Item item = itemService.updateItem(updates);
        if (item != null) {
            return ResponseEntity.ok(ItemDTO.from(item));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDTO> getItem(@PathVariable ObjectId itemId) {
        Item byId = itemReadService.findById(itemId);
        if (byId != null) {
            return ResponseEntity.ok(ItemDTO.from(byId));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/many")
    public ResponseEntity<List<ItemDTO>> getItems(@RequestBody List<String> itemIds) {
        List<Item> byId = itemReadService.findForIds(itemIds);
        if (byId != null && !byId.isEmpty()) {
            return ResponseEntity.ok(byId.stream().map(ItemDTO::from).toList());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/toggle-active")
    public ResponseEntity<ItemDTO> toggleActiveStatus(@RequestParam ObjectId itemId, @RequestParam boolean active) {
        Item item = itemWriteService.deactivateItem(itemId, active);
        if (item != null) {
            return ResponseEntity.ok(ItemUtils.from(item));
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/fetch-paged")
    public ResponseEntity<List<ItemDTO>> fetchItemsPageForCurrentUser(@RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                                                      @RequestParam(value = "offset", defaultValue = "0") Integer offset) {
        return ResponseEntity.ok(itemReadService.findUserItems(offset, limit)
                .stream().map(ItemDTO::from).toList());
    }

    @GetMapping("/fetch-items-paged")
    public ResponseEntity<List<ItemDTO>> fetchItemsPage(@RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                                        @RequestParam(value = "offset", defaultValue = "0") Integer offset) {
        return ResponseEntity.ok(itemReadService.findForUser(offset, limit)
                .stream().map(ItemDTO::from).toList());
    }

    @GetMapping("/fetch-geo-paged")
    public ResponseEntity<List<ItemDTO>> fetchItemsPageWithGeolocation(
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude,
            @RequestParam(value = "distance", defaultValue = "1000", required = false) Double distance,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit,
            @RequestParam(value = "offset", defaultValue = "0") Integer offset) {
        return ResponseEntity.ok(itemReadService.findForUserDestination(offset, limit, latitude, longitude, distance)
                .stream().map(ItemDTO::from).toList());
    }

    @GetMapping("/count-for-location")
    public ResponseEntity<Long> countForLocationBlockingUser(
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude,
            @RequestParam(value = "distance", defaultValue = "1000", required = false) Double distance
    ) {
        return ResponseEntity.ok(itemReadService.countForLocationRemovingBlockedBy(latitude, longitude, distance));
    }
}
