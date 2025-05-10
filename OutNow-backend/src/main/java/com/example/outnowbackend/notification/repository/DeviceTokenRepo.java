package com.example.outnowbackend.notification.repository;

import com.example.outnowbackend.notification.domain.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceTokenRepo extends JpaRepository<DeviceToken, Long> {
    List<DeviceToken> findAllByUserId(Integer userId);
    Optional<DeviceToken> findByToken(String token);
    void deleteByToken(String token);
}
