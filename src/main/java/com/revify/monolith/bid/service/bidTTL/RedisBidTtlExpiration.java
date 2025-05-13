package com.revify.monolith.bid.service.bidTTL;

import com.revify.monolith.bid.messaging.BillingProducer;
import com.revify.monolith.bid.service.AuctionService;
import com.revify.monolith.bid.service.ManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisBidTtlExpiration {

    private final BillingProducer billingProducer;

    private final AuctionService auctionService;

    private final ManagementService managementService;

    private final RedisExpirationListener expirationListener;

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        expirationListener.listenToExpirationEvents(expiredKey -> {
            System.out.println("Key expired: " + expiredKey);

            handleExpiredKey(expiredKey);
        });
    }

    private void handleExpiredKey(String expiredKey) {
        auctionService.findAuction(expiredKey)
                .subscribe(e -> managementService
                        .findTerminatingBidForItem(e.getId())
                        .subscribe(model -> billingProducer.createBill(model, e))
                );
    }
}