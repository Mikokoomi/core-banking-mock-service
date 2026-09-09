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
    ResponseEntity<AccountResponse> create(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody CreateAccountRequest request) {
        if (idempotencyKey == null || idempotencyKey.isBlank() || idempotencyKey.length() > 200) {
            throw new InvalidIdempotencyKeyException();
        }
        BankAccountService.CreateAccountResult result = service.create(idempotencyKey, request);
        return ResponseEntity.status(result.created() ? HttpStatus.CREATED : HttpStatus.OK).body(result.account());
    }
    @GetMapping("/{accountNumber}")
    AccountResponse get(@PathVariable String accountNumber) { return service.get(accountNumber); }
}
