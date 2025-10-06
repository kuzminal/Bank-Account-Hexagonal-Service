package ru.kuzmin.bankaccount.domain.ports.out;

import ru.kuzmin.bankaccount.domain.event.DomainEvent;

public interface BankAccountNotificationPort {
    void notify(DomainEvent event);
}
