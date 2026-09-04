package com.digitalbank.corebanking.account;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
    boolean existsByApplicationId(UUID applicationId);
    Optional<BankAccount> findByAccountNumber(String accountNumber);
}
