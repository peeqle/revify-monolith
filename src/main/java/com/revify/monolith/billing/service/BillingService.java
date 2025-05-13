package com.revify.monolith.billing.service;


import com.revify.monolith.billing.exception.BillingProcessingException;
import com.revify.monolith.billing.exception.InsuranceAlreadyExistsException;
import com.revify.monolith.billing.exception.InsuranceDoesntExistsException;
import com.revify.monolith.billing.exception.InsurancePersistenceException;
import com.revify.monolith.billing.model.Billing;
import com.revify.monolith.billing.model.InsuranceBilling;
import com.revify.monolith.billing.model.ItemBilling;
import com.revify.monolith.billing.model.ItemPremiumBilling;
import com.revify.monolith.billing.model.dto.BillingSearchDTO;
import com.revify.monolith.billing.service.repository.BillingRepository;
import com.revify.monolith.billing.service.specification.BillingSpecification;
import com.revify.monolith.commons.exceptions.UnauthorizedAccessError;
import com.revify.monolith.commons.finance.Price;
import com.revify.monolith.commons.finance.TaxRegion;
import com.revify.monolith.commons.messaging.dto.BillingCreation;
import com.revify.monolith.commons.messaging.dto.ItemBillingCreation;
import com.revify.monolith.commons.messaging.dto.ItemPremiumBillingCreation;
import com.revify.monolith.finance.model.Insurance;
import com.revify.monolith.finance.service.insurance.InsuranceService;
import com.revify.monolith.user.models.user.AppUser;
import com.revify.monolith.user.service.ReadUserService;
import com.revify.monolith.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingService {

    private final BillingRepository billingRepository;

    private final InsuranceService insuranceService;

    private final ReadUserService readUserService;

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void createBilling(BillingCreation creation) throws InsurancePersistenceException {
        TaxRegion taxRegion = fetchTaxRegion(creation.getPayerId());
        Billing billing;

        if (creation instanceof ItemBillingCreation value) {
            billing = handleItemBilling(value, taxRegion);
        } else if (creation instanceof ItemPremiumBillingCreation value) {
            billing = handleItemPremiumBilling(value);
        } else {
            billing = handleDefaultBilling(creation, taxRegion);
        }

        if (billing == null) throw new BillingProcessingException(creation);
        sendBillingNotification(billing);
    }

    private Billing handleItemPremiumBilling(ItemPremiumBillingCreation value) {
        if (billingRepository.existsByItemPremiumIdAndPayerId(value.getItemPremiumId(), value.getPayerId())) {
            return null;
        }

        ItemPremiumBilling itemBilling = new ItemPremiumBilling();
        itemBilling.setItemPremiumId(value.getItemPremiumId());
        itemBilling.setPrice(value.getPrice());
        itemBilling.setPayerId(value.getPayerId());
        itemBilling.setBillingStrategy(value.getBillingStrategy());
        itemBilling.setCreatedAt(Instant.now().toEpochMilli());
        itemBilling.setTaxRegion(value.getTaxRegion());

        return billingRepository.save(itemBilling);
    }

    private Billing handleItemBilling(ItemBillingCreation value, TaxRegion taxRegion) throws InsurancePersistenceException {
        if (billingRepository.existsByItemIdAndPayerId(value.getItemId(), value.getPayerId())) {
            return null;
        }

        ItemBilling itemBilling = new ItemBilling();
        itemBilling.setItemId(value.getItemId());
        itemBilling.setPrice(value.getPrice());
        itemBilling.setPayerId(value.getPayerId());
        itemBilling.setBillingStrategy(value.getBillingStrategy());
        itemBilling.setCreatedAt(Instant.now().toEpochMilli());

        Optional<Insurance> insuranceOpt = insuranceService.findByItemId(value.getItemId());
        Insurance insurance = insuranceOpt.orElseThrow(() -> new InsuranceDoesntExistsException(value.getItemId()));
        if (!billingRepository.existsByInsuranceId(insurance.getId())) {
            InsuranceBilling insuranceBilling = createInsuranceBilling(value, taxRegion, insurance.getId());
            itemBilling.setInsurance(List.of(billingRepository.save(insuranceBilling)));
        } else {
            throw new InsuranceAlreadyExistsException("Insurance already created!");
        }

        return billingRepository.save(itemBilling);
    }

    private InsuranceBilling createInsuranceBilling(BillingCreation value, TaxRegion taxRegion, UUID insuranceId) {
        InsuranceBilling insuranceBilling = new InsuranceBilling();
        insuranceBilling.setInsurance(new Insurance(insuranceId));
        insuranceBilling.setPrice(new Price.Builder()
                .withAmount(value.getPrice().getAmount())
                .withCurrency(value.getPrice().getCurrency())
                .build());
        insuranceBilling.setPayerId(value.getPayerId());
        insuranceBilling.setBillingStrategy(value.getBillingStrategy());
        insuranceBilling.setCreatedAt(Instant.now().toEpochMilli());
        insuranceBilling.setTaxRegion(taxRegion);

        return insuranceBilling;
    }

    private Billing handleDefaultBilling(BillingCreation value, TaxRegion taxRegion) {
        Billing billing = new Billing();
        billing.setPrice(value.getPrice());
        billing.setPayerId(value.getPayerId());
        billing.setBillingStrategy(value.getBillingStrategy());
        billing.setCreatedAt(Instant.now().toEpochMilli());
        billing.setTaxRegion(taxRegion);
        return billingRepository.save(billing);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRES_NEW)
    public Page<Billing> readAvailableBilling(BillingSearchDTO value, Pageable pageable) {
        Specification<Billing> specification = BillingSpecification.withPayerId()
                .and(BillingSpecification.isPayed(value.getPayed()));

        return billingRepository.findAll(specification, pageable);
    }


    private TaxRegion fetchTaxRegion(Long payerId) {
        try {
            AppUser appUser = readUserService.loadUserById(payerId);
            return appUser.getFinancialInformation().getTaxRegion();
        } catch (UnauthorizedAccessError e) {
            log.error("Cannot fetch user details from RPC request for id: {}", payerId, e);
            return TaxRegion.BELARUS;
        }
    }

    //notify couriers about payment
    public void sendBillingNotification(Billing save) {
//        directNotificationProducer.sendDirect();
    }
}
