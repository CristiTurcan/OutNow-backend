package com.example.outnowbackend.businessaccount.mapper;

import com.example.outnowbackend.businessaccount.domain.BusinessAccount;
import com.example.outnowbackend.businessaccount.dto.BusinessAccountDTO;
import org.springframework.stereotype.Component;

@Component
public class BusinessAccountMapper {

    public BusinessAccountDTO toDTO(BusinessAccount account) {
        if (account == null) {
            return null;
        }
        BusinessAccountDTO dto = new BusinessAccountDTO();
        dto.setId(account.getId());
        dto.setEmail(account.getEmail());
        dto.setUsername(account.getUsername());
        dto.setUserPhoto(account.getUserPhoto());
        dto.setBio(account.getBio());
        dto.setLocation(account.getLocation());
        dto.setInterestList(account.getInterestList());
        dto.setLatitude(account.getLatitude());
        dto.setLongitude(account.getLongitude());
        return dto;
    }

    public BusinessAccount toEntity(BusinessAccountDTO dto) {
        if (dto == null) {
            return null;
        }
        BusinessAccount account = new BusinessAccount();
        account.setId(dto.getId());
        account.setEmail(dto.getEmail());
        account.setUsername(dto.getUsername());
        account.setUserPhoto(dto.getUserPhoto());
        account.setBio(dto.getBio());
        account.setLocation(dto.getLocation());
        account.setInterestList(dto.getInterestList());
        account.setLatitude(dto.getLatitude());
        account.setLongitude(dto.getLongitude());
        return account;
    }
}
