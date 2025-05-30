package com.revify.monolith.finance.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMethodDTO {
    private String displayBrand;
    private String cardCountry;
    private Long expMonth;
    private Long expYear;
    private String last4;
    private Boolean cvcCorrect;
}
