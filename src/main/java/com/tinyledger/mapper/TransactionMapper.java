package com.tinyledger.mapper;

import com.tinyledger.dto.transaction.TransactionDto;
import com.tinyledger.model.Transaction;

public final class TransactionMapper {

    public static TransactionDto toDto(Transaction transaction) {
        return new TransactionDto(
                transaction.transactionId(),
                transaction.transactionType(),
                transaction.amount(),
                transaction.timestamp()
        );
    }
}
