package ru.kuzmin.bankaccount.infrastructure.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.kuzmin.bankaccount.domain.models.entity.BankAccount;
import ru.kuzmin.bankaccount.domain.models.dto.BankAccountDTO;

@Mapper
public interface BankAccountMapper {
    BankAccountMapper INSTANCE = Mappers.getMapper(BankAccountMapper.class);

    BankAccountDTO toBankAccountDto(BankAccount bankAccount);

    BankAccount toBankAccount(BankAccountDTO bankAccountDto);
}
