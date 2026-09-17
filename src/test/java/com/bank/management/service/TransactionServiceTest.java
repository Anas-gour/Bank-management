package com.bank.management.service;

import com.bank.management.repository.AccountRepository;
import com.bank.management.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.bank.management.exception.InsufficientBalanceException;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bank.management.dto.DepositRequest;
import com.bank.management.dto.DepositResponse;

import com.bank.management.dto.WithdrawRequest;
import com.bank.management.dto.WithdrawResponse;
import com.bank.management.entity.Account;
import com.bank.management.entity.AccountStatus;
import com.bank.management.entity.Transaction;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.bank.management.exception.AccountNotActiveException;

import com.bank.management.dto.TransferRequest;
import com.bank.management.dto.TransferResponse;

public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void depositShouldIncreaseBalance() {

        // Arrange
        Account account = new Account();
        account.setBalance(new BigDecimal("5000"));
        account.setAccountNumber("ACC1003-UPDATED");
        account.setStatus(AccountStatus.ACTIVE);

        DepositRequest request = new DepositRequest();
        request.setAccountId(21L);
        request.setAmount(new BigDecimal("1000"));

        when(accountRepository.findById(21L))
                .thenReturn(Optional.of(account));

        Transaction transaction = new Transaction();
        transaction.setTransactionType("DEPOSIT");
        transaction.setAmount(new BigDecimal("1000"));
        transaction.setAccount(account);

        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(transaction);

        // Act
        DepositResponse response = transactionService.deposit(request);

        // Assert
        assertEquals(new BigDecimal("6000"), account.getBalance());
        assertEquals("DEPOSIT", response.getTransactionType());
        assertEquals(new BigDecimal("1000"), response.getAmount());
    }

    @Test
    void withdrawShouldDecreaseBalance() {

        // Arrange
        Account account = new Account();
        account.setBalance(new BigDecimal("5000"));
        account.setAccountNumber("ACC1003-UPDATED");
        account.setStatus(AccountStatus.ACTIVE);

        WithdrawRequest request = new WithdrawRequest();
        request.setAccountId(21L);
        request.setAmount(new BigDecimal("1000"));

        when(accountRepository.findById(21L))
                .thenReturn(Optional.of(account));

        Transaction transaction = new Transaction();
        transaction.setTransactionType("WITHDRAW");
        transaction.setAmount(new BigDecimal("1000"));
        transaction.setAccount(account);

        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(transaction);

        // Act
        WithdrawResponse response = transactionService.withdraw(request);

        // Assert
        assertEquals(new BigDecimal("4000"), account.getBalance());
        assertEquals("WITHDRAW", response.getTransactionType());
        assertEquals(new BigDecimal("1000"), response.getAmount());
    }

    @Test
    void withdrawShouldThrowExceptionWhenBalanceIsInsufficient() {

        // Arrange
        Account account = new Account();
        account.setBalance(new BigDecimal("5000"));
        account.setAccountNumber("ACC1003-UPDATED");
        account.setStatus(AccountStatus.ACTIVE);

        WithdrawRequest request = new WithdrawRequest();
        request.setAccountId(21L);
        request.setAmount(new BigDecimal("10000"));

        when(accountRepository.findById(21L))
                .thenReturn(Optional.of(account));

        // Act & Assert
        assertThrows(
                InsufficientBalanceException.class,
                () -> transactionService.withdraw(request)
        );

        assertEquals(new BigDecimal("5000"), account.getBalance());
    }

    @Test
    void depositShouldThrowExceptionWhenAccountIsBlocked() {

        Account account = new Account();
        account.setBalance(new BigDecimal("5000"));
        account.setAccountNumber("ACC1003-UPDATED");
        account.setStatus(AccountStatus.BLOCKED);

        DepositRequest request = new DepositRequest();
        request.setAccountId(21L);
        request.setAmount(new BigDecimal("1000"));

        when(accountRepository.findById(21L))
                .thenReturn(Optional.of(account));

        assertThrows(
                AccountNotActiveException.class,
                () -> transactionService.deposit(request)
        );
    }

    @Test
    void withdrawShouldThrowExceptionWhenAccountIsBlocked() {

        Account account = new Account();
        account.setBalance(new BigDecimal("5000"));
        account.setAccountNumber("ACC1003-UPDATED");
        account.setStatus(AccountStatus.BLOCKED);

        WithdrawRequest request = new WithdrawRequest();
        request.setAccountId(21L);
        request.setAmount(new BigDecimal("1000"));

        when(accountRepository.findById(21L))
                .thenReturn(Optional.of(account));

        assertThrows(
                AccountNotActiveException.class,
                () -> transactionService.withdraw(request)
        );
    }

    @Test
    void transferShouldUpdateBothAccountBalances() {

        // Arrange
        Account fromAccount = new Account();
        fromAccount.setBalance(new BigDecimal("5000"));
        fromAccount.setAccountNumber("ACC1003");
        fromAccount.setStatus(AccountStatus.ACTIVE);

        Account toAccount = new Account();
        toAccount.setBalance(new BigDecimal("3000"));
        toAccount.setAccountNumber("ACC1004");
        toAccount.setStatus(AccountStatus.ACTIVE);

        TransferRequest request = new TransferRequest();
        request.setFromAccountId(21L);
        request.setToAccountId(22L);
        request.setAmount(new BigDecimal("1000"));

        when(accountRepository.findById(21L))
                .thenReturn(Optional.of(fromAccount));

        when(accountRepository.findById(22L))
                .thenReturn(Optional.of(toAccount));

        Transaction debitTransaction = new Transaction();
        debitTransaction.setTransactionType("TRANSFER_DEBIT");
        debitTransaction.setAmount(new BigDecimal("1000"));
        debitTransaction.setAccount(fromAccount);

        Transaction creditTransaction = new Transaction();
        creditTransaction.setTransactionType("TRANSFER_CREDIT");
        creditTransaction.setAmount(new BigDecimal("1000"));
        creditTransaction.setAccount(toAccount);

        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(debitTransaction)
                .thenReturn(creditTransaction);

        // Act
        TransferResponse response = transactionService.transfer(request);

        // Assert
        assertEquals(new BigDecimal("4000"), fromAccount.getBalance());
        assertEquals(new BigDecimal("4000"), toAccount.getBalance());
        assertEquals(new BigDecimal("1000"), response.getAmount());
    }

    @Test
    void transferShouldThrowExceptionWhenBalanceIsInsufficient() {

        // Arrange
        Account fromAccount = new Account();
        fromAccount.setBalance(new BigDecimal("5000"));
        fromAccount.setAccountNumber("ACC1003");
        fromAccount.setStatus(AccountStatus.ACTIVE);

        Account toAccount = new Account();
        toAccount.setBalance(new BigDecimal("3000"));
        toAccount.setAccountNumber("ACC1004");
        toAccount.setStatus(AccountStatus.ACTIVE);

        TransferRequest request = new TransferRequest();
        request.setFromAccountId(21L);
        request.setToAccountId(22L);
        request.setAmount(new BigDecimal("10000"));

        when(accountRepository.findById(21L))
                .thenReturn(Optional.of(fromAccount));

        when(accountRepository.findById(22L))
                .thenReturn(Optional.of(toAccount));

        // Act + Assert
        assertThrows(
                InsufficientBalanceException.class,
                () -> transactionService.transfer(request)
        );

        // Balance should remain unchanged
        assertEquals(new BigDecimal("5000"), fromAccount.getBalance());
        assertEquals(new BigDecimal("3000"), toAccount.getBalance());
    }

    @Test
    void transferShouldThrowExceptionWhenSourceAccountIsBlocked() {

        // Arrange
        Account fromAccount = new Account();
        fromAccount.setBalance(new BigDecimal("5000"));
        fromAccount.setAccountNumber("ACC1003");
        fromAccount.setStatus(AccountStatus.BLOCKED);

        Account toAccount = new Account();
        toAccount.setBalance(new BigDecimal("3000"));
        toAccount.setAccountNumber("ACC1004");
        toAccount.setStatus(AccountStatus.ACTIVE);

        TransferRequest request = new TransferRequest();
        request.setFromAccountId(21L);
        request.setToAccountId(22L);
        request.setAmount(new BigDecimal("1000"));

        when(accountRepository.findById(21L))
                .thenReturn(Optional.of(fromAccount));

        when(accountRepository.findById(22L))
                .thenReturn(Optional.of(toAccount));

        // Act + Assert
        assertThrows(
                AccountNotActiveException.class,
                () -> transactionService.transfer(request)
        );

        // Balances should remain unchanged
        assertEquals(new BigDecimal("5000"), fromAccount.getBalance());
        assertEquals(new BigDecimal("3000"), toAccount.getBalance());
    }

    @Test
    void transferShouldThrowExceptionWhenDestinationAccountIsBlocked() {

        Account fromAccount = new Account();
        fromAccount.setBalance(new BigDecimal("5000"));
        fromAccount.setAccountNumber("ACC1003");
        fromAccount.setStatus(AccountStatus.ACTIVE);

        Account toAccount = new Account();
        toAccount.setBalance(new BigDecimal("3000"));
        toAccount.setAccountNumber("ACC1004");
        toAccount.setStatus(AccountStatus.BLOCKED);

        TransferRequest request = new TransferRequest();
        request.setFromAccountId(21L);
        request.setToAccountId(22L);
        request.setAmount(new BigDecimal("1000"));

        when(accountRepository.findById(21L))
                .thenReturn(Optional.of(fromAccount));

        when(accountRepository.findById(22L))
                .thenReturn(Optional.of(toAccount));

        assertThrows(
                AccountNotActiveException.class,
                () -> transactionService.transfer(request)
        );

        assertEquals(new BigDecimal("5000"), fromAccount.getBalance());
        assertEquals(new BigDecimal("3000"), toAccount.getBalance());
    }

    @Test
    void transferShouldThrowExceptionWhenSourceAndDestinationAreSame() {

        Account account = new Account();
        account.setBalance(new BigDecimal("5000"));
        account.setAccountNumber("ACC1003");
        account.setStatus(AccountStatus.ACTIVE);

        TransferRequest request = new TransferRequest();
        request.setFromAccountId(21L);
        request.setToAccountId(21L);
        request.setAmount(new BigDecimal("1000"));

        when(accountRepository.findById(21L))
                .thenReturn(Optional.of(account));

        assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.transfer(request)
        );

        assertEquals(new BigDecimal("5000"), account.getBalance());
    }
}