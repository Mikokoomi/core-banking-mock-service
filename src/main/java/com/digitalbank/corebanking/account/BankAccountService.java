package com.digitalbank.corebanking.account;

import com.digitalbank.corebanking.account.dto.AccountResponse;
import com.digitalbank.corebanking.account.dto.CreateAccountRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
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
    public CreateAccountResult create(String idempotencyKey, CreateAccountRequest request) {
        String key = idempotencyKey.trim();
        String customerId = request.customerId().trim();
        String productCode = request.productCode().trim();
        var byKey = repository.findByIdempotencyKey(key);
        if (byKey.isPresent()) return replayOrConflict(byKey.get(), request.applicationId(), customerId, productCode, key);
        var byApplication = repository.findByApplicationId(request.applicationId());
        if (byApplication.isPresent()) {
            BankAccount existing = byApplication.get();
            if (!samePayload(existing, request.applicationId(), customerId, productCode)
                    || (existing.getIdempotencyKey() != null && !existing.getIdempotencyKey().equals(key))) {
                throw new IdempotencyKeyConflictException();
            }
            existing.setIdempotencyKey(key);
            return new CreateAccountResult(response(repository.saveAndFlush(existing)), false);
        }
        OffsetDateTime now = OffsetDateTime.now(clock);
        BankAccount account = new BankAccount();
        account.setAccountNumber("ACC-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());
        account.setApplicationId(request.applicationId());
        account.setIdempotencyKey(key);
        account.setCustomerId(customerId);
        account.setProductCode(productCode);
        account.setStatus(AccountStatus.ACTIVE);
        account.setOpenedAt(now);
        try {
            return new CreateAccountResult(response(repository.saveAndFlush(account)), true);
        } catch (DataIntegrityViolationException exception) {
            throw new IdempotencyKeyConflictException();
        }
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
    private CreateAccountResult replayOrConflict(BankAccount account, UUID applicationId,
                                                  String customerId, String productCode, String key) {
        if (!samePayload(account, applicationId, customerId, productCode)
                || !key.equals(account.getIdempotencyKey())) throw new IdempotencyKeyConflictException();
        return new CreateAccountResult(response(account), false);
    }
    private boolean samePayload(BankAccount account, UUID applicationId, String customerId, String productCode) {
        return applicationId.equals(account.getApplicationId())
                && customerId.equals(account.getCustomerId())
                && productCode.equals(account.getProductCode());
    }
    public record CreateAccountResult(AccountResponse account, boolean created) {}
}
