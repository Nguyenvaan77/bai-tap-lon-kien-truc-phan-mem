package bankproject.customer.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bankproject.customer.entity.Beneficiaries;
import bankproject.customer.repository.BeneficiaryRepository;
import bankproject.customer.service.BeneficiariesService;

@Service
public class BeneficiariesServiceImpl implements BeneficiariesService {

    @Autowired
    private BeneficiaryRepository beneficiariesRepository;

    @Override
    public Beneficiaries createBeneficiary(Beneficiaries beneficiary) {
        return beneficiariesRepository.save(beneficiary);
    }

    @Override
    public Beneficiaries getBeneficiaryById(int beneficiaryId) {
        Optional<Beneficiaries> beneficiaryOptional = beneficiariesRepository.findById(beneficiaryId);
        return beneficiaryOptional.orElse(null);
    }

    @Override
    public List<Beneficiaries> getAllBeneficiaries() {
        return beneficiariesRepository.findAll();
    }

    @Override
    public Beneficiaries updateBeneficiary(Beneficiaries beneficiary) {
        return beneficiariesRepository.save(beneficiary);
    }

    @Override
    public void deleteBeneficiary(int beneficiaryId) {
        beneficiariesRepository.deleteById(beneficiaryId);
    }

    @Override
    public List<Beneficiaries> getBeneficiariesByUserId(String userId) {
        return beneficiariesRepository.findByUserId(userId);
    }

    @Override
    public List<Beneficiaries> createBeneficiaries(Beneficiaries beneficiary, String userId) {
        beneficiary.setUserId(userId);
        beneficiariesRepository.save(beneficiary);
        return beneficiariesRepository.findByUserId(userId);
    }
}
