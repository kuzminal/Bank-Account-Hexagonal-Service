package ru.kuzmin.bankaccount.domain.event;

import lombok.Getter;
import lombok.Setter;
import ru.kuzmin.bankaccount.domain.models.entity.BankAccount;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountWithdrawnEvent extends DomainEvent {
    private final BankAccount account;
    private final BigDecimal withdrawnAmount;
    public AccountWithdrawnEvent(BankAccount account, BigDecimal withdrawnAmount) {
        this.account = account;
        this.withdrawnAmount = withdrawnAmount;
    }
}
