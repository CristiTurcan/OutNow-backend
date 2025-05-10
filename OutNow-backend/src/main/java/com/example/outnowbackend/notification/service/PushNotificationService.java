package com.example.outnowbackend.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Slf4j
public class PushNotificationService {

    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";
    private final RestTemplate rest = new RestTemplate();

    public void sendPush(List<String> tokens, String title, String body, Map<String, Object> data) {
        for (int i = 0; i < tokens.size(); i += 100) {
            List<Map<String,Object>> batch = new ArrayList<>();
            tokens.subList(i, Math.min(i + 100, tokens.size()))
                    .forEach(token -> {
                        Map<String,Object> msg = new HashMap<>();
                        msg.put("to", token);
                        msg.put("sound", "default");
                        msg.put("title", title);
                        msg.put("body", body);
                        if (data != null) msg.put("data", data);
                        batch.add(msg);
                    });

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<List<Map<String,Object>>> req = new HttpEntity<>(batch, headers);

            try {
                ResponseEntity<String> resp = rest.postForEntity(EXPO_PUSH_URL, req, String.class);
                log.info("Expo push response: {}", resp.getBody());
            } catch (Exception ex) {
                log.error("Failed to send push: {}", ex.getMessage(), ex);
            }
        }
    }
}
