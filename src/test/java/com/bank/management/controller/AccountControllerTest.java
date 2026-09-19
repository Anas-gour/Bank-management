package com.bank.management.controller;

import com.bank.management.Controller.AccountController;
import com.bank.management.service.AccountService;

import java.util.List;
import com.bank.management.service.JwtService;
import com.bank.management.entity.Account;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import com.bank.management.dto.AccountRequest;

import org.springframework.http.MediaType;
import static org.mockito.ArgumentMatchers.any;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.bank.management.entity.AccountStatus;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldGetAllAccounts() throws Exception {

        when(accountService.getAllAccounts())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void shouldGetAccountById() throws Exception {

        Account account = new Account();
        account.setAccountNumber("ACC1003");
        account.setAccountType("SAVINGS");
        account.setBalance(new BigDecimal("5000"));

        when(accountService.getAccountById(21L))
                .thenReturn(account);

        mockMvc.perform(get("/api/accounts/21"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    {
                        "accountNumber": "ACC1003",
                        "accountType": "SAVINGS",
                        "balance": 5000
                    }
                    """));
    }

    @Test
    void shouldCreateAccount() throws Exception {

        Account account = new Account();
        account.setAccountNumber("ACC1005");
        account.setAccountType("SAVINGS");
        account.setBalance(new BigDecimal("10000"));

        when(accountService.createAccount(any(AccountRequest.class)))
                .thenReturn(account);

        mockMvc.perform(
                        post("/api/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "accountNumber": "ACC1005",
                                        "accountType": "SAVINGS",
                                        "balance": 10000,
                                        "customerId": 21
                                    }
                                    """)
                )
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    {
                        "accountNumber": "ACC1005",
                        "accountType": "SAVINGS",
                        "balance": 10000
                    }
                    """));
    }

    @Test
    void shouldUpdateAccount() throws Exception {

        Account account = new Account();

        account.setAccountNumber("ACC1003-UPDATED");
        account.setAccountType("CURRENT");
        account.setBalance(new BigDecimal("5000"));
        account.setStatus(AccountStatus.ACTIVE);

        when(accountService.updateAccount(
                eq(21L),
                any(Account.class)
        )).thenReturn(account);

        mockMvc.perform(
                        put("/api/accounts/21")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "accountNumber": "ACC1003-UPDATED",
                                        "accountType": "CURRENT",
                                        "balance": 999999,
                                        "status": "ACTIVE"
                                    }
                                    """)
                )
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    {
                        "accountNumber": "ACC1003-UPDATED",
                        "accountType": "CURRENT",
                        "balance": 5000,
                        "status": "ACTIVE"
                    }
                    """));
    }

    @Test
    void shouldDeleteAccount() throws Exception {

        mockMvc.perform(
                        delete("/api/accounts/21")
                )
                .andExpect(status().isOk());
    }
}