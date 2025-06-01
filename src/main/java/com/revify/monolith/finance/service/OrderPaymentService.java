package com.revify.monolith.finance.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.commons.finance.Price;
import com.revify.monolith.config.messaging.RabbitMqEndpointsConfiguration;
import com.revify.monolith.currency_reader.service.CurrencyService;
import com.revify.monolith.finance.messaging.DelayProducer;
import com.revify.monolith.finance.model.addons.PaymentExecutionStatus;
import com.revify.monolith.finance.model.jpa.BePaidPaymentSystemAccount;
import com.revify.monolith.finance.model.jpa.PaymentSystemAccount;
import com.revify.monolith.finance.model.jpa.StripePaymentSystemAccount;
import com.revify.monolith.finance.model.jpa.payment.Payment;
import com.revify.monolith.finance.service.repository.PaymentRepository;
import com.revify.monolith.items.model.item.Item;
import com.revify.monolith.items.service.item.ItemReadService;
import com.revify.monolith.orders.models.Order;
import com.revify.monolith.orders.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderPaymentService {
    private final RecipientService recipientService;

    private final PaymentRepository paymentRepository;

    private final ItemReadService itemReadService;

    private final CurrencyService currencyService;

    private final DelayProducer delayProducer;

    private final Gson gson = new GsonBuilder().create();
    private final OrderService orderService;

    @RabbitListener(queues = RabbitMqEndpointsConfiguration.PAYMENT_EXPIRATION)
    public void handlePaymentExpiration(String messagePayload, Message message) {
        Long receivedDelay = message.getMessageProperties().getReceivedDelayLong();
        log.debug("Received expired payment message: " + messagePayload);
        log.debug("Original delay: " + receivedDelay + "ms");
        log.debug("Received at: " + Instant.now());

        TypeToken<Map<String, String>> type = new TypeToken<>() {
        };
        Map<String, String> stringStringMap = gson.fromJson(messagePayload, type);
        if (stringStringMap != null && stringStringMap.containsKey("paymentId")) {
            try {
                Payment payment = paymentRepository.getReferenceById(UUID.fromString(stringStringMap.get("paymentId")));
                if (!payment.getExecutionStatus().equals(PaymentExecutionStatus.EXECUTED)) {
                    PaymentSystemAccount account = payment.getAccount();

                    Order order = orderService.findOrderById(payment.getOrderId());
                    order.setItems(order.getItems().stream().filter(e -> payment.getItems().contains(e)).collect(Collectors.toSet()));
                    order.setReceivers(order.getReceivers().stream().filter(e -> Objects.equals(account.getSystemUserId(), e)).collect(Collectors.toSet()));

                }
            } catch (EntityNotFoundException e) {
                log.error("Payment with id " + stringStringMap.get("paymentId") + " not found");
            }

        }
    }

    public void processPayment(Order order) {
        List<Item> itemsInvolved = itemReadService.findForIds(order.getItems());
        if (itemsInvolved.isEmpty()) {
            throw new RuntimeException("No items involved");
        }

        var base = itemsInvolved.getFirst().getPrice();
        {
            base.setAmount(BigDecimal.ZERO);
            var head = order.getShipmentParticle();
            while (head != null && head.getNext() != null) {
                Price headPrice = head.getPrice();
                if (!base.getCurrency().equals(headPrice.getCurrency())) {
                    BigDecimal bigDecimal = currencyService.convertTo(headPrice.getCurrency().getName(), base.getCurrency().getName(), headPrice.getAmount().doubleValue());
                    base.setAmount(base.getAmount().add(bigDecimal));
                }
                head = head.getNext();
            }
        }

        if (order.getIsShoplift()) {
            for (Long receiverId : order.getReceivers()) {
                createPayment(order.getId().toHexString(),
                        receiverId,
                        itemsInvolved.stream().filter(e -> e.getCreatorId().equals(receiverId)).toList(),
                        base);
            }
        }
    }

    //todo optimize for adding payment account after payment creation
    public void createPayment(String orderId, Long receiver, List<Item> userItems, Price base) {
        List<PaymentSystemAccount> paymentSystemAccounts = recipientService.fetchForUser(receiver);
        if (paymentSystemAccounts.isEmpty()) {
            return;
        }
        for (PaymentSystemAccount paymentSystemAccount : paymentSystemAccounts) {
            Payment payment = new Payment();
            {
                for (Item item : userItems) {
                    var itemPrice = item.getPrice();
                    if (!itemPrice.getCurrency().equals(base.getCurrency())) {
                        base.setAmount(base.getAmount().add(currencyService.convertTo(itemPrice, base.getCurrency())));
                    } else {
                        base.setAmount(base.getAmount().add(itemPrice.getAmount()));
                    }
                }
                payment.setItems(userItems.stream().map(Item::getId).map(ObjectId::toHexString).collect(Collectors.toList()));
                payment.setPrice(base);
            }
            payment.setOrderId(orderId);
            payment.setExecutionStatus(PaymentExecutionStatus.WAITING);
            payment.setCreatedAt(Instant.now().toEpochMilli());
            payment.setDescription("Payment for REVIFY order" + orderId);
            PaymentSystemAccount psa;

            if (paymentSystemAccount instanceof BePaidPaymentSystemAccount) {
                psa = new BePaidPaymentSystemAccount();
            } else {
                psa = new StripePaymentSystemAccount();
            }

            psa.setId(paymentSystemAccount.getId());
            payment.setAccount(psa);

            Payment save = paymentRepository.save(payment);

            delayProducer.sendPaymentExpirationMessage(save.getId().toString(),
                    Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli() - Instant.now().toEpochMilli());
        }
    }

    public List<Payment> getUserPayments(Integer offset, Integer limit) {
        return paymentRepository.findByAccountId(UserUtils.getUserId(), PageRequest.of(offset, limit));
    }
}
