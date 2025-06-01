package com.revify.monolith.finance.service;

import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.commons.finance.Price;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
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
                    BigDecimal bigDecimal = currencyService.convertTo(headPrice, base.getCurrency());
                    base.setAmount(base.getAmount().add(bigDecimal));
                }
                head = head.getNext();
            }
        }

        if (order.getIsShoplift()) {
            for (Long receiverId : order.getReceivers()) {
                createPayment(order, receiverId,
                        itemsInvolved.stream().filter(e -> e.getCreatorId().equals(receiverId)).toList(),
                        base);
            }
        }
    }

    //todo optimize for adding payment account after payment creation
    public void createPayment(Order order, Long receiver, List<Item> userItems, Price base) {
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
            payment.setOrderId(order.getId().toHexString());
            payment.setExecutionStatus(PaymentExecutionStatus.WAITING);
            payment.setCreatedAt(Instant.now().toEpochMilli());
            payment.setDescription("Payment for REVIFY order" + order.getId().toHexString());
            PaymentSystemAccount psa;

            if (paymentSystemAccount instanceof BePaidPaymentSystemAccount) {
                psa = new BePaidPaymentSystemAccount();
            } else {
                psa = new StripePaymentSystemAccount();
            }

            psa.setId(paymentSystemAccount.getId());
            payment.setAccount(psa);

            Payment save = paymentRepository.save(payment);

            delayProducer.sendPaymentExpirationMessage(save.getId().toString(), order.getPaymentsCutoff() == null ?
                    2 * 1000 * 60 * 60 * 24 : order.getPaymentsCutoff());
        }
    }


    public List<Payment> findForOrder(String orderId) {
        return paymentRepository.findByOrderId(orderId);

    }

    public List<Payment> getUserPayments(Integer offset, Integer limit) {
        return paymentRepository.findByAccountId(UserUtils.getUserId(), PageRequest.of(offset, limit));
    }
}
