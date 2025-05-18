package com.revify.monolith.bid.service.bidTTL;

import com.revify.monolith.bid.messaging.BillingProducer;
import com.revify.monolith.bid.models.Auction;
import com.revify.monolith.bid.models.Bid;
import com.revify.monolith.bid.service.AuctionService;
import com.revify.monolith.bid.service.ManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisBidTtlExpiration {

    private final BillingProducer billingProducer;

    private final AuctionService auctionService;

    private final ManagementService managementService;

    private final RedisExpirationListener expirationListener;

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        expirationListener.listenToExpirationEvents(this::handleExpiredKey);
    }

    private void handleExpiredKey(Message expiredAuctionMessage) {
        final String auctionId = new String(expiredAuctionMessage.getBody());
        System.out.println("Key expired: " + auctionId);
        Auction auction = auctionService.findAuction(auctionId);

        if(auction == null) {
            log.warn("Key not found: {}", auctionId);
            return;
        }
        if (!ObjectId.isValid(auctionId)) {
            log.warn("Key is invalid: {}", auctionId);
            return;
        }
        auctionService.closeAuction(new ObjectId(auctionId));

        Bid terminatingBidForItem = managementService.findTerminatingBidForItem(new ObjectId(auctionId));
        billingProducer.createBill(terminatingBidForItem, auction);
    }
}