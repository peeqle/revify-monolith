package com.revify.monolith.shoplift;

import com.revify.monolith.shoplift.model.Filter;
import com.revify.monolith.shoplift.model.Shop;
import com.revify.monolith.shoplift.model.Shoplift;
import com.revify.monolith.shoplift.model.dto.ShopDTO;
import com.revify.monolith.shoplift.model.dto.ShopliftDTO;
import com.revify.monolith.shoplift.model.req.Accept_Shoplift;
import com.revify.monolith.shoplift.model.req.Create_Shoplift;
import com.revify.monolith.shoplift.service.ShopliftService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/shoplift")
@RequiredArgsConstructor
public class ShopliftController {

    private final ShopliftService shopliftService;

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
    public ResponseEntity<Shoplift> createShoplift(@RequestBody Create_Shoplift shoplift) {
        return ResponseEntity.ok(shopliftService.createNew(shoplift));
    }

    @PostMapping("/find")
    public ResponseEntity<List<ShopliftDTO>> findToDestination(@RequestBody Filter filter) {
        return ResponseEntity.ok(shopliftService.find(filter).stream().map(e -> {

            List<ShopDTO> shopDTOs = new ArrayList<>();
            for (String shopId : e.getShopIds()) {
                Shop shop = shopliftService.findShop(shopId);
                shopDTOs.add(ShopDTO.from(shop));
            }
            return ShopliftDTO.from(e, shopDTOs);
        }).collect(Collectors.toList()));
    }

    @PostMapping("/accept")
    public void acceptItems(@RequestBody Accept_Shoplift acceptShoplift) {
        shopliftService.acceptShoplifting(acceptShoplift);
    }

    @PostMapping("/disable")
    public void disableShoplift(@RequestParam String shopliftId) {
        shopliftService.disable(shopliftId);
    }

    //courier request to join room for items

    //block and delete items from user
}
