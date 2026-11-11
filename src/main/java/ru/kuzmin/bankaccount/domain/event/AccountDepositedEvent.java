package ru.kuzmin.bankaccount.domain.event;

import java.math.BigDecimal;

/**
 * Событие пополнения счёта.
 */
public class AccountDepositedEvent extends DomainEvent {

    private final Long accountId;
    private final BigDecimal amount;
    private final BigDecimal balanceAfter;

    public AccountDepositedEvent(Long accountId, BigDecimal amount, BigDecimal balanceAfter) {
        this.accountId = accountId;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
    }

    public Long getAccountId() {
        return accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    @Override
    public String aggregateId() {
        return String.valueOf(accountId);
    }
}
