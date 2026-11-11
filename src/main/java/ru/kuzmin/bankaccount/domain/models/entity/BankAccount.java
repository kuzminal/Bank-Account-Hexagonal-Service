package ru.kuzmin.bankaccount.domain.models.entity;

import ru.kuzmin.bankaccount.domain.exception.InsufficientFundsException;
import ru.kuzmin.bankaccount.domain.exception.InvalidAmountException;

import java.math.BigDecimal;

/**
 * Доменный агрегат «банковский счёт».
 * Чистая модель без зависимостей от JPA, Spring и прочей инфраструктуры.
 * {@code version} используется для оптимистичной блокировки (persistence-слой).
 */
public class BankAccount {

    private final Long id;
    private final String owner;
    private BigDecimal balance;
    private final Long version;

    public BankAccount(Long id, String owner, BigDecimal balance, Long version) {
        this.id = id;
        this.owner = owner;
        this.balance = balance;
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Long getVersion() {
        return version;
    }

    public void deposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be positive");
        }
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be positive");
        }
        if (amount.compareTo(this.balance) > 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        this.balance = this.balance.subtract(amount);
    }
}
