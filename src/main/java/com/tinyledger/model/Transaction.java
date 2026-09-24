package com.tinyledger.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Transaction(UUID transactionId, TransactionType transactionType, BigDecimal amount, Instant timestamp) {
}
