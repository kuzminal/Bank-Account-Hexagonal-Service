package ru.kuzmin.bankaccount.domain.exception;

/**
 * Некорректная сумма операции (отрицательная, нулевая или null).
 */
public class InvalidAmountException extends RuntimeException {

    public InvalidAmountException(String message) {
        super(message);
    }
}
