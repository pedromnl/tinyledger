package com.tinyledger.dto.transaction;

import com.tinyledger.model.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(name = "Transaction", description = "Single transaction entry in an account history")
public record TransactionDto(
        @Schema(description = "Unique transaction identifier", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID transactionId,
        @Schema(description = "Transaction type", example = "WITHDRAWAL")
        TransactionType transactionType,
        @Schema(description = "Transaction amount", example = "249.25")
        BigDecimal amount,
        @Schema(description = "Timestamp when the transaction was recorded")
        Instant timestamp
) {
}
