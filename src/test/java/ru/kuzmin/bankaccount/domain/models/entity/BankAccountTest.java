package ru.kuzmin.bankaccount.domain.models.entity;

import org.junit.jupiter.api.Test;
import ru.kuzmin.bankaccount.domain.exception.InsufficientFundsException;
import ru.kuzmin.bankaccount.domain.exception.InvalidAmountException;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BankAccountTest {

    private static BankAccount account(String balance) {
        return new BankAccount(1L, "Owner", new BigDecimal(balance), 0L);
    }

    @Test
    void deposit_increasesBalance() {
        BankAccount account = account("100.00");
        account.deposit(new BigDecimal("50.00"));
        assertThat(account.getBalance()).isEqualByComparingTo("150.00");
    }

    @Test
    void withdraw_decreasesBalance() {
        BankAccount account = account("100.00");
        account.withdraw(new BigDecimal("40.00"));
        assertThat(account.getBalance()).isEqualByComparingTo("60.00");
    }

    @Test
    void withdraw_moreThanBalance_throwsInsufficientFunds() {
        BankAccount account = account("100.00");
        assertThatThrownBy(() -> account.withdraw(new BigDecimal("100.01")))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void deposit_invalidAmount_throws() {
        BankAccount account = account("100.00");
        assertThatThrownBy(() -> account.deposit(BigDecimal.ZERO))
                .isInstanceOf(InvalidAmountException.class);
        assertThatThrownBy(() -> account.deposit(new BigDecimal("-1")))
                .isInstanceOf(InvalidAmountException.class);
        assertThatThrownBy(() -> account.deposit(null))
                .isInstanceOf(InvalidAmountException.class);
    }

    @Test
    void withdraw_invalidAmount_throws() {
        BankAccount account = account("100.00");
        assertThatThrownBy(() -> account.withdraw(BigDecimal.ZERO))
                .isInstanceOf(InvalidAmountException.class);
        assertThatThrownBy(() -> account.withdraw(new BigDecimal("-1")))
                .isInstanceOf(InvalidAmountException.class);
        assertThatThrownBy(() -> account.withdraw(null))
                .isInstanceOf(InvalidAmountException.class);
    }

    @Test
    void balance_isScaleSafeForDecimalArithmetic() {
        BankAccount account = account("0.10");
        account.deposit(new BigDecimal("0.20"));
        assertThat(account.getBalance()).isEqualByComparingTo("0.30");
    }
}
