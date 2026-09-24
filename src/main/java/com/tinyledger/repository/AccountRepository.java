package com.tinyledger.repository;

import com.tinyledger.model.Account;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AccountRepository {

    private final ConcurrentHashMap<UUID, Account> accountStore = new ConcurrentHashMap<>();

    @PostConstruct
    public void initializeAccountStore() {
        Account checkingAccount = new Account(UUID.randomUUID(), "Checking Account");
        checkingAccount.deposit(new BigDecimal("1250.75"));
        checkingAccount.withdraw(new BigDecimal("250.75"));

        Account savingsAccount = new Account(UUID.randomUUID(), "Savings Account");
        savingsAccount.deposit(new BigDecimal("5000.00"));
        savingsAccount.deposit(new BigDecimal("1500.00"));

        accountStore.put(checkingAccount.getAccountId(), checkingAccount);
        accountStore.put(savingsAccount.getAccountId(), savingsAccount);
    }

    public List<Account> getAccounts() {
        return List.copyOf(accountStore.values());
    }

    public Account getAccountById(UUID accountId) {
        return accountStore.get(accountId);
    }

    public Account createAccount(Account account) {
        accountStore.put(account.getAccountId(), account);
        return account;
    }

}
