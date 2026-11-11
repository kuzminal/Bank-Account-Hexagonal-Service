package ru.kuzmin.bankaccount.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kuzmin.bankaccount.domain.event.AccountCreatedEvent;
import ru.kuzmin.bankaccount.domain.event.AccountDepositedEvent;
import ru.kuzmin.bankaccount.domain.event.DomainEvent;
import ru.kuzmin.bankaccount.domain.exception.AccountNotFoundException;
import ru.kuzmin.bankaccount.domain.exception.InsufficientFundsException;
import ru.kuzmin.bankaccount.domain.exception.InvalidAmountException;
import ru.kuzmin.bankaccount.domain.models.dto.BankAccountDTO;
import ru.kuzmin.bankaccount.domain.models.entity.BankAccount;
import ru.kuzmin.bankaccount.domain.ports.out.BankAccountRepositoryPort;
import ru.kuzmin.bankaccount.domain.ports.out.OutboxRepositoryPort;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {

    @Mock
    private BankAccountRepositoryPort repository;

    @Mock
    private OutboxRepositoryPort outbox;

    private BankAccountService service;

    @BeforeEach
    void setUp() {
        service = new BankAccountService(repository, outbox);
    }

    @Test
    void createAccount_persistsAndPublishesCreatedEvent() {
        BankAccount saved = new BankAccount(1L, "Owner", new BigDecimal("100.00"), 0L);
        when(repository.save(any())).thenReturn(saved);

        BankAccountDTO dto = service.createAccount("Owner", new BigDecimal("100.00"));

        assertThat(dto.id()).isEqualTo(1L);
        verify(outbox).save(any(AccountCreatedEvent.class));
    }

    @Test
    void createAccount_negativeBalance_throwsAndDoesNothing() {
        assertThatThrownBy(() -> service.createAccount("Owner", new BigDecimal("-1")))
                .isInstanceOf(InvalidAmountException.class);
        verifyNoInteractions(repository, outbox);
    }

    @Test
    void deposit_found_persistsAndPublishesDepositedEvent() {
        BankAccount account = new BankAccount(1L, "Owner", new BigDecimal("100.00"), 0L);
        when(repository.findById(1L)).thenReturn(Optional.of(account));
        when(repository.save(any())).thenReturn(account);

        service.deposit(1L, new BigDecimal("50.00"));

        ArgumentCaptor<DomainEvent> captor = ArgumentCaptor.forClass(DomainEvent.class);
        verify(outbox).save(captor.capture());
        assertThat(captor.getValue()).isInstanceOf(AccountDepositedEvent.class);
    }

    @Test
    void deposit_notFound_throws() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deposit(99L, new BigDecimal("10.00")))
                .isInstanceOf(AccountNotFoundException.class);
        verify(outbox, never()).save(any());
    }

    @Test
    void withdraw_insufficientFunds_throwsAndDoesNotPublish() {
        BankAccount account = new BankAccount(1L, "Owner", new BigDecimal("10.00"), 0L);
        when(repository.findById(1L)).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> service.withdraw(1L, new BigDecimal("20.00")))
                .isInstanceOf(InsufficientFundsException.class);
        verifyNoInteractions(outbox);
    }
}
