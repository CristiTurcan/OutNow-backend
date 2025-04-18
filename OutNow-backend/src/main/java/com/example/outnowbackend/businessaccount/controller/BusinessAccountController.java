package com.example.outnowbackend.businessaccount.controller;

import com.example.outnowbackend.businessaccount.dto.BusinessAccountDTO;
import com.example.outnowbackend.businessaccount.domain.BusinessAccount;
import com.example.outnowbackend.businessaccount.service.BusinessAccountService;
import com.example.outnowbackend.user.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/business-accounts")
public class BusinessAccountController {

    private final BusinessAccountService businessAccountService;

    @Autowired
    public BusinessAccountController(BusinessAccountService businessAccountService) {
        this.businessAccountService = businessAccountService;
    }

    @PostMapping
    public ResponseEntity<BusinessAccountDTO> createBusinessAccount(@RequestBody BusinessAccount businessAccount) {
        BusinessAccountDTO created = businessAccountService.createBusinessAccount(businessAccount);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/by-email")
    public ResponseEntity<BusinessAccountDTO> getBusinessAccountByEmail(@RequestParam String email) {
        Optional<BusinessAccountDTO> optionalAccount = businessAccountService.getBusinessAccountByEmail(email);
        return optionalAccount.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/by-email")
    public ResponseEntity<BusinessAccountDTO> updateBusinessAccountByEmail(
            @RequestParam String email,
            @RequestBody BusinessAccount businessAccountUpdates) {
        BusinessAccountDTO updated = businessAccountService.updateBusinessAccountByEmail(email, businessAccountUpdates);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusinessAccountDTO> getBusinessAccountById(@PathVariable Integer id) {
        Optional<BusinessAccountDTO> optionalAccount = businessAccountService.getBusinessAccountById(id);
        return optionalAccount.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/id")
    public ResponseEntity<Integer> getBusinessAccountIdByEmail(@RequestParam String email) {
        Optional<BusinessAccount> optionalAccount = businessAccountService.getBusinessAccountEntityByEmail(email);
        return optionalAccount.map(account -> ResponseEntity.ok(account.getId()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<BusinessAccountDTO>> getAllBusinessAccounts() {
        List<BusinessAccountDTO> accounts = businessAccountService.getAllBusinessAccounts();
        return ResponseEntity.ok(accounts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BusinessAccountDTO> updateBusinessAccount(@PathVariable Integer id,
                                                                    @RequestBody BusinessAccount businessAccount) {
        BusinessAccountDTO updated = businessAccountService.updateBusinessAccount(id, businessAccount);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}/followers/count")
    public ResponseEntity<Long> getFollowersCount(@PathVariable Integer id) {
        return ResponseEntity.ok(businessAccountService.getFollowersCount(id));
    }

    @GetMapping("/{id}/followers")
    public ResponseEntity<Set<UserDTO>> getFollowers(@PathVariable Integer id) {
        try {
            Set<UserDTO> followers = businessAccountService.getFollowers(id);
            return ResponseEntity.ok(followers);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBusinessAccount(@PathVariable Integer id) {
        businessAccountService.deleteBusinessAccount(id);
        return ResponseEntity.noContent().build();
    }
}
