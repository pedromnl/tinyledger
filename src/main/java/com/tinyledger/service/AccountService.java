package com.tinyledger.service;

import com.tinyledger.dto.account.CreateAccountRequestDto;
import com.tinyledger.dto.transaction.DepositRequest;
import com.tinyledger.dto.account.AccountDto;
import com.tinyledger.dto.transaction.TransactionDto;
import com.tinyledger.dto.transaction.WithdrawalRequest;
import com.tinyledger.mapper.AccountMapper;
import com.tinyledger.mapper.TransactionMapper;
import com.tinyledger.model.Account;
import com.tinyledger.model.Transaction;
import com.tinyledger.exception.AccountNotFoundException;
import com.tinyledger.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<AccountDto> getAccounts() {
        return accountRepository.getAccounts().stream()
                .map(AccountMapper::toDto)
                .toList();
    }

    public AccountDto getAccountById(UUID accountId) {
        Account account = getAccountOrThrowAccountNotFoundException(accountId);

        return AccountMapper.toDto(account);
    }

    public BigDecimal getAccountBalance(UUID accountId) {
        Account account = getAccountOrThrowAccountNotFoundException(accountId);

        return account.getBalance();
    }

    public List<TransactionDto> getAccountTransactions(UUID accountId) {
        Account account = getAccountOrThrowAccountNotFoundException(accountId);

        return account.getTransactions().stream()
                .sorted(Comparator.comparing(Transaction::timestamp).reversed()) // show most recent transactions first
                .map(TransactionMapper::toDto)
                .toList();
    }

    public AccountDto createAccount(CreateAccountRequestDto createAccountRequestDto) {
        Account accountToCreate = AccountMapper.toModel(createAccountRequestDto);

        Account createdAccount = accountRepository.createAccount(accountToCreate);

        return AccountMapper.toDto(createdAccount);
    }

    public TransactionDto deposit(UUID accountId, DepositRequest request) {
        Account accountToDeposit = getAccountOrThrowAccountNotFoundException(accountId);

        Transaction depositTransaction = accountToDeposit.deposit(request.amount());

        return TransactionMapper.toDto(depositTransaction);
    }

    public TransactionDto withdrawal(UUID accountId, WithdrawalRequest request) {
        Account accountToWithdraw = getAccountOrThrowAccountNotFoundException(accountId);

        Transaction withdrawalTransaction = accountToWithdraw.withdraw(request.amount());

        return TransactionMapper.toDto(withdrawalTransaction);
    }

    private Account getAccountOrThrowAccountNotFoundException(UUID accountId) {
        return Optional.ofNullable(accountRepository.getAccountById(accountId))
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }
}
