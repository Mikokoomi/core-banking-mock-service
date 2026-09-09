package com.digitalbank.corebanking.account;

import com.digitalbank.corebanking.account.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {
    @Mock BankAccountRepository repository;
    Clock clock = Clock.fixed(Instant.parse("2026-09-04T02:00:00Z"), ZoneOffset.UTC);
    @Test void firstRequestCreatesActiveAccount() {
        when(repository.findByIdempotencyKey("KEY-1")).thenReturn(Optional.empty());
        when(repository.findByApplicationId(any())).thenReturn(Optional.empty());
        when(repository.saveAndFlush(any())).thenAnswer(i -> { BankAccount a=i.getArgument(0); a.setAccountId(UUID.randomUUID()); return a; });
        var result = service().create(" KEY-1 ", new CreateAccountRequest(UUID.randomUUID(), " CUS001 ", " CURRENT_ACCOUNT "));
        assertTrue(result.created()); assertTrue(result.account().accountNumber().startsWith("ACC-"));
        assertEquals(AccountStatus.ACTIVE, result.account().status()); assertEquals("CUS001", result.account().customerId());
    }
    @Test void sameKeyAndPayloadReplaysSameAccount() {
        BankAccount existing = account("KEY-1", UUID.randomUUID(), "CUS001", "P");
        when(repository.findByIdempotencyKey("KEY-1")).thenReturn(Optional.of(existing));
        var result = service().create("KEY-1", request(existing));
        assertFalse(result.created()); assertEquals(existing.getAccountId(), result.account().accountId());
        verify(repository, never()).saveAndFlush(any());
    }
    @Test void sameKeyDifferentPayloadConflicts() {
        BankAccount existing = account("KEY-1", UUID.randomUUID(), "CUS001", "P");
        when(repository.findByIdempotencyKey("KEY-1")).thenReturn(Optional.of(existing));
        assertThrows(IdempotencyKeyConflictException.class, () -> service().create("KEY-1", new CreateAccountRequest(existing.getApplicationId(), "OTHER", "P")));
    }
    @Test void sameApplicationDifferentKeyConflicts() {
        BankAccount existing = account("KEY-1", UUID.randomUUID(), "CUS001", "P");
        when(repository.findByIdempotencyKey("KEY-2")).thenReturn(Optional.empty());
        when(repository.findByApplicationId(existing.getApplicationId())).thenReturn(Optional.of(existing));
        assertThrows(IdempotencyKeyConflictException.class, () -> service().create("KEY-2", request(existing)));
    }
    @Test void legacyRowIsBoundAndReplayed() {
        BankAccount existing = account(null, UUID.randomUUID(), "CUS001", "P");
        when(repository.findByIdempotencyKey("KEY-1")).thenReturn(Optional.empty());
        when(repository.findByApplicationId(existing.getApplicationId())).thenReturn(Optional.of(existing));
        when(repository.saveAndFlush(existing)).thenReturn(existing);
        var result = service().create("KEY-1", request(existing));
        assertFalse(result.created()); assertEquals("KEY-1", existing.getIdempotencyKey());
    }
    @Test void databaseUniquenessViolationBecomesConflict() {
        UUID id=UUID.randomUUID(); when(repository.findByIdempotencyKey("KEY-1")).thenReturn(Optional.empty());
        when(repository.findByApplicationId(id)).thenReturn(Optional.empty());
        when(repository.saveAndFlush(any())).thenThrow(new DataIntegrityViolationException("unique"));
        assertThrows(IdempotencyKeyConflictException.class, () -> service().create("KEY-1", new CreateAccountRequest(id,"CUS001","P")));
    }
    @Test void getReturnsAccount() {
        BankAccount a=account("KEY", UUID.randomUUID(), "CUS001", "P"); when(repository.findByAccountNumber(a.getAccountNumber())).thenReturn(Optional.of(a));
        assertEquals(a.getAccountNumber(), service().get(a.getAccountNumber()).accountNumber());
    }
    @Test void getMissingIsRejected() {
        when(repository.findByAccountNumber("NOPE")).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> service().get("NOPE"));
    }
    private BankAccountService service() { return new BankAccountService(repository, clock); }
    private CreateAccountRequest request(BankAccount a) { return new CreateAccountRequest(a.getApplicationId(), a.getCustomerId(), a.getProductCode()); }
    private BankAccount account(String key, UUID applicationId, String customerId, String productCode) {
        BankAccount a=new BankAccount(); a.setAccountId(UUID.randomUUID()); a.setAccountNumber("ACC-1"); a.setApplicationId(applicationId);
        a.setIdempotencyKey(key); a.setCustomerId(customerId); a.setProductCode(productCode); a.setStatus(AccountStatus.ACTIVE);
        a.setOpenedAt(OffsetDateTime.now(clock)); return a;
    }
}
