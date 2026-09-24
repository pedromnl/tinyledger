package com.tinyledger.dto.transaction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(name = "WithdrawalRequest", description = "Details of the withdrawal to record")
public record WithdrawalRequest(
        @Schema(description = "Withdrawal amount", example = "150.00")
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount
) {
}
