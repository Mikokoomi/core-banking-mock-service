package com.digitalbank.corebanking.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateAccountRequest(
        @NotNull UUID applicationId,
        @NotBlank String customerId,
        @NotBlank String productCode
) {}
