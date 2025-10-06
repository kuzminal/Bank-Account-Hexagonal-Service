package ru.kuzmin.bankaccount.domain.ports.out;

import ru.kuzmin.bankaccount.domain.models.entity.PendingEvent;
import java.util.List;

public interface PendingEventRepositoryPort {
    List<PendingEvent> findByStatusOrderByCreatedAtAsc(String status);
    void incrementRetryCount(Long id);
    void updateStatus(Long id, String status);
}
