package com.tinyledger.model;

import com.tinyledger.exception.InsufficientFundsException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private static final String TEST_ACCOUNT_NAME = "Test Account";
    private static final UUID TEST_ACCOUNT_UUID = UUID.randomUUID();

    @Test
    void constructor_validParameters_shouldCreateAccountWithCorrectValues() {
        UUID accountId = TEST_ACCOUNT_UUID;
        Account account = new Account(accountId, TEST_ACCOUNT_NAME);

        assertEquals(accountId, account.getAccountId());
        assertEquals(TEST_ACCOUNT_NAME, account.getAccountName());
        assertEquals(BigDecimal.ZERO, account.getBalance());
        assertEquals(0, account.getTransactions().size());
        assertNotNull(account.getCreatedAt());
    }

    @Test
    void deposit_positiveAmount_shouldIncreaseBalanceAndRecordTransaction() {
        Account account = new Account(TEST_ACCOUNT_UUID, TEST_ACCOUNT_NAME);
        Transaction transaction = account.deposit(BigDecimal.valueOf(500));

        assertEquals(BigDecimal.valueOf(500), account.getBalance());
        assertEquals(TransactionType.DEPOSIT, transaction.transactionType());
        assertEquals(BigDecimal.valueOf(500), transaction.amount());
        assertEquals(1, account.getTransactions().size());
    }

    @Test
    void deposit_or_withdraw_nonPositiveAmount_shouldThrowIllegalArgumentException() {
        Account account = new Account(TEST_ACCOUNT_UUID, TEST_ACCOUNT_NAME);

        assertThrows(IllegalArgumentException.class, () -> account.deposit(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> account.deposit(BigDecimal.valueOf(-1)));
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> account.withdraw(BigDecimal.valueOf(-1)));
    }

    @Test
    void deposit_multipleDeposits_shouldAccumulateBalance() {
        Account account = new Account(TEST_ACCOUNT_UUID, TEST_ACCOUNT_NAME);
        account.deposit(BigDecimal.valueOf(100));
        account.deposit(BigDecimal.valueOf(200));
        account.deposit(BigDecimal.valueOf(150));

        assertEquals(BigDecimal.valueOf(450), account.getBalance());
        assertEquals(3, account.getTransactions().size());
    }

    @Test
    void withdraw_sufficientBalance_shouldDecreaseBalanceAndRecordTransaction() {
        Account account = new Account(TEST_ACCOUNT_UUID, TEST_ACCOUNT_NAME);
        account.deposit(BigDecimal.valueOf(1000));
        Transaction transaction = account.withdraw(BigDecimal.valueOf(300));

        assertEquals(BigDecimal.valueOf(700), account.getBalance());
        assertEquals(TransactionType.WITHDRAWAL, transaction.transactionType());
        assertEquals(BigDecimal.valueOf(300), transaction.amount());
        assertEquals(2, account.getTransactions().size());
    }

    @Test
    void withdraw_insufficientBalance_shouldThrowInsufficientFundsException() {
        Account account = new Account(TEST_ACCOUNT_UUID, TEST_ACCOUNT_NAME);
        assertThrows(InsufficientFundsException.class, () -> account.withdraw(BigDecimal.valueOf(100)));
    }

    @Test
    void withdraw_exactBalance_shouldResultInZeroBalance() {
        Account account = new Account(TEST_ACCOUNT_UUID, TEST_ACCOUNT_NAME);
        account.deposit(BigDecimal.valueOf(500));
        account.withdraw(BigDecimal.valueOf(500));

        assertEquals(BigDecimal.ZERO, account.getBalance());
    }

    @Test
    void withdraw_zeroBalance_shouldThrowInsufficientFundsException() {
        Account account = new Account(TEST_ACCOUNT_UUID, TEST_ACCOUNT_NAME);
        assertThrows(InsufficientFundsException.class, () -> account.withdraw(BigDecimal.valueOf(1)));
    }

    @Test
    void deposit_and_withdraw_multipleTimes_shouldMaintainCorrectBalance() {
        Account account = new Account(TEST_ACCOUNT_UUID, TEST_ACCOUNT_NAME);
        account.deposit(BigDecimal.valueOf(1000));
        account.withdraw(BigDecimal.valueOf(300));
        account.deposit(BigDecimal.valueOf(500));
        account.withdraw(BigDecimal.valueOf(200));

        assertEquals(BigDecimal.valueOf(1000), account.getBalance());
        assertEquals(4, account.getTransactions().size());

        long depositCount = account.getTransactions().stream()
                .filter(t -> t.transactionType() == TransactionType.DEPOSIT)
                .count();
        long withdrawalCount = account.getTransactions().stream()
                .filter(t -> t.transactionType() == TransactionType.WITHDRAWAL)
                .count();

        assertEquals(2, depositCount);
        assertEquals(2, withdrawalCount);
    }

    @Test
    void getBalance_shouldReturnCurrentBalance() {
        Account account = new Account(TEST_ACCOUNT_UUID, TEST_ACCOUNT_NAME);
        account.deposit(BigDecimal.valueOf(250));

        assertEquals(BigDecimal.valueOf(250), account.getBalance());
    }

    @Test
    void deposit_smallAmount_shouldHandleDecimalPrecision() {
        Account account = new Account(TEST_ACCOUNT_UUID, TEST_ACCOUNT_NAME);
        account.deposit(BigDecimal.valueOf(0.01));
        account.deposit(BigDecimal.valueOf(0.02));

        assertEquals(BigDecimal.valueOf(0.03), account.getBalance());
    }

    @Test
    void withdraw_smallAmount_shouldHandleDecimalPrecision() {
        Account account = new Account(TEST_ACCOUNT_UUID, TEST_ACCOUNT_NAME);
        account.deposit(BigDecimal.valueOf(1.00));
        account.withdraw(BigDecimal.valueOf(0.25));

        assertEquals(BigDecimal.valueOf(0.75), account.getBalance());
    }
}
