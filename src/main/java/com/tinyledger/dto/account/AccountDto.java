package com.tinyledger.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(name = "Account", description = "Account details")
public record AccountDto(
        @Schema(description = "Unique account identifier", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID accountId,
        @Schema(description = "Human-readable account name", example = "Checking Account")
        String accountName,
        @Schema(description = "Account creation date", example = "2023-01-01T00:00:00Z")
        Instant createdAt
) {
}
