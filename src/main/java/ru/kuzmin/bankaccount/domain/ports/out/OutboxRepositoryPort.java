package ru.kuzmin.bankaccount.domain.ports.out;

import ru.kuzmin.bankaccount.domain.event.DomainEvent;
import ru.kuzmin.bankaccount.domain.event.OutboxRecord;

import java.util.List;

/**
 * Порт transactional outbox: событие сохраняется в той же транзакции,
 * что и изменение агрегата, а отдельный relay публикует его в брокер.
 */
public interface OutboxRepositoryPort {

    void save(DomainEvent event);

    List<OutboxRecord> findUnsent(int limit);

    void markSent(Long id);
}
