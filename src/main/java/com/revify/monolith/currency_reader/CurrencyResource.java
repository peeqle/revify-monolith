package com.revify.monolith.currency_reader;

import com.revify.monolith.commons.finance.Currency;
import com.revify.monolith.commons.finance.Price;
import com.revify.monolith.currency_reader.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/currency")
@RequiredArgsConstructor
public class CurrencyResource {

    private final CurrencyService currencyService;

    @PostMapping("/convert")
    public ResponseEntity<Object> convert(@RequestBody ConvertRequest request) {
        if (request == null || request.from == null || request.to == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        BigDecimal bigDecimal = currencyService.convertTo(request.from, request.to);
        return ResponseEntity.ok(Price.builder()
                .withCurrency(request.to)
                .withAmount(bigDecimal)
                .build());
    }

    public record ConvertRequest(Price from, Currency to) {
    }
}
