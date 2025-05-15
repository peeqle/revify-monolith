package com.revify.monolith.finance.service.insurance;

import com.revify.monolith.commons.messaging.dto.finance.InsuranceRequest;
import com.revify.monolith.finance.model.Insurance;
import com.revify.monolith.finance.service.repository.InsuranceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InsuranceService {

    private final InsuranceRepository insuranceRepository;

    public Optional<Insurance> findByItemId(String itemId) {
        return insuranceRepository.findInsuranceByItemId(itemId);
    }

    public void createInsuranceEntry(InsuranceRequest value) {
        Insurance newInsurance = InsuranceMapper.toInsurance(value);
        insuranceRepository.save(newInsurance);
    }

    public Optional<Insurance> updateInsuranceStatuses(String insuranceId, boolean isActive, boolean isPayed) {
        Optional<Insurance> byId = insuranceRepository.findById(UUID.fromString(insuranceId));
        byId.ifPresent(insurance -> {
            insurance.setIsActive(isActive);
            insurance.setIsPayed(isPayed);

            insuranceRepository.save(insurance);
        });

        return byId;
    }

    public static class InsuranceMapper {
        public static Insurance toInsurance(InsuranceRequest insuranceRequest) {
            Insurance insurance = new Insurance();

            insurance.setUserId(insuranceRequest.getUserId());
            insurance.setItemId(insuranceRequest.getItemId());
            insurance.setInsurancePrice(insuranceRequest.getInsurancePrice());
            insurance.setItemPrice(insuranceRequest.getItemPrice());


            return insurance;
        }
    }
}
