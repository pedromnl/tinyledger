package com.tinyledger.model;

import com.tinyledger.exception.InsufficientFundsException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Account {
    private final UUID accountId;
    private final String accountName;
    private BigDecimal balance;
    private final List<Transaction> transactions;
    private final Instant createdAt;

    public Account(UUID accountId, String accountName) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.balance = BigDecimal.ZERO;
        this.transactions = new ArrayList<>();
        this.createdAt = Instant.now();
    }

    public UUID getAccountId() {
        return accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public synchronized BigDecimal getBalance() {
        return balance;
    }

    public synchronized List<Transaction> getTransactions() {
        return List.copyOf(transactions);
    }

    public synchronized Transaction deposit(BigDecimal amount) {
        BigDecimal validatedAmount = validatePositiveAmount(amount);

        balance = balance.add(validatedAmount);

        Transaction depositTransaction = new Transaction(
                UUID.randomUUID(),
                TransactionType.DEPOSIT,
                validatedAmount,
                Instant.now()
        );
        transactions.add(depositTransaction);

        return depositTransaction;
    }

    public synchronized Transaction withdraw(BigDecimal amount) {
        BigDecimal validatedAmount = validatePositiveAmount(amount);

        if (balance.compareTo(validatedAmount) < 0) {
            throw new InsufficientFundsException();
        }

        balance = balance.subtract(validatedAmount);

        Transaction withdrawalTransaction = new Transaction(
                UUID.randomUUID(),
                TransactionType.WITHDRAWAL,
                validatedAmount,
                Instant.now()
        );
        transactions.add(withdrawalTransaction);

        return withdrawalTransaction;
    }

    private static BigDecimal validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        return amount;
    }
}
