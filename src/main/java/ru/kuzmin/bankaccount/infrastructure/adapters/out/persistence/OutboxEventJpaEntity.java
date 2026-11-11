package ru.kuzmin.bankaccount.infrastructure.adapters.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * JPA-сущность таблицы outbox. Событие хранится в виде JSON-строки (payload),
 * метаданные (eventType, aggregateId) — отдельными колонками для выборки и роутинга.
 */
@Entity
@Table(name = "outbox_event")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEventJpaEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private String aggregateId;

    @Column(nullable = false, columnDefinition = "text")
    private String payload;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private boolean sent;

    private Instant sentAt;

    public void markSent() {
        this.sent = true;
        this.sentAt = Instant.now();
    }
}
