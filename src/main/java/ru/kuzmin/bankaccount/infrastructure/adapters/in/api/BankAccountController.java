package ru.kuzmin.bankaccount.infrastructure.adapters.in.api;

import org.springframework.web.bind.annotation.*;
import ru.kuzmin.bankaccount.domain.models.dto.Transfer;
import ru.kuzmin.bankaccount.domain.ports.in.BankAccountServicePort;

import java.math.BigDecimal;

@RestController
@RequestMapping("/accounts")
public class BankAccountController {

    private final BankAccountServicePort service;

    public BankAccountController(BankAccountServicePort service) {
        this.service = service;
    }

    @PostMapping
    public Long createAccount(@RequestBody CreateAccountRequest request) {
        return service.createAccount(request.owner(), request.initialBalance()).id();
    }

    @PostMapping("/{id}/deposit")
    public void deposit(@PathVariable Long id, @RequestBody Transfer transfer) {
        service.deposit(id, transfer.amount());
    }

    @PostMapping("/{id}/withdraw")
    public void withdraw(@PathVariable Long id, @RequestBody Transfer transfer) {
        service.withdraw(id, transfer.amount());
    }

    @GetMapping("/{id}/balance")
    public BigDecimal checkBalance(@PathVariable Long id) {
        return service.checkBalance(id);
    }

    public record CreateAccountRequest(String owner, BigDecimal initialBalance) {}
}
