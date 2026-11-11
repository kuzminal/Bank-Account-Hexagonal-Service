package ru.kuzmin.bankaccount.infrastructure.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data репозиторий — деталь инфраструктуры. Домен о нём не знает:
 * наружу торчит только порт {@link ru.kuzmin.bankaccount.domain.ports.out.BankAccountRepositoryPort}.
 */
public interface BankAccountJpaRepository extends JpaRepository<BankAccountJpaEntity, Long> {
}
