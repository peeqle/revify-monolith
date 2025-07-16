package com.revify.monolith.shoplift;

import com.revify.monolith.shoplift.model.Filter;
import com.revify.monolith.shoplift.model.Shop;
import com.revify.monolith.shoplift.model.Shoplift;
import com.revify.monolith.shoplift.model.ShopliftEvent;
import com.revify.monolith.shoplift.model.dto.ShopDTO;
import com.revify.monolith.shoplift.model.dto.ShopliftDTO;
import com.revify.monolith.shoplift.model.req.Accept_Shoplift;
import com.revify.monolith.shoplift.model.req.Create_Shoplift;
import com.revify.monolith.shoplift.service.ShopliftService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.revify.monolith.commons.messaging.WsQueues.SHOPLIFT_EVENTS;

@RestController
@RequestMapping("/shoplift")
@RequiredArgsConstructor
public class ShopliftController {

    private final ShopliftService shopliftService;

    private final SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping
    public ResponseEntity<ShopliftDTO> getShoplift(@RequestParam String shopliftId) {
        Shoplift shoplift = shopliftService.getById(shopliftId);
        List<ShopDTO> shopDTOs = new ArrayList<>();
        for (String shopId : shoplift.getShopIds()) {
            Shop shop = shopliftService.findShop(shopId);
            shopDTOs.add(ShopDTO.from(shop));
        }
        return ResponseEntity.ok(ShopliftDTO.from(shoplift, shopDTOs));
    }

    @PostMapping("/create")
    public ResponseEntity<ShopliftDTO> createShoplift(@RequestBody Create_Shoplift shoplift) {
        return ResponseEntity.ok(cook(shopliftService.createNew(shoplift)));
    }

    @PostMapping("/attach")
    public ResponseEntity<?> attachItem(@RequestParam("itemId") ObjectId itemId,
                                        @RequestParam("shopliftId") ObjectId shopliftId) {
        shopliftService.addShopliftItem(itemId, shopliftId);
        simpMessagingTemplate.convertAndSend(SHOPLIFT_EVENTS + shopliftId, ShopliftEvent.builder()
                .activeAt(Instant.now().toEpochMilli())
                .type(ShopliftEvent.ShopliftEventType.ITEMS_CHANGED)
                .build());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/find")
    public ResponseEntity<List<ShopliftDTO>> findToDestination(@RequestBody Filter filter) {
        return ResponseEntity.ok(shopliftService.find(filter).stream().map(this::cook).collect(Collectors.toList()));
    }

    /*
     * Create a delivery lap, with the selected items, pinned to the courier
     */
    @PostMapping("/accept")
    public void finishWithItems(@RequestBody Accept_Shoplift acceptShoplift) {
        shopliftService.acceptShoplifting(acceptShoplift);
    }

    @PostMapping("/disable")
    public void disableShoplift(@RequestParam("shopliftId") String shopliftId, @RequestParam("state") boolean state) {
        shopliftService.disable(shopliftId, state);
        if (!state) {
            simpMessagingTemplate.convertAndSend(SHOPLIFT_EVENTS + shopliftId, ShopliftEvent.builder()
                    .activeAt(Instant.now().toEpochMilli())
                    .type(ShopliftEvent.ShopliftEventType.FINISH)
                    .build());
        }
    }

    private ShopliftDTO cook(Shoplift shoplift) {
        List<ShopDTO> shopDTOs = new ArrayList<>();
        for (String shopId : shoplift.getShopIds()) {
            Shop shop = shopliftService.findShop(shopId);
            shopDTOs.add(ShopDTO.from(shop));
        }
        return ShopliftDTO.from(shoplift, shopDTOs);
    }
}
