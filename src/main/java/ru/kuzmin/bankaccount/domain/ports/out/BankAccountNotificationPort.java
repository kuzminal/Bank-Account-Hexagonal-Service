package ru.kuzmin.bankaccount.domain.ports.out;

import ru.kuzmin.bankaccount.domain.event.DomainEvent;
import ru.kuzmin.bankaccount.domain.models.entity.BankAccount;

public interface BankAccountNotificationPort {
    void notify(DomainEvent event);
}
