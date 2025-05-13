package com.revify.monolith.billing.service.specification;

import com.revify.monolith.billing.model.Billing;
import com.revify.monolith.commons.auth.sync.UserUtils;
import com.revify.monolith.commons.exceptions.UnauthorizedAccessError;
import org.springframework.data.jpa.domain.Specification;

public class BillingSpecification {

    public static Specification<Billing> isPayed(Boolean isPayed) {
        return (root, query, criteriaBuilder) -> {
            if (isPayed == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("billing_payed"), isPayed);
        };
    }

    public static Specification<Billing> withPayerId() {
        return (root, query, criteriaBuilder) ->
        {
            try {
                return criteriaBuilder.equal(root.get("payer_id"), UserUtils.getKeycloakId());
            } catch (UnauthorizedAccessError e) {
                throw new RuntimeException(e);
            }
        };
    }
}
