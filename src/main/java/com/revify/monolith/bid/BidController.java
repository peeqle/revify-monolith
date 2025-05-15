package com.revify.monolith.bid;


import com.revify.monolith.bid.service.ManagementService;
import com.revify.monolith.bid.util.BidUtils;
import com.revify.monolith.commons.bids.BidDTO;
import com.revify.monolith.commons.models.bid.BidCreationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

@Slf4j
@RestController
@RequestMapping("/bids")
@RequiredArgsConstructor

@PreAuthorize("hasRole('ROLE_USER')")
public class BidController {

    private final ManagementService managementService;

    @PostMapping("/place")
    public Mono<BidDTO> placeBid(@Validated @RequestBody Mono<BidCreationRequest> bidCreationRequest) {
        log.debug("Placing bid for item");
        return managementService
                .createBid(bidCreationRequest)
                .transform(BidUtils::from);
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
