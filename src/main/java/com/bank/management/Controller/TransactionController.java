package com.bank.management.Controller;

import com.bank.management.service.TransactionService;
import org.springframework.web.bind.annotation.*;

import com.bank.management.entity.Transaction;

import jakarta.validation.Valid;
import java.util.List;
import com.bank.management.dto.DepositRequest;
import com.bank.management.dto.WithdrawRequest;
import com.bank.management.dto.TransferRequest;
import com.bank.management.dto.TransactionResponse;
import com.bank.management.dto.DepositResponse;
import com.bank.management.dto.WithdrawResponse;
import com.bank.management.dto.TransferResponse;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<TransactionResponse> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @GetMapping("/{id}")
    public TransactionResponse getTransactionById(@PathVariable Long id) {
        return transactionService.getTransactionById(id);
    }

    @PostMapping("/deposit")
    public DepositResponse deposit(@Valid @RequestBody DepositRequest request) {
        return transactionService.deposit(request);
    }

    @PostMapping("/withdraw")
    public WithdrawResponse withdraw(@Valid @RequestBody WithdrawRequest request) {
        return transactionService.withdraw(request);
    }

    @PostMapping("/transfer")
    public TransferResponse transfer(@Valid @RequestBody TransferRequest request) {
        return transactionService.transfer(request);
    }
}