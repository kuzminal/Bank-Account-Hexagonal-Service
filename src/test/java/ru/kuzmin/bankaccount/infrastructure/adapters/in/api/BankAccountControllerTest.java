package ru.kuzmin.bankaccount.infrastructure.adapters.in.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.kuzmin.bankaccount.domain.exception.AccountNotFoundException;
import ru.kuzmin.bankaccount.domain.exception.InsufficientFundsException;
import ru.kuzmin.bankaccount.domain.models.dto.BankAccountDTO;
import ru.kuzmin.bankaccount.domain.ports.in.BankAccountServicePort;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BankAccountController.class)
class BankAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BankAccountServicePort service;

    @Test
    void createAccount_returnsId() throws Exception {
        when(service.createAccount(eq("Owner"), any()))
                .thenReturn(new BankAccountDTO(1L, "Owner", new BigDecimal("100.00")));

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"owner\":\"Owner\",\"initialBalance\":100.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }

    @Test
    void checkBalance_returnsBalance() throws Exception {
        when(service.checkBalance(1L)).thenReturn(new BigDecimal("100.00"));

        mockMvc.perform(get("/accounts/1/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(100.0));
    }

    @Test
    void withdraw_notFound_returns404() throws Exception {
        doThrow(new AccountNotFoundException(99L)).when(service).withdraw(eq(99L), any());

        mockMvc.perform(post("/accounts/99/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":10.00}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Account not found"));
    }

    @Test
    void withdraw_insufficientFunds_returns409() throws Exception {
        doThrow(new InsufficientFundsException("Insufficient funds"))
                .when(service).withdraw(eq(1L), any());

        mockMvc.perform(post("/accounts/1/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":1000.00}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Insufficient funds"));
    }
}
