package com.revify.monolith.finance.service.insurance;

import com.revify.monolith.commons.messaging.dto.finance.InsuranceRequest;
import com.revify.monolith.finance.model.Insurance;
import com.revify.monolith.finance.service.repository.InsuranceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
