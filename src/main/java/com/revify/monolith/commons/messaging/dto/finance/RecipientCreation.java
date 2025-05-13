package com.revify.monolith.commons.messaging.dto.finance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipientCreation {
    private String userId;
    private String username;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;

    private Long dobDay;
    private Long dobMonth;
    private Long dobYear;

    private String countryCode;
}
