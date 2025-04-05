package com.example.outnowbackend.businessaccount.service;

import com.example.outnowbackend.businessaccount.domain.BusinessAccount;
import com.example.outnowbackend.businessaccount.dto.BusinessAccountDTO;
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

    @Autowired
    public BusinessAccountService(BusinessAccountRepo businessAccountRepository) {
        this.businessAccountRepository = businessAccountRepository;
    }

    // Conversion helper: entity -> DTO
    public BusinessAccountDTO convertToDTO(BusinessAccount account) {
        BusinessAccountDTO dto = new BusinessAccountDTO();
        dto.setId(account.getId());
        dto.setEmail(account.getEmail());
        dto.setUsername(account.getUsername());
        dto.setUserPhoto(account.getUserPhoto());
        dto.setBio(account.getBio());
        dto.setLocation(account.getLocation());
        dto.setInterestList(account.getInterestList());
        return dto;
    }

    // Helper for lists
    public List<BusinessAccountDTO> convertToDTOList(List<BusinessAccount> accounts) {
        return accounts.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public BusinessAccountDTO createBusinessAccount(BusinessAccount businessAccount) {
        BusinessAccount created = businessAccountRepository.save(businessAccount);
        return convertToDTO(created);
    }

    public Optional<BusinessAccountDTO> getBusinessAccountById(Integer id) {
        return businessAccountRepository.findById(id).map(this::convertToDTO);
    }

    public List<BusinessAccountDTO> getAllBusinessAccounts() {
        List<BusinessAccount> accounts = businessAccountRepository.findAll();
        return convertToDTOList(accounts);
    }

    public Optional<BusinessAccountDTO> getBusinessAccountByEmail(String email) {
        return businessAccountRepository.findByEmail(email).map(this::convertToDTO);
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
        return convertToDTO(updated);
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
        return convertToDTO(updated);
    }

    public void deleteBusinessAccount(Integer id) {
        businessAccountRepository.deleteById(id);
    }
}
