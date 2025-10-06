package ru.kuzmin.bankaccount.application.service;

import org.springframework.stereotype.Service;
import ru.kuzmin.bankaccount.domain.event.AccountCreatedEvent;
import ru.kuzmin.bankaccount.domain.event.AccountDepositedEvent;
import ru.kuzmin.bankaccount.domain.event.AccountWithdrawnEvent;
import ru.kuzmin.bankaccount.domain.models.entity.BankAccount;
import ru.kuzmin.bankaccount.domain.models.dto.BankAccountDTO;
import ru.kuzmin.bankaccount.domain.ports.out.BankAccountNotificationPort;
import ru.kuzmin.bankaccount.domain.ports.out.BankAccountRepositoryPort;
import ru.kuzmin.bankaccount.domain.ports.in.BankAccountServicePort;
import ru.kuzmin.bankaccount.infrastructure.mappers.BankAccountMapper;

import java.math.BigDecimal;

@Service
public class BankAccountService implements BankAccountServicePort {

    private final BankAccountRepositoryPort repository;
    private final BankAccountNotificationPort notification;

    public BankAccountService(BankAccountRepositoryPort repository, BankAccountNotificationPort notification) {
        this.repository = repository;
        this.notification = notification;
    }

    @Override
    public BankAccountDTO createAccount(String owner, BigDecimal initialBalance) {
        BankAccount account = new BankAccount(null, owner, initialBalance);
        var response = BankAccountMapper.INSTANCE.toBankAccountDto(repository.save(account));
        notification.notify(new AccountCreatedEvent(response));
        return response;
    }

    @Override
    public void deposit(Long accountId, BigDecimal amount) {
        BankAccount account = repository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.deposit(amount);
        repository.save(account);
        notification.notify(new AccountDepositedEvent(BankAccountMapper.INSTANCE.toBankAccountDto(account), amount));
    }

    @Override
    public void withdraw(Long accountId, BigDecimal amount) {
        BankAccount account = repository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.withdraw(amount);
        repository.save(account);
        notification.notify(new AccountWithdrawnEvent(BankAccountMapper.INSTANCE.toBankAccountDto(account), amount));
    }

    @Override
    public BigDecimal checkBalance(Long accountId) {
        return repository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"))
                .getBalance();
    }
}
