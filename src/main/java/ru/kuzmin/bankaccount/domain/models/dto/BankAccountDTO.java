package ru.kuzmin.bankaccount.domain.models.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public record BankAccountDTO(Long id, String owner, BigDecimal balance) implements Serializable {
}
