package com.digitalbank.corebanking.account;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "bank_accounts")
@Getter @Setter @NoArgsConstructor
public class BankAccount {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "account_id", nullable = false, updatable = false)
    private UUID accountId;
    @Column(name = "account_number", nullable = false, unique = true, length = 40)
    private String accountNumber;
    @Column(name = "application_id", nullable = false, unique = true)
    private UUID applicationId;
    @Column(name = "idempotency_key", unique = true, length = 200)
    private String idempotencyKey;
    @Column(name = "customer_id", nullable = false, length = 100)
    private String customerId;
    @Column(name = "product_code", nullable = false, length = 50)
    private String productCode;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private AccountStatus status;
    @Column(name = "opened_at", nullable = false)
    private OffsetDateTime openedAt;
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
    @PrePersist void create() { createdAt = openedAt; updatedAt = openedAt; }
    @PreUpdate void update() { updatedAt = OffsetDateTime.now(); }
}
