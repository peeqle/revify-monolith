package com.revify.monolith.bid;


import com.revify.monolith.bid.service.ManagementService;
import com.revify.monolith.bid.service.SelfAuctionBidException;
import com.revify.monolith.bid.util.BidUtils;
import com.revify.monolith.commons.bids.BidDTO;
import com.revify.monolith.commons.models.bid.BidCreationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
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
    public Mono<BidResponse> placeBid(@Validated @RequestBody BidCreationRequest bidCreationRequest) {
        log.debug("Placing bid for item {}", bidCreationRequest.getItemId());

        return managementService
                .createBid(bidCreationRequest)
                .map(bid -> {
                    BidDTO dto = BidUtils.from(bid);
                    return new BidResponse(dto);
                })
                .onErrorResume(SelfAuctionBidException.class, e ->
                        Mono.just(new BidResponse(null, HttpStatus.BAD_REQUEST, e.getMessage())))
                .onErrorResume(ResponseStatusException.class, e ->
                        Mono.just(new BidResponse(null, HttpStatus.BAD_REQUEST, e.getMessage())))
                .onErrorResume(e ->
                        Mono.just(new BidResponse(null, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error")));
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

    public record BidResponse(Object entity, HttpStatus status, String errorMessage) {
        public BidResponse(Object entity) {
            this(entity, null, "");
        }
    }

    ;
}
