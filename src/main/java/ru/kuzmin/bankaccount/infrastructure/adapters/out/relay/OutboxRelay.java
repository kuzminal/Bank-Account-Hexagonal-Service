package ru.kuzmin.bankaccount.infrastructure.adapters.out.relay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.kuzmin.bankaccount.domain.event.OutboxRecord;
import ru.kuzmin.bankaccount.domain.ports.out.OutboxRepositoryPort;

import java.util.concurrent.TimeUnit;

/**
 * Relay transactional outbox: периодически забирает неотправленные события
 * из outbox-таблицы и публикует их в Kafka с ключом по aggregateId.
 * Гарантия at-least-once: отметка sent ставится только после успешной отправки.
 */
@Component
public class OutboxRelay {

    private static final Logger log = LoggerFactory.getLogger(OutboxRelay.class);

    private final OutboxRepositoryPort outbox;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;
    private final int batchSize;

    public OutboxRelay(OutboxRepositoryPort outbox,
                       KafkaTemplate<String, String> kafkaTemplate,
                       @Value("${app.kafka.topic}") String topic,
                       @Value("${app.outbox.batch-size:100}") int batchSize) {
        this.outbox = outbox;
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
        this.batchSize = batchSize;
    }

    @Scheduled(fixedDelayString = "${app.outbox.relay-delay-ms:1000}")
    public void relay() {
        for (OutboxRecord record : outbox.findUnsent(batchSize)) {
            try {
                kafkaTemplate.send(topic, record.aggregateId(), record.payload())
                        .get(5, TimeUnit.SECONDS);
                outbox.markSent(record.id());
            } catch (Exception e) {
                log.error("Failed to publish outbox record id={}, eventType={}",
                        record.id(), record.eventType(), e);
            }
        }
    }
}
