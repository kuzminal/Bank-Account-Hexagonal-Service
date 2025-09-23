package ru.kuzmin.bankaccount.domain.ports.in;

import ru.kuzmin.bankaccount.domain.models.dto.BankAccountDTO;

import java.math.BigDecimal;

public interface BankAccountServicePort {
    BankAccountDTO createAccount(String owner, BigDecimal initialBalance);
    void deposit(Long accountId, BigDecimal amount);
    void withdraw(Long accountId, BigDecimal amount);
    BigDecimal checkBalance(Long accountId);
}
