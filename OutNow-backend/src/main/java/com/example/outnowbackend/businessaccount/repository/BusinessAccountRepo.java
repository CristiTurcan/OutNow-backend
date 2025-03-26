package com.example.outnowbackend.businessaccount.repository;

import com.example.outnowbackend.businessaccount.domain.BusinessAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusinessAccountRepo extends JpaRepository<BusinessAccount, Integer> {
    // Add custom query methods if needed
    Optional<BusinessAccount> findByEmail(String email);

}
