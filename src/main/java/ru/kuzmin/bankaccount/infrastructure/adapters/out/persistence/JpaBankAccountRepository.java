package ru.kuzmin.bankaccount.infrastructure.adapters.out.persistence;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kuzmin.bankaccount.domain.models.entity.BankAccount;
import ru.kuzmin.bankaccount.domain.ports.out.BankAccountRepositoryPort;

@Primary
@Repository
public interface JpaBankAccountRepository extends
        BankAccountRepositoryPort,
        JpaRepository<BankAccount, Long> {

//    @Override
//    default BankAccount save(BankAccount account) {
//        return saveAndFlush(account);
//    }
//
//    @Override
//    default Optional<BankAccount> findById(Long id) {
//        return findById(id);
//    }
}
