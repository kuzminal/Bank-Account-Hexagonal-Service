package ru.kuzmin.bankaccount.infrastructure.adapters.out.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import ru.kuzmin.bankaccount.domain.event.DomainEvent;
import ru.kuzmin.bankaccount.domain.event.OutboxRecord;
import ru.kuzmin.bankaccount.domain.ports.out.OutboxRepositoryPort;

import java.time.Instant;
import java.util.List;

/**
 * Адаптер outbox: сериализует доменное событие в JSON и хранит его в таблице
 * в той же транзакции, что и изменение агрегата.
 */
@Repository
public class OutboxRepositoryAdapter implements OutboxRepositoryPort {

    private final OutboxEventJpaRepository jpaRepository;
    private final ObjectMapper objectMapper;

    public OutboxRepositoryAdapter(OutboxEventJpaRepository jpaRepository, ObjectMapper objectMapper) {
        this.jpaRepository = jpaRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(DomainEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            jpaRepository.save(new OutboxEventJpaEntity(
                    null,
                    event.eventType(),
                    event.aggregateId(),
                    payload,
                    Instant.now(),
                    false,
                    null));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize domain event", e);
        }
    }

    @Override
    public List<OutboxRecord> findUnsent(int limit) {
        return jpaRepository.findBySentFalseOrderByIdAsc(PageRequest.of(0, limit)).stream()
                .map(e -> new OutboxRecord(e.getId(), e.getEventType(), e.getAggregateId(), e.getPayload()))
                .toList();
    }

    @Override
    public void markSent(Long id) {
        jpaRepository.findById(id).ifPresent(e -> {
            e.markSent();
            jpaRepository.save(e);
        });
    }
}
