package ru.kuzmin.bankaccount.infrastructure.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.kuzmin.bankaccount.domain.models.dto.BankAccountDTO;
import ru.kuzmin.bankaccount.domain.models.entity.BankAccount;

@Mapper
public interface BankAccountMapper {

    BankAccountMapper INSTANCE = Mappers.getMapper(BankAccountMapper.class);

    BankAccountDTO toBankAccountDto(BankAccount bankAccount);
}
