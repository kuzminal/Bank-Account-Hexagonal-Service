package ru.kuzmin.bankaccount.domain.exception;

/**
 * Счёт не найден.
 */
public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(Long accountId) {
        super("Account not found: " + accountId);
    }
}
