package com.tinyledger.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "CreateAccountRequest", description = "Details of the account to create")
public record CreateAccountRequestDto(
        @Schema(description = "Human-readable account name", example = "Checking Account")
        @NotBlank(message = "Account name is required")
        @Size(min = 3, message = "Account name must contain at least 3 characters")
        String accountName
) {
}
