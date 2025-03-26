package com.example.outnowbackend.businessaccount.controller;

import com.example.outnowbackend.businessaccount.domain.BusinessAccount;
import com.example.outnowbackend.businessaccount.service.BusinessAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/business-accounts")
public class BusinessAccountController {

    private final BusinessAccountService businessAccountService;

    @Autowired
    public BusinessAccountController(BusinessAccountService businessAccountService) {
        this.businessAccountService = businessAccountService;
    }

    @PostMapping
    public ResponseEntity<BusinessAccount> createBusinessAccount(@RequestBody BusinessAccount businessAccount) {
        BusinessAccount created = businessAccountService.createBusinessAccount(businessAccount);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/by-email")
    public ResponseEntity<BusinessAccount> getBusinessAccountByEmail(@RequestParam String email) {
        Optional<BusinessAccount> optionalAccount = businessAccountService.getBusinessAccountByEmail(email);
        return optionalAccount.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/by-email")
    public ResponseEntity<BusinessAccount> updateBusinessAccountByEmail(
            @RequestParam String email,
            @RequestBody BusinessAccount businessAccountUpdates) {
        BusinessAccount updated = businessAccountService.updateBusinessAccountByEmail(email, businessAccountUpdates);
        return ResponseEntity.ok(updated);
    }


    @GetMapping("/{id}")
    public ResponseEntity<BusinessAccount> getBusinessAccountById(@PathVariable Integer id) {
        Optional<BusinessAccount> optionalAccount = businessAccountService.getBusinessAccountById(id);
        return optionalAccount.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/id")
    public ResponseEntity<Integer> getBusinessAccountIdByEmail(@RequestParam String email) {
        Optional<BusinessAccount> optionalAccount = businessAccountService.getBusinessAccountByEmail(email);
        return optionalAccount.map(account -> ResponseEntity.ok(account.getId()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping
    public ResponseEntity<List<BusinessAccount>> getAllBusinessAccounts() {
        List<BusinessAccount> accounts = businessAccountService.getAllBusinessAccounts();
        return ResponseEntity.ok(accounts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BusinessAccount> updateBusinessAccount(@PathVariable Integer id,
                                                                 @RequestBody BusinessAccount businessAccount) {
        BusinessAccount updated = businessAccountService.updateBusinessAccount(id, businessAccount);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBusinessAccount(@PathVariable Integer id) {
        businessAccountService.deleteBusinessAccount(id);
        return ResponseEntity.noContent().build();
    }
}
