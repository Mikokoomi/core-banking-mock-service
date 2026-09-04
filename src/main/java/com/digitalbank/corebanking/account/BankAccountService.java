package com.digitalbank.corebanking.account;

import com.digitalbank.corebanking.account.dto.AccountResponse;
import com.digitalbank.corebanking.account.dto.CreateAccountRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class BankAccountService {
    private final BankAccountRepository repository;
    private final Clock clock;
    public BankAccountService(BankAccountRepository repository, Clock clock) {
        this.repository = repository; this.clock = clock;
    }
    @Transactional
    public AccountResponse create(CreateAccountRequest request) {
        if (repository.existsByApplicationId(request.applicationId())) throw new AccountAlreadyExistsException();
        OffsetDateTime now = OffsetDateTime.now(clock);
        BankAccount account = new BankAccount();
        account.setAccountNumber("ACC-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());
        account.setApplicationId(request.applicationId());
        account.setCustomerId(request.customerId().trim());
        account.setProductCode(request.productCode().trim());
        account.setStatus(AccountStatus.ACTIVE);
        account.setOpenedAt(now);
        return response(repository.save(account));
    }
    @Transactional(readOnly = true)
    public AccountResponse get(String accountNumber) {
        return repository.findByAccountNumber(accountNumber)
                .map(this::response).orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }
    private AccountResponse response(BankAccount a) {
        return new AccountResponse(a.getAccountId(), a.getAccountNumber(), a.getApplicationId(),
                a.getCustomerId(), a.getProductCode(), a.getStatus(), a.getOpenedAt());
    }
}
