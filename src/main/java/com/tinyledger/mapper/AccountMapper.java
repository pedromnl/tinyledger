package com.tinyledger.mapper;

import com.tinyledger.dto.account.AccountDto;
import com.tinyledger.dto.account.CreateAccountRequestDto;
import com.tinyledger.model.Account;

import java.util.UUID;

public final class AccountMapper {

    public static Account toModel(CreateAccountRequestDto dto) {
        return new Account(UUID.randomUUID(), dto.accountName());
    }

    public static AccountDto toDto(Account account) {
        return new AccountDto(
                account.getAccountId(),
                account.getAccountName(),
                account.getCreatedAt());
    }
}
