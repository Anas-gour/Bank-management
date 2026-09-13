package com.bank.management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_seq_gen")
    @SequenceGenerator(
            name = "transaction_seq_gen",
            sequenceName = "transaction_seq",
            allocationSize = 1
    )
    private Long id;

    @Setter
    private String transactionType;

    @Setter
    private BigDecimal amount;

    @Setter
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    public Transaction() {
    }

}