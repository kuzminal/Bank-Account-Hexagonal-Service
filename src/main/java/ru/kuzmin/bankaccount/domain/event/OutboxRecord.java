package ru.kuzmin.bankaccount.domain.event;

/**
 * Запись outbox, которую relay публикует в Kafka.
 * {@code payload} — доменное событие, сериализованное в JSON (формат хранения outbox).
 */
public record OutboxRecord(Long id, String eventType, String aggregateId, String payload) {
}
