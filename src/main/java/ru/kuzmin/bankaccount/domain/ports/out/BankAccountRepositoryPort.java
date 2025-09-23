package ru.kuzmin.bankaccount.domain.ports.out;

import ru.kuzmin.bankaccount.domain.models.entity.BankAccount;

import java.util.Optional;

public interface BankAccountRepositoryPort {
    BankAccount save(BankAccount account);
    Optional<BankAccount> findById(Long id);
}
