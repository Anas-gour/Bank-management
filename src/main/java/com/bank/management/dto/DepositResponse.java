package com.bank.management.dto;

import java.math.BigDecimal;

public class DepositResponse {

    private String message;
    private Long transactionId;
    private Long accountId;
    private String accountNumber;
    private BigDecimal amount;
    private String transactionType;

    public DepositResponse() {
    }

    public DepositResponse(
            String message,
            Long transactionId,
            Long accountId,
            String accountNumber,
            BigDecimal amount,
            String transactionType) {

        this.message = message;
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.transactionType = transactionType;
    }

    public String getMessage() {
        return message;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getTransactionType() {
        return transactionType;
    }
}