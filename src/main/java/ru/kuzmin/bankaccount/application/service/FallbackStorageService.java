package ru.kuzmin.bankaccount.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kuzmin.bankaccount.domain.event.DomainEvent;
import ru.kuzmin.bankaccount.domain.models.entity.PendingEvent;
import ru.kuzmin.bankaccount.infrastructure.adapters.out.persistence.PendingEventRepository;

import java.util.List;

@Service
@Slf4j
public class FallbackStorageService {
    private final PendingEventRepository repository;

    public FallbackStorageService(PendingEventRepository repository) {
        this.repository = repository;
    }

    public void saveMessage(String topic, String key, DomainEvent message) {
        PendingEvent pendingMessage = new PendingEvent(topic, key, message);
        try {
            repository.save(pendingMessage);
        } catch (Exception e) {
            if (e.getMessage().contains("Duplicate entry")) {
                log.warn("Duplicate entry for message. Topic: {}, Key: {}, ID: {}",
                        topic, key, pendingMessage.getId());
            } else {
                log.error("Error saving message to fallback storage. Topic: {}, Key: {}, ID: {}", e);
            }
        }
        log.info("Message saved to fallback storage. Topic: {}, Key: {}, ID: {}",
                topic, key, pendingMessage.getId());
    }

    public List<PendingEvent> getPendingMessages() {
        return repository.findByStatusOrderByCreatedAtAsc("PENDING");
    }

    public void markAsProcessing(Long id) {
        repository.updateStatus(id, "PROCESSING");
    }

    public void markAsSent(Long id) {
        repository.updateStatus(id, "SENT");
    }

    public void incrementRetryCount(Long id) {
        repository.incrementRetryCount(id);
    }

    public void deleteMessage(Long id) {
        repository.deleteById(id);
    }
}
