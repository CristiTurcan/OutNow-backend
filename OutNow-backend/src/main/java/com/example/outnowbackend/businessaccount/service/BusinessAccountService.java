package com.example.outnowbackend.businessaccount.service;

import com.example.outnowbackend.businessaccount.domain.BusinessAccount;
import com.example.outnowbackend.businessaccount.dto.BusinessAccountDTO;
import com.example.outnowbackend.businessaccount.mapper.BusinessAccountMapper;
import com.example.outnowbackend.businessaccount.repository.BusinessAccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BusinessAccountService {

    private final BusinessAccountRepo businessAccountRepository;
    private final BusinessAccountMapper businessAccountMapper;

    @Autowired
    public BusinessAccountService(BusinessAccountRepo businessAccountRepository,
                                  BusinessAccountMapper businessAccountMapper) {
        this.businessAccountRepository = businessAccountRepository;
        this.businessAccountMapper = businessAccountMapper;
    }

    public BusinessAccountDTO createBusinessAccount(BusinessAccount businessAccount) {
        BusinessAccount created = businessAccountRepository.save(businessAccount);
        return businessAccountMapper.toDTO(created);
    }

    public Optional<BusinessAccountDTO> getBusinessAccountById(Integer id) {
        return businessAccountRepository.findById(id)
                .map(businessAccountMapper::toDTO);
    }

    public List<BusinessAccountDTO> getAllBusinessAccounts() {
        List<BusinessAccount> accounts = businessAccountRepository.findAll();
        return accounts.stream()
                .map(businessAccountMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<BusinessAccountDTO> getBusinessAccountByEmail(String email) {
        return businessAccountRepository.findByEmail(email)
                .map(businessAccountMapper::toDTO);
    }

    // If you need the raw entity in some cases, you can keep this helper.
    public Optional<BusinessAccount> getBusinessAccountEntityByEmail(String email) {
        return businessAccountRepository.findByEmail(email);
    }

    @Transactional
    public BusinessAccountDTO updateBusinessAccount(Integer id, BusinessAccount businessAccount) {
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
        BusinessAccount updated = businessAccountRepository.save(existingAccount);
        return businessAccountMapper.toDTO(updated);
    }

    @Transactional
    public BusinessAccountDTO updateBusinessAccountByEmail(String email, BusinessAccount businessAccountUpdates) {
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
        BusinessAccount updated = businessAccountRepository.save(existingAccount);
        return businessAccountMapper.toDTO(updated);
    }

    public void deleteBusinessAccount(Integer id) {
        businessAccountRepository.deleteById(id);
    }
}
