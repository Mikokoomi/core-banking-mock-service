package com.digitalbank.corebanking.account.dto;

import com.digitalbank.corebanking.account.AccountStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AccountResponse(UUID accountId, String accountNumber, UUID applicationId,
                              String customerId, String productCode, AccountStatus status,
                              OffsetDateTime openedAt) {}
