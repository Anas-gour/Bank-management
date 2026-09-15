package com.bank.management.service;

import com.bank.management.dto.AccountRequest;
import com.bank.management.entity.Account;
import com.bank.management.entity.Customer;
import com.bank.management.exception.AccountNotFoundException;
import com.bank.management.exception.CustomerNotFoundException;
import com.bank.management.repository.AccountRepository;
import com.bank.management.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public AccountService(
            AccountRepository accountRepository,
            CustomerRepository customerRepository) {

        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    public Account createAccount(AccountRequest request) {

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() ->
                        new CustomerNotFoundException("Customer not found"));

        Account account = new Account();

        account.setAccountNumber(request.getAccountNumber());
        account.setAccountType(request.getAccountType());
        account.setBalance(request.getBalance());
        account.setCustomer(customer);

        return accountRepository.save(account);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() ->
                        new AccountNotFoundException("Account not found"));
    }

    public Account updateAccount(Long id, Account account) {

        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() ->
                        new AccountNotFoundException("Account not found"));

        existingAccount.setAccountNumber(account.getAccountNumber());
        existingAccount.setAccountType(account.getAccountType());
        existingAccount.setBalance(account.getBalance());
        existingAccount.setStatus(account.getStatus());

        return accountRepository.save(existingAccount);
    }

    public void deleteAccount(Long id) {

        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() ->
                        new AccountNotFoundException("Account not found"));

        accountRepository.delete(existingAccount);
    }
}