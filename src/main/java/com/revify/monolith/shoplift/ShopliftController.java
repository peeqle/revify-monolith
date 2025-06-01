package com.revify.monolith.shoplift;

import com.revify.monolith.shoplift.model.Filter;
import com.revify.monolith.shoplift.model.Shoplift;
import com.revify.monolith.shoplift.model.req.Accept_Shoplift;
import com.revify.monolith.shoplift.model.req.Create_Shoplift;
import com.revify.monolith.shoplift.service.ShopliftService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shoplift")
@RequiredArgsConstructor
public class ShopliftController {

    private final ShopliftService shopliftService;

    @GetMapping("/filters")
    public ResponseEntity<Filter> getFilter() {
        return ResponseEntity.ok(new Filter());
    }

    @GetMapping("/find")
    public ResponseEntity<List<Shoplift>> findToDestination(@RequestBody Filter filter) {
        return ResponseEntity.ok(shopliftService.find(filter));
    }

    @PostMapping("/create")
    public ResponseEntity<Shoplift> createShoplift(@RequestBody Create_Shoplift shoplift) {
        return ResponseEntity.ok(shopliftService.createNew(shoplift));
    }

    @PostMapping("/accept")
    public void acceptItems(@RequestBody Accept_Shoplift acceptShoplift) {
        shopliftService.acceptShoplifting(acceptShoplift);
    }

    //courier request to join room for items

    //block and delete items from user

    //payment creation for items

    //limited time and expiry for items payments

    //split delivery on room? so one courier is buying and other can deliver the items? insane?
}
