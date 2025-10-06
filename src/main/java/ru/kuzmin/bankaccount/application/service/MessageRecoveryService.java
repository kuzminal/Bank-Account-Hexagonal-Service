package ru.kuzmin.bankaccount.application.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.kuzmin.bankaccount.domain.models.entity.PendingEvent;
import ru.kuzmin.bankaccount.infrastructure.adapters.out.kafka.KafkaBankAccountNotifier;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MessageRecoveryService {

    private final KafkaBankAccountNotifier kafkaProducer;
    private final FallbackStorageService fallbackService;
    private final CircuitBreaker circuitBreaker;
    private final Counter recoverySuccessCounter;
    private final Counter recoveryErrorCounter;

    public MessageRecoveryService(KafkaBankAccountNotifier kafkaProducer,
                                  FallbackStorageService fallbackService,
                                  CircuitBreakerRegistry circuitBreakerRegistry,
                                  MeterRegistry meterRegistry) {
        this.kafkaProducer = kafkaProducer;
        this.fallbackService = fallbackService;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("kafka-producer");

        this.recoverySuccessCounter = Counter.builder("message.recovery.success")
                .description("Successfully recovered messages")
                .register(meterRegistry);
        this.recoveryErrorCounter = Counter.builder("message.recovery.error")
                .description("Failed message recovery attempts")
                .register(meterRegistry);
    }

    @Scheduled(fixedDelay = 30000)
    public void recoverMessages() {
        if (circuitBreaker.getState() != CircuitBreaker.State.CLOSED) {
            log.debug("Circuit Breaker state is {}, skipping recovery", circuitBreaker.getState());
            return;
        }

        List<PendingEvent> pendingMessages = fallbackService.getPendingMessages();
        if (pendingMessages.isEmpty()) {
            return;
        }

        log.info("Starting recovery of {} pending messages", Optional.of(pendingMessages.size()));

        for (PendingEvent message : pendingMessages) {
            if (message.getRetryCount() >= 5) {
                log.warn("Message {} exceeded max retry count, archiving", message.getId());
                fallbackService.markAsSent(message.getId());
                continue;
            }

            try {
                fallbackService.markAsProcessing(message.getId());

                // Синхронная отправка при восстановлении
                kafkaProducer.sendMessageAsync(message.getTopic(), message.getKey(), message.getMessage());

                fallbackService.markAsSent(message.getId());
                recoverySuccessCounter.increment();
                log.info("Successfully recovered message ID: {}", message.getId());

            } catch (Exception e) {
                fallbackService.incrementRetryCount(message.getId());
                recoveryErrorCounter.increment();
                log.error("Failed to recover message ID: {}. Retry count: {}",
                        message.getId(), (Object) (message.getRetryCount() + 1), e);

                // Прерываем восстановление при ошибке
                break;
            }
        }
    }
}

