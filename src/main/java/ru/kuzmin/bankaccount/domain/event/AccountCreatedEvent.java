package ru.kuzmin.bankaccount.domain.event;

import java.math.BigDecimal;

/**
 * Событие создания счёта. Содержит только данные контракта события,
 * а не ссылку на сущность — это защищает потребителей от внутренней структуры домена.
 */
public class AccountCreatedEvent extends DomainEvent {

    private final Long accountId;
    private final String owner;
    private final BigDecimal initialBalance;

    public AccountCreatedEvent(Long accountId, String owner, BigDecimal initialBalance) {
        this.accountId = accountId;
        this.owner = owner;
        this.initialBalance = initialBalance;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getOwner() {
        return owner;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    @Override
    public String aggregateId() {
        return String.valueOf(accountId);
    }
}
