package com.bank.management.dto;

import java.math.BigDecimal;

public class TransferResponse {

    private String message;
    private Long debitTransactionId;
    private Long creditTransactionId;
    private Long fromAccountId;
    private String fromAccountNumber;
    private Long toAccountId;
    private String toAccountNumber;
    private BigDecimal amount;

    public TransferResponse() {
    }

    public TransferResponse(
            String message,
            Long debitTransactionId,
            Long creditTransactionId,
            Long fromAccountId,
            String fromAccountNumber,
            Long toAccountId,
            String toAccountNumber,
            BigDecimal amount) {

        this.message = message;
        this.debitTransactionId = debitTransactionId;
        this.creditTransactionId = creditTransactionId;
        this.fromAccountId = fromAccountId;
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountId = toAccountId;
        this.toAccountNumber = toAccountNumber;
        this.amount = amount;
    }

    public String getMessage() {
        return message;
    }

    public Long getDebitTransactionId() {
        return debitTransactionId;
    }

    public Long getCreditTransactionId() {
        return creditTransactionId;
    }

    public Long getFromAccountId() {
        return fromAccountId;
    }

    public String getFromAccountNumber() {
        return fromAccountNumber;
    }

    public Long getToAccountId() {
        return toAccountId;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}