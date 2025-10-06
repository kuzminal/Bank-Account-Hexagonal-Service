package ru.kuzmin.bankaccount.infrastructure.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kuzmin.bankaccount.domain.models.entity.PendingEvent;
import ru.kuzmin.bankaccount.domain.ports.out.PendingEventRepositoryPort;

import java.util.List;

@Repository
public interface PendingEventRepository extends
        PendingEventRepositoryPort,
        JpaRepository<PendingEvent, Long> {

    List<PendingEvent> findByStatusOrderByCreatedAtAsc(String status);

    @Modifying
    @Query("UPDATE PendingEvent p SET p.retryCount = p.retryCount + 1 WHERE p.id = :id")
    void incrementRetryCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE PendingEvent p SET p.status = :status WHERE p.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") String status);
}
