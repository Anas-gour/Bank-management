package com.bank.management.dto;

import java.math.BigDecimal;

public class WithdrawRequest {

    private Long accountId;
    private BigDecimal amount;

    public WithdrawRequest() {
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}