package ru.kuzmin.bankaccount.infrastructure.adapters.out.persistence;

import org.springframework.stereotype.Repository;
import ru.kuzmin.bankaccount.domain.models.entity.BankAccount;
import ru.kuzmin.bankaccount.domain.ports.out.BankAccountRepositoryPort;

import java.util.Optional;

/**
 * Адаптер персистентности: реализует порт {@link BankAccountRepositoryPort},
 * делегирует в Spring Data и маппит JPA-сущность ↔ доменный агрегат.
 */
@Repository
public class JpaBankAccountRepositoryAdapter implements BankAccountRepositoryPort {

    private final BankAccountJpaRepository jpaRepository;

    public JpaBankAccountRepositoryAdapter(BankAccountJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public BankAccount save(BankAccount account) {
        return jpaRepository.save(BankAccountJpaEntity.fromDomain(account)).toDomain();
    }

    @Override
    public Optional<BankAccount> findById(Long id) {
        return jpaRepository.findById(id).map(BankAccountJpaEntity::toDomain);
    }
}
