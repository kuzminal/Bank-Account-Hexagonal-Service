package ru.kuzmin.bankaccount.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Базовый класс доменного события.
 * Событие неизменяемо: идентификатор и время фиксируются в момент создания.
 * Не зависит от Spring и инфраструктуры.
 */
public abstract class DomainEvent {

    private final UUID eventId;
    private final Instant occurredOn;

    protected DomainEvent() {
        this.eventId = UUID.randomUUID();
        this.occurredOn = Instant.now();
    }

    public UUID getEventId() {
        return eventId;
    }

    public Instant getOccurredOn() {
        return occurredOn;
    }

    /**
     * Тип события — используется потребителями для маршрутизации/десериализации.
     */
    public String eventType() {
        return getClass().getSimpleName();
    }

    /**
     * Версия схемы события — для управления эволюцией контракта.
     */
    public String eventVersion() {
        return "1.0";
    }

    /**
     * Идентификатор агрегата — используется как ключ партиции Kafka,
     * чтобы события одного счёта попадали в одну партицию (сохранение порядка).
     */
    public abstract String aggregateId();
}
