package ru.kuzmin.bankaccount.infrastructure.adapters.out.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.TimeoutException;
import org.hibernate.sql.exec.ExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.kuzmin.bankaccount.application.service.FallbackStorageService;
import ru.kuzmin.bankaccount.domain.event.DomainEvent;
import ru.kuzmin.bankaccount.domain.ports.out.BankAccountNotificationPort;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class KafkaBankAccountNotifier implements BankAccountNotificationPort {
    @Value("${spring.kafka.topic}")
    String topic;
    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;
    private final FallbackStorageService fallbackService;
    private final CircuitBreaker circuitBreaker;

    // Метрики для мониторинга
    private final MeterRegistry meterRegistry;
    private final Counter successCounter;
    private final Counter fallbackCounter;
    private final Counter circuitOpenCounter;

    public KafkaBankAccountNotifier(
            KafkaTemplate<String, DomainEvent> kafkaTemplate,
            FallbackStorageService fallbackService,
            CircuitBreakerRegistry circuitBreakerRegistry,
            CircuitBreaker circuitBreaker,
            MeterRegistry meterRegistry) {
        this.kafkaTemplate = kafkaTemplate;
        this.fallbackService = fallbackService;
        this.circuitBreaker = circuitBreaker;
        this.meterRegistry = meterRegistry;

        // Инициализация метрик
        this.successCounter = Counter.builder("kafka.producer.success")
                .description("Successful Kafka messages")
                .register(meterRegistry);
        this.fallbackCounter = Counter.builder("kafka.producer.fallback")
                .description("Messages saved to fallback")
                .register(meterRegistry);
        this.circuitOpenCounter = Counter.builder("kafka.producer.circuit_open")
                .description("Circuit breaker open events")
                .register(meterRegistry);

        // Настройка обработчиков событий Circuit Breaker
        setupCircuitBreakerEventHandlers();
    }

    @Override
    public void notify(DomainEvent event) {
//        String event = String.format("Account %d: %s. Balance: %s",
//                account.getId(), message, account.getBalance());
        sendMessageAsync(topic, event.getAggregateId().toString(), event);
        //kafkaTemplate.send(topic, event);
    }

    private void setupCircuitBreakerEventHandlers() {
        circuitBreaker.getEventPublisher()
                .onSuccess(event -> log.debug("Circuit Breaker call successful"))
                .onError(event -> log.warn("Circuit Breaker call failed: {}", event.getThrowable().getMessage()))
                .onStateTransition(event -> log.info("Circuit Breaker state changed from {} to {}",
                        event.getStateTransition().getFromState(),
                        event.getStateTransition().getToState()))
                .onCallNotPermitted(event -> {
                    log.warn("Circuit Breaker OPEN - call not permitted");
                    circuitOpenCounter.increment();
                });
    }

    //@Async
    public CompletableFuture<SendResult<String, DomainEvent>> sendMessageAsync(String topic, String key, DomainEvent message) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Используем Circuit Breaker для синхронной операции
                return circuitBreaker.executeSupplier(() -> {
                    log.debug("Sending message to Kafka. Topic: {}, Key: {}", topic, key);

                    // Создаем CompletableFuture для результата отправки
                    CompletableFuture<SendResult<String, DomainEvent>> future = new CompletableFuture<>();
                    ObjectMapper objectMapper = new ObjectMapper();

                    // Отправляем сообщение
                    kafkaTemplate.send(topic, key, message)
                            .whenComplete((result, throwable) -> {
                                if (throwable != null) {
                                    future.completeExceptionally(throwable);
                                } else {
                                    future.complete(result);
                                }
                            });

                    // Ждем результат с таймаутом
                    try {
                        return future.get(10, TimeUnit.SECONDS);
                    } catch (TimeoutException e) {
                        throw new KafkaException("Timeout while sending message to Kafka", e);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new KafkaException("Interrupted while sending message", e);
                    } catch (ExecutionException | java.util.concurrent.ExecutionException | java.util.concurrent.TimeoutException e) {
                        throw new KafkaException("Failed to send message to Kafka", e.getCause());
                    }
                });

            } catch (CallNotPermittedException e) {
                // Circuit Breaker OPEN - сохраняем в fallback
                handleCircuitBreakerOpen(topic, key, message, e);
                throw e;
            } catch (Exception e) {
                // Другие ошибки - также сохраняем в fallback
                log.error("Error sending message to Kafka. Key: {}", key, e);
                fallbackService.saveMessage(topic, key, message);
                fallbackCounter.increment();
                throw new RuntimeException("Failed to send message", e);
            }
        }).whenComplete((result, throwable) -> {
            if (throwable == null) {
                successCounter.increment();
                log.info("Message sent successfully. Topic: {}, Partition: {}, Offset: {}",
                        topic, (Object) result.getRecordMetadata().partition(), (Object) result.getRecordMetadata().offset());
            }
        });
    }

//    // Упрощенный синхронный метод
//    public void sendMessage(String topic, String key, DomainEvent message) {
//        try {
//            circuitBreaker.executeRunnable(() -> {
//                try {
//                    // Используем get() для синхронной отправки с таймаутом
//                    SendResult<String, DomainEvent> result = kafkaTemplate.send(topic, key, message)
//                            .get(10, TimeUnit.SECONDS);
//
//                    successCounter.increment();
//                    log.info("Message sent successfully. Topic: {}, Offset: {}",
//                            topic, (Object) result.getRecordMetadata().offset());
//
//                } catch (TimeoutException e) {
//                    throw new KafkaException("Timeout while sending message", e);
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                    throw new KafkaException("Interrupted while sending message", e);
//                } catch (ExecutionException | java.util.concurrent.ExecutionException |
//                         java.util.concurrent.TimeoutException e) {
//                    throw new KafkaException("Failed to send message", e.getCause());
//                }
//            });
//
//        } catch (CallNotPermittedException e) {
//            handleCircuitBreakerOpen(topic, key, message, e);
//        } catch (Exception e) {
//            log.error("Error sending message to Kafka. Key: {}", key, e);
//            fallbackService.saveMessage(topic, key, message);
//            fallbackCounter.increment();
//        }
//    }

    private void handleCircuitBreakerOpen(String topic, String key, DomainEvent message, CallNotPermittedException e) {
        log.warn("Circuit Breaker OPEN for Kafka producer. Saving message to fallback storage. Key: {}", key);
        fallbackService.saveMessage(topic, key, message);
        fallbackCounter.increment();
        circuitOpenCounter.increment();
    }

    // Метод для получения состояния Circuit Breaker
    public CircuitBreaker.State getCircuitBreakerState() {
        return circuitBreaker.getState();
    }

    public CircuitBreaker.Metrics getCircuitBreakerMetrics() {
        return circuitBreaker.getMetrics();
    }
}