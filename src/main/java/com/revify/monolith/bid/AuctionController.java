package com.revify.monolith.bid;


import com.revify.monolith.bid.service.AuctionService;
import com.revify.monolith.bid.service.ManagementService;
import com.revify.monolith.bid.util.AuctionUtils;
import com.revify.monolith.bid.util.BidUtils;
import com.revify.monolith.commons.bids.AuctionDTO;
import com.revify.monolith.commons.bids.BidDTO;
import com.revify.monolith.commons.models.bid.BidCreationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

@Slf4j
@RestController
@RequestMapping("/auction")
@RequiredArgsConstructor

@PreAuthorize("hasRole('ROLE_USER')")
public class AuctionController {

    private final ManagementService managementService;

    private final AuctionService auctionService;

    @PostMapping("/finish-auction")
    public Mono<Void> finishAuction(@RequestParam ObjectId auctionId, @RequestParam ObjectId selectedBid) {
        log.debug("Finishing auction");
        return managementService.finishAuction(auctionId, selectedBid);
    }

    @PostMapping("/close-auction")
    public Mono<AuctionDTO> closeAuction(@RequestParam ObjectId auctionId) {
        log.debug("Closing auction");
        return auctionService.closeAuction(auctionId)
                .transform(AuctionUtils::from);
    }

    @PostMapping("/archive-auction")
    public Mono<AuctionDTO> archiveAuction(@RequestParam ObjectId auctionId) {
        log.debug("Archiving auction");
        return auctionService.archiveAuction(auctionId)
                .transform(AuctionUtils::from);
    }

    @GetMapping("/all-for-item")
    public Flux<BidDTO> findAllBids(@RequestParam String itemId) {
        return managementService
                .findAllBids(Mono.just(itemId))
                .transform(BidUtils::from);
    }

    @GetMapping("/all-for-item-limited")
    public Flux<BidDTO> findAllBids(@RequestParam String itemId, @RequestParam Integer limit) {
        return managementService
                .findLastBids(Mono.just(itemId).zipWith(Mono.just(limit), Tuples::of))
                .transform(BidUtils::from);
    }
}
