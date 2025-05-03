package com.example.outnowbackend.notification.service;

import com.example.outnowbackend.notification.domain.DeviceToken;
import com.example.outnowbackend.notification.repository.DeviceTokenRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeviceTokenService {

    private final DeviceTokenRepo repo;

    public DeviceTokenService(DeviceTokenRepo repo) {
        this.repo = repo;
    }

    @Transactional
    public void registerToken(Integer userId, String token) {
        repo.findByToken(token).ifPresentOrElse(
                existing -> existing.setUserId(userId),
                () -> repo.save(DeviceToken.builder()
                        .userId(userId)
                        .token(token)
                        .build())
        );
    }

    @Transactional
    public void unregisterToken(String token) {
        repo.deleteByToken(token);
    }

    @Transactional(readOnly = true)
    public List<String> getTokensForUser(Integer userId) {
        return repo.findAllByUserId(userId)
                .stream()
                .map(DeviceToken::getToken)
                .toList();
    }
}
