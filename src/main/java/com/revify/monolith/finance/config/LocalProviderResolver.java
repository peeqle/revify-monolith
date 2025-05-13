package com.revify.monolith.finance.config;

import com.revify.monolith.finance.service.management.BePaidRecipientManagementService;
import com.revify.monolith.finance.service.management.StripeRecipientManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

@Component
@ApplicationScope
@RequiredArgsConstructor
public class LocalProviderResolver {

    private final StripeRecipientManagementService stripeRecipientManagementService;

    private final BePaidRecipientManagementService bePaidRecipientManagementService;

}
