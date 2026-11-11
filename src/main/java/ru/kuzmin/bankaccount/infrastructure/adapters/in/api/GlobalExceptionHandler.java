package ru.kuzmin.bankaccount.infrastructure.adapters.in.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.kuzmin.bankaccount.domain.exception.AccountNotFoundException;
import ru.kuzmin.bankaccount.domain.exception.InsufficientFundsException;
import ru.kuzmin.bankaccount.domain.exception.InvalidAmountException;

import java.time.Instant;

/**
 * Глобальный обработчик ошибок: преобразует доменные исключения
 * в корректные HTTP-статусы вместо 500.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(AccountNotFoundException e) {
        return build(HttpStatus.NOT_FOUND, "Account not found", e.getMessage());
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<ApiError> handleInvalidAmount(InvalidAmountException e) {
        return build(HttpStatus.BAD_REQUEST, "Invalid amount", e.getMessage());
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ApiError> handleInsufficientFunds(InsufficientFundsException e) {
        return build(HttpStatus.CONFLICT, "Insufficient funds", e.getMessage());
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiError> handleOptimisticLock(ObjectOptimisticLockingFailureException e) {
        return build(HttpStatus.CONFLICT, "Concurrent modification",
                "Account was modified by another request, please retry");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception e) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", "Unexpected error occurred");
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String error, String message) {
        return ResponseEntity.status(status)
                .body(new ApiError(Instant.now(), status.value(), error, message));
    }

    public record ApiError(Instant timestamp, int status, String error, String message) {
    }
}
