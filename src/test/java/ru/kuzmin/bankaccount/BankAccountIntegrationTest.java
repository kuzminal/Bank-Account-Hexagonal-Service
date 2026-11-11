package ru.kuzmin.bankaccount;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.kuzmin.bankaccount.domain.models.dto.BankAccountDTO;
import ru.kuzmin.bankaccount.domain.ports.in.BankAccountServicePort;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционный тест end-to-end: PostgreSQL + Kafka через Testcontainers.
 * Проверяет, что доменное событие реально доходит до Kafka через transactional outbox + relay.
 * Требует запущенный Docker (при его отсутствии тест автоматически пропускается).
 */
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
class BankAccountIntegrationTest {

    private static final String TOPIC = "bank-account-events";

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:17"));

    @Container
    @ServiceConnection
    static final KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1"));

    @Autowired
    private BankAccountServicePort service;

    @Test
    void deposit_publishesEventsToKafka() {
        BankAccountDTO account = service.createAccount("Owner", new BigDecimal("100.00"));
        service.deposit(account.id(), new BigDecimal("50.00"));

        try (KafkaConsumer<String, String> consumer = newConsumer()) {
            consumer.subscribe(List.of(TOPIC));

            List<String> payloads = collectMessages(consumer, account.id());

            assertThat(payloads)
                    .anyMatch(p -> p.contains("\"amount\""))
                    .anyMatch(p -> p.contains("\"initialBalance\""));
        }
    }

    private KafkaConsumer<String, String> newConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "bank-account-test");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new KafkaConsumer<>(props);
    }

    private List<String> collectMessages(KafkaConsumer<String, String> consumer, Long accountId) {
        List<String> result = new ArrayList<>();
        long deadline = System.currentTimeMillis() + 15_000;
        boolean sawDeposit = false;
        boolean sawCreated = false;

        while (System.currentTimeMillis() < deadline && !(sawDeposit && sawCreated)) {
            for (ConsumerRecord<String, String> record : consumer.poll(Duration.ofSeconds(2))) {
                if (String.valueOf(accountId).equals(record.key())) {
                    result.add(record.value());
                    sawDeposit |= record.value().contains("\"amount\"");
                    sawCreated |= record.value().contains("\"initialBalance\"");
                }
            }
        }
        return result;
    }
}
