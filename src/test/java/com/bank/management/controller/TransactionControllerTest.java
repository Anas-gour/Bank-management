package com.bank.management.controller;

import com.bank.management.service.TransactionService;
import com.bank.management.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bank.management.dto.TransferRequest;
import com.bank.management.dto.TransferResponse;

import static org.mockito.ArgumentMatchers.any;

import java.util.List;

import com.bank.management.dto.WithdrawRequest;
import com.bank.management.dto.WithdrawResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.bank.management.dto.TransactionResponse;
import java.math.BigDecimal;

import com.bank.management.dto.DepositRequest;
import com.bank.management.dto.DepositResponse;

import org.springframework.http.MediaType;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bank.management.controller.TransactionController;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldGetAllTransactions() throws Exception {

        when(transactionService.getAllTransactions())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void shouldGetTransactionById() throws Exception {

        TransactionResponse response = new TransactionResponse(
                1L,
                "DEPOSIT",
                new BigDecimal("5000"),
                21L,
                "ACC1003"
        );

        when(transactionService.getTransactionById(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    {
                        "id": 1,
                        "transactionType": "DEPOSIT",
                        "amount": 5000,
                        "accountId": 21,
                        "accountNumber": "ACC1003"
                    }
                    """));
    }

    @Test
    void shouldDepositMoney() throws Exception {

        DepositResponse response = new DepositResponse(
                "Deposit successful",
                101L,
                21L,
                "ACC1003",
                new BigDecimal("5000"),
                "DEPOSIT"
        );

        when(transactionService.deposit(any(DepositRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/transactions/deposit")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "accountId": 21,
                                        "amount": 5000
                                    }
                                    """)
                )
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    {
                        "message": "Deposit successful",
                        "transactionId": 101,
                        "accountId": 21,
                        "accountNumber": "ACC1003",
                        "amount": 5000,
                        "transactionType": "DEPOSIT"
                    }
                    """));
    }

    @Test
    void shouldWithdrawMoney() throws Exception {

        WithdrawResponse response = new WithdrawResponse(
                "Withdrawal successful",
                102L,
                21L,
                "ACC1003",
                new BigDecimal("1000"),
                "WITHDRAW"
        );

        when(transactionService.withdraw(any(WithdrawRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/transactions/withdraw")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "accountId": 21,
                                        "amount": 1000
                                    }
                                    """)
                )
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    {
                        "message": "Withdrawal successful",
                        "transactionId": 102,
                        "accountId": 21,
                        "accountNumber": "ACC1003",
                        "amount": 1000,
                        "transactionType": "WITHDRAW"
                    }
                    """));
    }

    @Test
    void shouldTransferMoney() throws Exception {

        TransferResponse response = new TransferResponse(
                "Transfer successful",
                103L,
                104L,
                21L,
                "ACC1003",
                22L,
                "ACC1004",
                new BigDecimal("1000")
        );

        when(transactionService.transfer(any(TransferRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/transactions/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "fromAccountId": 21,
                                        "toAccountId": 22,
                                        "amount": 1000
                                    }
                                    """)
                )
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    {
                        "message": "Transfer successful",
                        "debitTransactionId": 103,
                        "creditTransactionId": 104,
                        "fromAccountId": 21,
                        "fromAccountNumber": "ACC1003",
                        "toAccountId": 22,
                        "toAccountNumber": "ACC1004",
                        "amount": 1000
                    }
                    """));
    }
}