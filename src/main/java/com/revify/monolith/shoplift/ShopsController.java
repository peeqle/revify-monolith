package com.revify.monolith.shoplift;

import com.revify.monolith.shoplift.model.Filter;
import com.revify.monolith.shoplift.model.Shop;
import com.revify.monolith.shoplift.model.Shoplift;
import com.revify.monolith.shoplift.repo.ShopRepository;
import com.revify.monolith.shoplift.service.ShopliftService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/shops")
@RequiredArgsConstructor
public class ShopsController {

    private final ShopliftService shopliftService;

    private final ShopRepository shopRepository;

    private final JdbcTemplate jdbcTemplate;

    @PostMapping("/find")
    public ResponseEntity<List<Shoplift>> findToDestination(@RequestBody Filter filter) {
        return ResponseEntity.ok(shopliftService.find(filter));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Shop>> searchShops(@RequestParam String query, @RequestParam Integer limit, @RequestParam Integer offset) {
        return ResponseEntity.ok(shopRepository.search(query, PageRequest.of(offset, limit)));
    }

    //courier request to join room for items

    //block and delete items from user
}
