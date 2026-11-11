package ru.kuzmin.bankaccount.infrastructure.adapters.out.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventJpaEntity, Long> {

    List<OutboxEventJpaEntity> findBySentFalseOrderByIdAsc(Pageable pageable);
}
