package com.example.outnowbackend.businessaccount.service;

import com.example.outnowbackend.businessaccount.domain.BusinessAccount;
import com.example.outnowbackend.businessaccount.repository.BusinessAccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BusinessAccountService {

    private final BusinessAccountRepo businessAccountRepository;

    @Autowired
    public BusinessAccountService(BusinessAccountRepo businessAccountRepository) {
        this.businessAccountRepository = businessAccountRepository;
    }

    public BusinessAccount createBusinessAccount(BusinessAccount businessAccount) {
        return businessAccountRepository.save(businessAccount);
    }

    public Optional<BusinessAccount> getBusinessAccountById(Integer id) {
        return businessAccountRepository.findById(id);
    }

    public List<BusinessAccount> getAllBusinessAccounts() {
        return businessAccountRepository.findAll();
    }

    public Optional<BusinessAccount> getBusinessAccountByEmail(String email) {
        return businessAccountRepository.findByEmail(email);
    }


    @Transactional
    public BusinessAccount updateBusinessAccount(Integer id, BusinessAccount businessAccount) {
        BusinessAccount existingAccount = businessAccountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Business account not found"));

        if (businessAccount.getEmail() != null) {
            existingAccount.setEmail(businessAccount.getEmail());
        }
        if (businessAccount.getUsername() != null) {
            existingAccount.setUsername(businessAccount.getUsername());
        }
        if (businessAccount.getUserPhoto() != null) {
            existingAccount.setUserPhoto(businessAccount.getUserPhoto());
        }
        if (businessAccount.getBio() != null) {
            existingAccount.setBio(businessAccount.getBio());
        }
        if (businessAccount.getLocation() != null) {
            existingAccount.setLocation(businessAccount.getLocation());
        }
        if (businessAccount.getInterestList() != null) {
            existingAccount.setInterestList(businessAccount.getInterestList());
        }
        return businessAccountRepository.save(existingAccount);
    }

    @Transactional
    public BusinessAccount updateBusinessAccountByEmail(String email, BusinessAccount businessAccountUpdates) {
        BusinessAccount existingAccount = businessAccountRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Business account not found"));

        if (businessAccountUpdates.getUsername() != null) {
            existingAccount.setUsername(businessAccountUpdates.getUsername());
        }
        if (businessAccountUpdates.getUserPhoto() != null) {
            existingAccount.setUserPhoto(businessAccountUpdates.getUserPhoto());
        }
        if (businessAccountUpdates.getBio() != null) {
            existingAccount.setBio(businessAccountUpdates.getBio());
        }
        if (businessAccountUpdates.getLocation() != null) {
            existingAccount.setLocation(businessAccountUpdates.getLocation());
        }
        if (businessAccountUpdates.getInterestList() != null) {
            existingAccount.setInterestList(businessAccountUpdates.getInterestList());
        }

        return businessAccountRepository.save(existingAccount);
    }


    public void deleteBusinessAccount(Integer id) {
        businessAccountRepository.deleteById(id);
    }
}
