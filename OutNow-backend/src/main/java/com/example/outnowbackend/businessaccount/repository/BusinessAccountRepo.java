package com.example.outnowbackend.businessaccount.repository;

import com.example.outnowbackend.businessaccount.domain.BusinessAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusinessAccountRepo extends JpaRepository<BusinessAccount, Integer> {
    Optional<BusinessAccount> findByEmail(String email);
    @Query("SELECT COUNT(u) FROM User u JOIN u.followedAccounts ba WHERE ba.id = :businessAccountId")
    long countFollowersByBusinessAccountId(@Param("businessAccountId") Integer businessAccountId);
}
