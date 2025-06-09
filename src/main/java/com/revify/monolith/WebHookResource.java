package com.revify.monolith;

import com.revify.monolith.finance.service.OrderPaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Context;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/hook")
@RequiredArgsConstructor
public class WebHookResource {

    private final OrderPaymentService orderPaymentService;

    private static final String WEBHOOK_SECRET = "whsec_7cf23a2861d88b642171edbe80de995c6d01773182666d7a882e7d105dda4538";

    @PostMapping("/stripe")
    public ResponseEntity<?> handleStripe(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {
        String payload = new BufferedReader(request.getReader())
                .lines()
                .collect(Collectors.joining("\n"));
        String sigHeader = request.getHeader("Stripe-Signature");

        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, WEBHOOK_SECRET);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        handleVerifiedEvent(event);

        return ResponseEntity.ok().build();
    }

    private void handleVerifiedEvent(Event event) {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();

        if (!dataObjectDeserializer.getObject().isPresent()) {
            log.debug("⚠️  Deserialization failed, possibly due to API version mismatch.");
            return;
        }

        switch (event.getType()) {
            case "payment_intent.succeeded":
                handlePaymentIntentSucceeded(dataObjectDeserializer);
                break;
            case "payment_method.attached":
                handlePaymentMethodAttached(dataObjectDeserializer);
                break;
            // ... handle other event types
            default:
                log.debug("Unhandled event type: " + event.getType());
        }
    }

    private void handlePaymentIntentSucceeded(EventDataObjectDeserializer dataObjectDeserializer) {
        PaymentIntent paymentIntent = (PaymentIntent) dataObjectDeserializer.getObject().get();

        if (paymentIntent.getId() != null && paymentIntent.getStatus() != null) {
            log.debug("✅ Verified PaymentIntent structure");
            processPaymentIntent(paymentIntent);
        } else {
            log.debug("⚠️  Unexpected PaymentIntent structure");
        }
    }

    private void handlePaymentMethodAttached(EventDataObjectDeserializer dataObjectDeserializer) {
        PaymentMethod paymentMethod = (PaymentMethod) dataObjectDeserializer.getObject().get();

        if (paymentMethod.getId() != null && paymentMethod.getType() != null) {
            log.debug("✅ Verified PaymentMethod structure");
            if ("card".equals(paymentMethod.getType()) && paymentMethod.getCard() != null && paymentMethod.getCard().getLast4() != null) {
                log.debug("✅ Verified card PaymentMethod details");
                processPaymentMethod(paymentMethod);
            } else {
                log.debug("⚠️  Unexpected PaymentMethod details");
            }
        } else {
            log.debug("⚠️  Unexpected PaymentMethod structure");
        }
    }

    private void processPaymentIntent(PaymentIntent paymentIntent) {
        String idempotencyKey = paymentIntent.getId();
        // Check if this payment_intent has been processed before
        if (alreadyProcessed(idempotencyKey)) {
            return;
        }

        markAsProcessed(idempotencyKey);
        orderPaymentService.changePaymentStatus(paymentIntent);
    }

    private void processPaymentMethod(PaymentMethod paymentMethod) {
        // Process the payment method
        // ...
    }

    private boolean alreadyProcessed(String idempotencyKey) {
        // Implement idempotency check logic
        return false;
    }

    private void markAsProcessed(String idempotencyKey) {
        // Implement logic to mark as processed
    }
}
