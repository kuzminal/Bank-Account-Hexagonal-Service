package ru.kuzmin.bankaccount.application.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.kuzmin.bankaccount.domain.models.dto.BankAccountDTO;
import ru.kuzmin.bankaccount.domain.models.entity.BankAccount;

/**
 * Маппер домен → DTO. Живёт в application-слое: это часть сценария использования,
 * а не инфраструктурная деталь.
 */
@Mapper
public interface BankAccountMapper {

    BankAccountMapper INSTANCE = Mappers.getMapper(BankAccountMapper.class);

    BankAccountDTO toBankAccountDto(BankAccount bankAccount);
}
