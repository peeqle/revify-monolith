package com.revify.monolith.currency_reader.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("CURRENCY")
public class CurrencySnapshot implements Serializable {
    private Long timestamp;
    private String currency;
    private String parentFiat;
    private Double amount;
}
