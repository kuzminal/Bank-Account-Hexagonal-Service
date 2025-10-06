package ru.kuzmin.bankaccount.domain.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.kuzmin.bankaccount.domain.models.dto.BankAccountDTO;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class AccountDepositedEvent extends DomainEvent {
    private final BankAccountDTO account;
    private final BigDecimal depositedAmount;
    public AccountDepositedEvent(BankAccountDTO account, BigDecimal depositedAmount) {
        this.account = account;
        this.depositedAmount = depositedAmount;
    }
}
