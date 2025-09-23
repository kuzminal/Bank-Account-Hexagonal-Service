package ru.kuzmin.bankaccount.domain.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.kuzmin.bankaccount.domain.models.entity.BankAccount;

@EqualsAndHashCode(callSuper = true)
@Data
public class AccountCreatedEvent extends DomainEvent {
    private final BankAccount account;

    public AccountCreatedEvent(BankAccount account) {
        this.account = account;
    }
}
