package com.revify.monolith.finance.service;

import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.commons.finance.Price;
import com.revify.monolith.currency_reader.service.CurrencyService;
import com.revify.monolith.finance.model.addons.PaymentExecutionStatus;
import com.revify.monolith.finance.model.jpa.PaymentSystemAccount;
import com.revify.monolith.finance.model.jpa.payment.Payment;
import com.revify.monolith.finance.service.repository.PaymentRepository;
import com.revify.monolith.items.model.item.Item;
import com.revify.monolith.items.service.item.ItemReadService;
import com.revify.monolith.items.service.item.ItemService;
import com.revify.monolith.orders.models.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderPaymentService {
    private final RecipientService recipientService;

    private final PaymentRepository paymentRepository;

    private final ItemReadService itemReadService;

    private final CurrencyService currencyService;

    public void createPayment(Order order) {
        List<PaymentSystemAccount> paymentSystemAccounts = recipientService.fetchForUser(order.getReceiverId());
        if (paymentSystemAccounts.isEmpty()) {
            return;
        }
        for (PaymentSystemAccount paymentSystemAccount : paymentSystemAccounts) {
            Payment payment = new Payment();
            {
                Item byId = itemReadService.findById(order.getItemId());
                var base = byId.getPrice();
                var head = order.getShipmentParticle();
                while(head != null && head.getNext() != null) {
                    Price headPrice = head.getPrice();
                    if (!base.getCurrency().equals(headPrice.getCurrency())) {
                        BigDecimal bigDecimal = currencyService.convertTo(headPrice.getCurrency().getName(), base.getCurrency().getName(), headPrice.getAmount().doubleValue());
                        base.setAmount(base.getAmount().add(bigDecimal));
                    }
                    head = head.getNext();
                }
                payment.setPrice(base);
            }
            payment.setOrderId(order.getId().toHexString());
            payment.setExecutionStatus(PaymentExecutionStatus.WAITING);
            payment.setCreatedAt(Instant.now().toEpochMilli());
            payment.setDescription("Payment for REVIFY order" + order.getId().toHexString());

            PaymentSystemAccount psa = new PaymentSystemAccount();
            psa.setId(paymentSystemAccount.getId());
            payment.setAccount(psa);

            paymentRepository.save(payment);
        }
    }

    public List<Payment> getUserPayments(Integer offset, Integer limit) {
        return paymentRepository.findByAccountId(UserUtils.getUserId(), PageRequest.of(offset, limit));
    }
}
