package com.bank.management.service;

import com.bank.management.entity.Transaction;
import com.bank.management.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import com.bank.management.exception.TransactionNotFoundException;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.bank.management.exception.InsufficientBalanceException;

import com.bank.management.dto.DepositRequest;
import com.bank.management.dto.WithdrawRequest;
import com.bank.management.dto.TransferRequest;
import com.bank.management.dto.TransferResponse;
import com.bank.management.dto.TransactionResponse;
import com.bank.management.dto.DepositResponse;
import com.bank.management.dto.WithdrawResponse;

import com.bank.management.entity.AccountStatus;
import com.bank.management.entity.Account;
import com.bank.management.repository.AccountRepository;
import com.bank.management.exception.AccountNotFoundException;

import java.math.BigDecimal;


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

    public List<TransactionResponse> getAllTransactions() {

        return transactionRepository.findAll()
                .stream()
                .map(transaction -> {

                    if (transaction.getAccount() == null) {
                        return new TransactionResponse(
                                transaction.getId(),
                                transaction.getTransactionType(),
                                transaction.getAmount(),
                                null,
                                null
                        );
                    }

                    return new TransactionResponse(
                            transaction.getId(),
                            transaction.getTransactionType(),
                            transaction.getAmount(),
                            transaction.getAccount().getId(),
                            transaction.getAccount().getAccountNumber()
                    );
                })
                .toList();
    }

    public TransactionResponse getTransactionById(Long id) {

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() ->
                        new TransactionNotFoundException("Transaction not found"));

        if (transaction.getAccount() == null) {
            return new TransactionResponse(
                    transaction.getId(),
                    transaction.getTransactionType(),
                    transaction.getAmount(),
                    null,
                    null
            );
        }

        return new TransactionResponse(
                transaction.getId(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getAccount().getId(),
                transaction.getAccount().getAccountNumber()
        );
    }

    @Transactional
    public DepositResponse deposit(DepositRequest request) {

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() ->
                        new AccountNotFoundException("Account not found"));
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active");
        }

        BigDecimal newBalance =
                account.getBalance().add(request.getAmount());

        account.setBalance(newBalance);

        Transaction transaction = new Transaction();

        transaction.setTransactionType("DEPOSIT");
        transaction.setAmount(request.getAmount());
        transaction.setAccount(account);

        Transaction savedTransaction = transactionRepository.save(transaction);

        return new DepositResponse(
                "Deposit successful",
                savedTransaction.getId(),
                account.getId(),
                account.getAccountNumber(),
                savedTransaction.getAmount(),
                savedTransaction.getTransactionType()
        );
    }

    @Transactional
    public WithdrawResponse withdraw(WithdrawRequest request) {
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() ->
                        new AccountNotFoundException("Account not found"));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active");
        }

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

        Transaction savedTransaction = transactionRepository.save(transaction);

        return new WithdrawResponse(
                "Withdrawal successful",
                savedTransaction.getId(),
                account.getId(),
                account.getAccountNumber(),
                savedTransaction.getAmount(),
                savedTransaction.getTransactionType()
        );
    }

    @Transactional
    public TransferResponse transfer(TransferRequest request) {

        Account fromAccount = accountRepository.findById(request.getFromAccountId())
                .orElseThrow(() ->
                        new AccountNotFoundException("Source account not found"));

        Account toAccount = accountRepository.findById(request.getToAccountId())
                .orElseThrow(() ->
                        new AccountNotFoundException("Destination account not found"));

        if (fromAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Source account is not active");
        }

        if (toAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Destination account is not active");
        }

        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new IllegalArgumentException(
                    "Source and destination accounts cannot be the same"
            );
        }
        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        BigDecimal newFromBalance =
                fromAccount.getBalance().subtract(request.getAmount());

        BigDecimal newToBalance =
                toAccount.getBalance().add(request.getAmount());

        fromAccount.setBalance(newFromBalance);
        toAccount.setBalance(newToBalance);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // Debit transaction
        Transaction debitTransaction = new Transaction();
        debitTransaction.setTransactionType("TRANSFER_DEBIT");
        debitTransaction.setAmount(request.getAmount());
        debitTransaction.setAccount(fromAccount);

        Transaction savedDebitTransaction =
                transactionRepository.save(debitTransaction);


// Credit transaction
        Transaction creditTransaction = new Transaction();
        creditTransaction.setTransactionType("TRANSFER_CREDIT");
        creditTransaction.setAmount(request.getAmount());
        creditTransaction.setAccount(toAccount);

        Transaction savedCreditTransaction =
                transactionRepository.save(creditTransaction);


// Return response
        return new TransferResponse(
                "Transfer successful",
                savedDebitTransaction.getId(),
                savedCreditTransaction.getId(),
                fromAccount.getId(),
                fromAccount.getAccountNumber(),
                toAccount.getId(),
                toAccount.getAccountNumber(),
                request.getAmount()
        );
    }
}