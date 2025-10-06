package ru.kuzmin.bankaccount.domain.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.kuzmin.bankaccount.domain.models.dto.BankAccountDTO;

@EqualsAndHashCode(callSuper = true)
@Data
public class AccountCreatedEvent extends DomainEvent {
    private final BankAccountDTO account;

    public AccountCreatedEvent(BankAccountDTO account) {
        this.account = account;
    }
}
