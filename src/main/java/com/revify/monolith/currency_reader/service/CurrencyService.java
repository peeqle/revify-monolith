package com.revify.monolith.currency_reader.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.revify.monolith.commons.finance.Price;
import com.revify.monolith.currency_reader.data.CurrencySnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private static final String OPS_KEY = "CURRENCY";

    private static final String BASE_FIAT = "EUR";

    @Autowired
    @Qualifier("bidServiceRedisTemplate")
    private ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    private final ThreadLocal<Gson> gsonThreadLocal = ThreadLocal.withInitial(() -> new GsonBuilder().create());

    public Mono<CurrencySnapshot> findCurrency(Mono<String> literal) {
        return literal
                .flatMap(e -> reactiveRedisTemplate.opsForHash().get(OPS_KEY, e))
                .mapNotNull(v -> gsonThreadLocal.get().fromJson((String) v, CurrencySnapshot.class));
    }

    public Mono<BigDecimal> convertTo(String from, String to, Double amount) {
        return findCurrency(Mono.just(to))
                .zipWith(findCurrency(Mono.just(from)),
                        (first, second) ->
                                new BigDecimal(amount * (first.getAmount() / second.getAmount())));
    }

    public Mono<Boolean> compare(Operand operand, Price first, Price second) {
        if (first.getCurrency().equals(second.getCurrency())) {
            return Mono.just(operand.compare(first.getAmount(), second.getAmount()));
        }

        return convertTo(first.getCurrency().getName(), BASE_FIAT, first.getAmount().doubleValue())
                .flatMap(firstFiat ->
                        convertTo(second.getCurrency().getName(), BASE_FIAT, second.getAmount().doubleValue())
                                .map(secondFiat -> operand.compare(first.getAmount(), second.getAmount())));
    }

    public enum Operand {
        GT,
        LT,
        EQ;

        public Boolean compare(BigDecimal first, BigDecimal second) {
            return switch (this) {
                case GT -> first.compareTo(second) > 0;
                case LT -> first.compareTo(second) < 0;
                case EQ -> first.compareTo(second) == 0;
            };
        }

    }
}
