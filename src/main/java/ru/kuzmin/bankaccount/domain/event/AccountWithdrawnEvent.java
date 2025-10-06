package ru.kuzmin.bankaccount.domain.event;

import lombok.Getter;
import lombok.Setter;
import ru.kuzmin.bankaccount.domain.models.dto.BankAccountDTO;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountWithdrawnEvent extends DomainEvent {
    private final BankAccountDTO account;
    private final BigDecimal withdrawnAmount;
    public AccountWithdrawnEvent(BankAccountDTO account, BigDecimal withdrawnAmount) {
        this.account = account;
        this.withdrawnAmount = withdrawnAmount;
    }
}
