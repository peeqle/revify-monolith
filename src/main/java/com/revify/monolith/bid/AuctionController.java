package com.revify.monolith.bid;


import com.revify.monolith.bid.models.Auction;
import com.revify.monolith.bid.service.AuctionService;
import com.revify.monolith.bid.service.ManagementService;
import com.revify.monolith.commons.bids.AuctionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auction")
@RequiredArgsConstructor

@PreAuthorize("hasRole('ROLE_USER')")
public class AuctionController {

    private final ManagementService managementService;

    private final AuctionService auctionService;

    @GetMapping
    public ResponseEntity<AuctionDTO> getAuction(@RequestParam("auctionId") ObjectId id) {
        Auction auction = auctionService.findAuction(id);
        if (auction == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(AuctionDTO.from(auction));
    }

    @GetMapping("/auction-for-item")
    public ResponseEntity<AuctionDTO> getAuctionForItem(@RequestParam("itemId") String id) {
        Auction auction = auctionService.findAuctionByItemUserAndStatus(id, true);
        if (auction == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(AuctionDTO.from(auction));
    }

    @PostMapping("/finish-auction")
    public ResponseEntity<?> finishAuction(@RequestParam("auctionId") ObjectId auctionId, @RequestParam("selectedBidId") ObjectId selectedBid) {
        log.debug("Finishing auction");
        if (!auctionService.isAuctionCreatedByUser(auctionId)) {
            return ResponseEntity.badRequest().body("You cannot finish that auction");
        }

        Auction auction = managementService.finishAuction(auctionId, selectedBid);
        if (auction == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(AuctionDTO.from(auction));
    }

    @PostMapping("/close-auction")
    public ResponseEntity<AuctionDTO> closeAuction(@RequestParam ObjectId auctionId) {
        log.debug("Closing auction");
        Auction auction = auctionService.closeAuction(auctionId);
        if (auction == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(AuctionDTO.from(auction));
    }

    @PostMapping("/archive-auction")
    public ResponseEntity<AuctionDTO> archiveAuction(@RequestParam ObjectId auctionId) {
        log.debug("Archiving auction");
        Auction auction = auctionService.archiveAuction(auctionId);
        if (auction == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(AuctionDTO.from(auction));
    }
}
