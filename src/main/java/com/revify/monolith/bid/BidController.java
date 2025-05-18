package com.revify.monolith.bid;


import com.revify.monolith.bid.models.Bid;
import com.revify.monolith.bid.service.ManagementService;
import com.revify.monolith.commons.bids.BidDTO;
import com.revify.monolith.commons.models.bid.BidCreationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/bids")
@RequiredArgsConstructor

@PreAuthorize("hasRole('ROLE_USER')")
public class BidController {

    private final ManagementService managementService;

    @PostMapping("/place")
    public ResponseEntity<BidDTO> placeBid(@Validated @RequestBody BidCreationRequest bidCreationRequest) {
        log.debug("Placing bid for item {}", bidCreationRequest.getItemId());

        Bid bid = managementService.createBid(bidCreationRequest);
        return ResponseEntity.ok(BidDTO.from(bid));
    }

    @GetMapping("/all-for-item")
    public List<BidDTO> findAllBids(@RequestParam("itemId") String itemId) {
        return managementService.findAllBids(itemId)
                .stream().map(BidDTO::from).collect(Collectors.toList());
    }

    @GetMapping("/all-for-item-limited")
    public List<BidDTO> findAllBids(@RequestParam String itemId, @RequestParam Integer limit) {
        return managementService.findLastBids(itemId, limit)
                .stream().map(BidDTO::from).collect(Collectors.toList());
    }
}
