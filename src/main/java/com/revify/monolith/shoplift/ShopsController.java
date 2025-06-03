package com.revify.monolith.shoplift;

import com.revify.monolith.shoplift.model.Shop;
import com.revify.monolith.shoplift.repo.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/shops")
@RequiredArgsConstructor
public class ShopsController {

    private final ShopRepository shopRepository;

    @GetMapping("/search")
    public ResponseEntity<List<Shop>> searchShops(@RequestParam String query, @RequestParam Integer limit, @RequestParam Integer offset) {
        return ResponseEntity.ok(shopRepository.search(query, PageRequest.of(offset, limit)));
    }

    //courier request to join room for items

    //block and delete items from user
}
