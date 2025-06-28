package com.example.outnowbackend.businessaccount.service;

import com.example.outnowbackend.businessaccount.domain.BusinessAccount;
import com.example.outnowbackend.businessaccount.dto.BusinessAccountDTO;
import com.example.outnowbackend.businessaccount.mapper.BusinessAccountMapper;
import com.example.outnowbackend.businessaccount.repository.BusinessAccountRepo;
import com.example.outnowbackend.user.domain.User;
import com.example.outnowbackend.user.dto.UserDTO;
import com.example.outnowbackend.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusinessAccountService {

    private final BusinessAccountRepo businessAccountRepository;
    private final BusinessAccountMapper businessAccountMapper;
    private final UserMapper userMapper;

    public boolean usernameExists(String username) {
        return businessAccountRepository.existsByUsername(username);
    }

    public boolean emailExists(String email) {
        return businessAccountRepository.findByEmail(email).isPresent();
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
        if (businessAccount.getLatitude() != null) {
            existingAccount.setLatitude(businessAccount.getLatitude());
        }
        if (businessAccount.getLongitude() != null) {
            existingAccount.setLongitude(businessAccount.getLongitude());
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
        if (businessAccountUpdates.getLatitude() != null) {
            existingAccount.setLatitude(businessAccountUpdates.getLatitude());
        }
        if (businessAccountUpdates.getLongitude() != null) {
            existingAccount.setLongitude(businessAccountUpdates.getLongitude());
        }
        if (businessAccountUpdates.getInterestList() != null) {
            existingAccount.setInterestList(businessAccountUpdates.getInterestList());
        }
        BusinessAccount updated = businessAccountRepository.save(existingAccount);
        return businessAccountMapper.toDTO(updated);
    }

    @Transactional(readOnly = true)
    public long getFollowersCount(Integer businessAccountId) {
        return businessAccountRepository.countFollowersByBusinessAccountId(businessAccountId);
    }

    @Transactional(readOnly = true)
    public Set<UserDTO> getFollowers(Integer businessAccountId) {
        BusinessAccount account = businessAccountRepository.findById(businessAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Business account not found"));
        Set<User> followers = account.getFollowers();
        followers.size();

        return followers.stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toSet());
    }

    public void deleteBusinessAccount(Integer id) {
        businessAccountRepository.deleteById(id);
    }
}
