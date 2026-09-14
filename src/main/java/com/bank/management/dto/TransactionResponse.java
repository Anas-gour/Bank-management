package com.bank.management.dto;

import java.math.BigDecimal;

public class TransactionResponse {

    private Long id;
    private String transactionType;
    private BigDecimal amount;
    private Long accountId;
    private String accountNumber;

    public TransactionResponse() {
    }

    public TransactionResponse(
            Long id,
            String transactionType,
            BigDecimal amount,
            Long accountId,
            String accountNumber) {

        this.id = id;
        this.transactionType = transactionType;
        this.amount = amount;
        this.accountId = accountId;
        this.accountNumber = accountNumber;
    }

    public Long getId() {
        return id;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}