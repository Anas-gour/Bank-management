package com.bank.management.service;

import com.bank.management.entity.Transaction;
import com.bank.management.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import com.bank.management.exception.TransactionNotFoundException;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.bank.management.exception.InsufficientBalanceException;

import com.bank.management.dto.DepositRequest;
import com.bank.management.entity.Account;
import com.bank.management.repository.AccountRepository;
import com.bank.management.exception.AccountNotFoundException;

import java.math.BigDecimal;
import com.bank.management.dto.WithdrawRequest;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final AccountRepository accountRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository) {

        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() ->
                        new TransactionNotFoundException("Transaction not found"));
    }

    @Transactional
    public Transaction deposit(DepositRequest request) {

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() ->
                        new AccountNotFoundException("Account not found"));

        BigDecimal newBalance =
                account.getBalance().add(request.getAmount());

        account.setBalance(newBalance);

        accountRepository.save(account);

        Transaction transaction = new Transaction();

        transaction.setTransactionType("DEPOSIT");
        transaction.setAmount(request.getAmount());
        transaction.setAccount(account);

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction withdraw(WithdrawRequest request) {

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() ->
                        new AccountNotFoundException("Account not found"));

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        BigDecimal newBalance =
                account.getBalance().subtract(request.getAmount());

        account.setBalance(newBalance);

        accountRepository.save(account);

        Transaction transaction = new Transaction();

        transaction.setTransactionType("WITHDRAW");
        transaction.setAmount(request.getAmount());
        transaction.setAccount(account);

        return transactionRepository.save(transaction);
    }
}