package ru.kuzmin.bankaccount.domain.models.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.kuzmin.bankaccount.domain.event.DomainEvent;

import java.time.LocalDateTime;

@Entity
@Table(name = "pending_events")
@Getter
public class PendingEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;
    private String key;
    @JdbcTypeCode(SqlTypes.JSON)
    private DomainEvent message;
    private LocalDateTime createdAt;
    private Integer retryCount = (Integer) 0;
    private String status; // PENDING, PROCESSING, SENT

    // Конструкторы, геттеры, сеттеры
    public PendingEvent() {}

    public PendingEvent(String topic, String key, DomainEvent message) {
        this.topic = topic;
        this.key = key;
        this.message = message;
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING";
    }
}
