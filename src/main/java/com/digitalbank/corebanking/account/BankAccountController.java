package com.digitalbank.corebanking.account;

import com.digitalbank.corebanking.account.dto.AccountResponse;
import com.digitalbank.corebanking.account.dto.CreateAccountRequest;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class BankAccountController {
    private final BankAccountService service;
    public BankAccountController(BankAccountService service) { this.service = service; }
    @PostMapping
    ResponseEntity<AccountResponse> create(@Valid @RequestBody CreateAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }
    @GetMapping("/{accountNumber}")
    AccountResponse get(@PathVariable String accountNumber) { return service.get(accountNumber); }
}
