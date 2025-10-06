package ru.kuzmin.bankaccount.domain.event;

import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Маркерный интерфейс для всех доменных событий.
 * Определяет контракт для событий, происходящих в доменной модели.
 */
public abstract class DomainEvent implements Serializable {
    @Value("${orders.eventVersion:1.0}")
    private String eventVersion;

    /**
     * Возвращает время возникновения события
     *
     * @return LocalDateTime - момент времени, когда событие произошло
     */
    LocalDateTime occurredOn() {
        return LocalDateTime.now();
    }

    /**
     * Возвращает тип события (опционально, для удобства)
     * @return String - тип события
     */
    String eventType() {
        return this.getClass().getSimpleName();
    }

    /**
     * Возвращает версию события (для управления схемами)
     * @return String - версия события
     */
    String eventVersion() {
        return eventVersion;
    }

    public UUID getAggregateId() {
        return UUID.randomUUID(); // TODO: 06.05.2023 - заменить на v7
    }
}