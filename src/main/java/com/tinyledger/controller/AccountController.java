package com.tinyledger.controller;

import com.tinyledger.documentation.AccountIdParameter;
import com.tinyledger.documentation.BadRequestResponse;
import com.tinyledger.documentation.InsufficientFundsResponse;
import com.tinyledger.documentation.InternalServerErrorResponse;
import com.tinyledger.documentation.NotFoundResponse;
import com.tinyledger.dto.account.AccountDto;
import com.tinyledger.dto.account.CreateAccountRequestDto;
import com.tinyledger.dto.transaction.DepositRequest;
import com.tinyledger.dto.transaction.TransactionDto;
import com.tinyledger.dto.transaction.WithdrawalRequest;
import com.tinyledger.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Accounts", description = "Operations for managing bank accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    @Operation(summary = "List all accounts", description = "Returns every account currently stored.")
    @InternalServerErrorResponse
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Accounts returned successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AccountDto.class)))
            )
    })
    public List<AccountDto> getAccounts() {
        return accountService.getAccounts();
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "Get an account by id", description = "Returns the account details for the given account identifier.")
    @NotFoundResponse
    @InternalServerErrorResponse
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Account found",
                    content = @Content(schema = @Schema(implementation = AccountDto.class))
            )
    })
    public AccountDto getAccountById(
            @AccountIdParameter
            @PathVariable UUID accountId) {
        return accountService.getAccountById(accountId);
    }

    @GetMapping("/{accountId}/balance")
    @Operation(summary = "Get an account balance", description = "Returns the current balance for the given account.")
    @NotFoundResponse
    @InternalServerErrorResponse
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Balance returned successfully",
                    content = @Content(schema = @Schema(implementation = BigDecimal.class))
            )
    })
    public BigDecimal getAccountBalance(
            @AccountIdParameter
            @PathVariable UUID accountId) {
        return accountService.getAccountBalance(accountId);
    }

    @GetMapping("/{accountId}/transactions")
    @Operation(summary = "Get transaction history", description = "Returns all account transactions ordered by date.")
    @NotFoundResponse
    @InternalServerErrorResponse
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Transaction history returned successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TransactionDto.class)))
            )
    })
    public List<TransactionDto> getAccountTransactions(
            @AccountIdParameter
            @PathVariable UUID accountId) {
        return accountService.getAccountTransactions(accountId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new account", description = "Creates a new account with a zero balance.")
    @BadRequestResponse
    @InternalServerErrorResponse
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Account created successfully",
                    content = @Content(schema = @Schema(implementation = AccountDto.class))
            )
    })
    public AccountDto createAccount(@Valid @RequestBody CreateAccountRequestDto request) {
        return accountService.createAccount(request);
    }

    @PostMapping("/{accountId}/transactions/deposit")
    @Operation(summary = "Deposit money", description = "Deposits money into the given account.")
    @BadRequestResponse
    @NotFoundResponse
    @InternalServerErrorResponse
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Transaction recorded successfully",
                    content = @Content(schema = @Schema(implementation = TransactionDto.class))
            )
    })
    public TransactionDto deposit(
            @AccountIdParameter
            @PathVariable UUID accountId,
            @Valid @RequestBody DepositRequest request) {
        return accountService.deposit(accountId, request);
    }

    @PostMapping("/{accountId}/transactions/withdrawal")
    @Operation(summary = "Withdraw money", description = "Withdraws money from the given account.")
    @BadRequestResponse
    @NotFoundResponse
    @InsufficientFundsResponse
    @InternalServerErrorResponse
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Transaction recorded successfully",
                    content = @Content(schema = @Schema(implementation = TransactionDto.class))
            )
    })
    public TransactionDto withdrawal(
            @AccountIdParameter
            @PathVariable UUID accountId,
            @Valid @RequestBody WithdrawalRequest request) {
        return accountService.withdrawal(accountId, request);
    }
}
