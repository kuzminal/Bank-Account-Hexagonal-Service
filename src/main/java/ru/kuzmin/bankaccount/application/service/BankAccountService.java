package ru.kuzmin.bankaccount.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kuzmin.bankaccount.domain.event.AccountCreatedEvent;
import ru.kuzmin.bankaccount.domain.event.AccountDepositedEvent;
import ru.kuzmin.bankaccount.domain.event.AccountWithdrawnEvent;
import ru.kuzmin.bankaccount.domain.exception.AccountNotFoundException;
import ru.kuzmin.bankaccount.domain.exception.InvalidAmountException;
import ru.kuzmin.bankaccount.domain.models.dto.BankAccountDTO;
import ru.kuzmin.bankaccount.domain.models.entity.BankAccount;
import ru.kuzmin.bankaccount.domain.ports.in.BankAccountServicePort;
import ru.kuzmin.bankaccount.domain.ports.out.BankAccountRepositoryPort;
import ru.kuzmin.bankaccount.domain.ports.out.OutboxRepositoryPort;
import ru.kuzmin.bankaccount.application.mappers.BankAccountMapper;

import java.math.BigDecimal;

@Service
public class BankAccountService implements BankAccountServicePort {

    private final BankAccountRepositoryPort repository;
    private final OutboxRepositoryPort outbox;

    public BankAccountService(BankAccountRepositoryPort repository, OutboxRepositoryPort outbox) {
        this.repository = repository;
        this.outbox = outbox;
    }

    @Override
    @Transactional
    public BankAccountDTO createAccount(String owner, BigDecimal initialBalance) {
        if (initialBalance == null || initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException("Initial balance must be non-negative");
        }
        BankAccount saved = repository.save(new BankAccount(null, owner, initialBalance, null));
        outbox.save(new AccountCreatedEvent(saved.getId(), saved.getOwner(), saved.getBalance()));
        return BankAccountMapper.INSTANCE.toBankAccountDto(saved);
    }

    @Override
    @Transactional
    public void deposit(Long accountId, BigDecimal amount) {
        BankAccount account = repository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        account.deposit(amount);
        BankAccount saved = repository.save(account);
        outbox.save(new AccountDepositedEvent(saved.getId(), amount, saved.getBalance()));
    }

    @Override
    @Transactional
    public void withdraw(Long accountId, BigDecimal amount) {
        BankAccount account = repository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        account.withdraw(amount);
        BankAccount saved = repository.save(account);
        outbox.save(new AccountWithdrawnEvent(saved.getId(), amount, saved.getBalance()));
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal checkBalance(Long accountId) {
        return repository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId))
                .getBalance();
    }
}
