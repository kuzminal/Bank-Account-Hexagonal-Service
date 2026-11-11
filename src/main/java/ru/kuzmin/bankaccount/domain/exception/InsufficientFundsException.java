package ru.kuzmin.bankaccount.domain.exception;

/**
 * Недостаточно средств для операции.
 */
public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(String message) {
        super(message);
    }
}
