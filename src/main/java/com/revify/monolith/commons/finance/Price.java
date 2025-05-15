package com.revify.monolith.commons.finance;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class Price implements Comparable<Price> {
    @JsonDeserialize(using = Currency.CurrencyDeserializer.class)
    private Currency currency;
    private BigDecimal amount;

    public Price(Currency currency, BigDecimal amount) {
        this.currency = currency;
        this.amount = amount;
    }

    public static class Builder {

        private Currency currency;
        private BigDecimal amount;

        public Builder withCurrency(String currency) {
            this.currency = Currency.from(currency);
            return this;
        }

        public Builder withCurrency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public Builder withAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder withAmount(Double amount) {
            this.amount = new BigDecimal(amount);
            return this;
        }

        public Price build() {
            return new Price(currency, amount);
        }

    }

    @Override
    public int compareTo(Price other) {
        if (other == null) return 1;

        if (this.currency != other.currency) {
            return 1;
        }

        return this.amount == null ? -1 : this.amount.compareTo(other.amount);
    }
}
