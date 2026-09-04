package com.digitalbank.corebanking.account;

import com.digitalbank.corebanking.account.dto.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {
    @Mock BankAccountRepository repository;
    Clock clock = Clock.fixed(Instant.parse("2026-09-04T02:00:00Z"), ZoneOffset.UTC);
    @Test void createPersistsActiveAccountWithGeneratedNumber() {
        when(repository.save(any())).thenAnswer(i -> { BankAccount a=i.getArgument(0); a.setAccountId(UUID.randomUUID()); return a; });
        AccountResponse result = new BankAccountService(repository, clock).create(new CreateAccountRequest(UUID.randomUUID(), " CUS001 ", " CURRENT_ACCOUNT "));
        assertTrue(result.accountNumber().startsWith("ACC-")); assertEquals(AccountStatus.ACTIVE, result.status());
        assertEquals("CUS001", result.customerId()); assertEquals(OffsetDateTime.now(clock), result.openedAt());
    }
    @Test void duplicateApplicationIsRejected() {
        UUID id=UUID.randomUUID(); when(repository.existsByApplicationId(id)).thenReturn(true);
        assertThrows(AccountAlreadyExistsException.class, () -> new BankAccountService(repository, clock).create(new CreateAccountRequest(id,"CUS001","P")));
        verify(repository, never()).save(any());
    }
    @Test void getReturnsAccount() {
        BankAccount a=new BankAccount(); a.setAccountId(UUID.randomUUID()); a.setAccountNumber("ACC-1"); a.setApplicationId(UUID.randomUUID()); a.setCustomerId("CUS001"); a.setProductCode("P"); a.setStatus(AccountStatus.ACTIVE); a.setOpenedAt(OffsetDateTime.now(clock));
        when(repository.findByAccountNumber("ACC-1")).thenReturn(Optional.of(a));
        assertEquals("ACC-1", new BankAccountService(repository, clock).get("ACC-1").accountNumber());
    }
    @Test void getMissingIsRejected() {
        when(repository.findByAccountNumber("NOPE")).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> new BankAccountService(repository, clock).get("NOPE"));
    }
}
