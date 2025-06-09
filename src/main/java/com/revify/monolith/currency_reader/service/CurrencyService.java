package com.revify.monolith.currency_reader.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.revify.monolith.commons.finance.Currency;
import com.revify.monolith.commons.finance.Price;
import com.revify.monolith.currency_reader.data.CurrencySnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private static final String OPS_KEY = "CURRENCY";

    private static final String BASE_FIAT = "EUR";

    private final RedisTemplate<String, String> redisTemplate;

    private final ThreadLocal<Gson> gsonThreadLocal = ThreadLocal.withInitial(() -> new GsonBuilder().create());

    public CurrencySnapshot findCurrency(String literal) {
        Object o = redisTemplate.opsForHash().get(OPS_KEY, literal);
        return gsonThreadLocal.get().fromJson((String) o, CurrencySnapshot.class);
    }

    /**
     * @param first  - headliner
     * @param second
     * @return
     */
    public Price mergeTwo(Price first, Price second) {
        if (first.getCurrency() == second.getCurrency()) {
            return Price.builder()
                    .withAmount(first.getAmount().add(second.getAmount()))
                    .withCurrency(first.getCurrency())
                    .build();
        }

        var secondConvertedAmount = convertTo(second, first.getCurrency());
        return Price.builder()
                .withCurrency(first.getCurrency())
                .withAmount(first.getAmount().add(secondConvertedAmount))
                .build();
    }

    public BigDecimal convertTo(Price from, Currency to) {
        return convertTo(from.getCurrency().getName(), from.getAmount().doubleValue(), to.getName());
    }

    public BigDecimal convertTo(String from, Double amount, String to) {
        CurrencySnapshot currencyTo = findCurrency(to);
        CurrencySnapshot currencyFrom = findCurrency(from);
        if (currencyTo == null || currencyFrom == null) {
            throw new RuntimeException("Could not find currency");
        }
        return new BigDecimal(amount * (currencyTo.getAmount() / currencyFrom.getAmount()));
    }

    public Boolean compare(Operand operand, Price first, Price second) {
        if (first.getCurrency().equals(second.getCurrency())) {
            return operand.compare(first.getAmount(), second.getAmount());
        }

        BigDecimal convertedFirst = convertTo(first, Currency.from(BASE_FIAT));
        BigDecimal bigDecimal = convertTo(second, Currency.from(BASE_FIAT));

        return operand.compare(convertedFirst, bigDecimal);
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
