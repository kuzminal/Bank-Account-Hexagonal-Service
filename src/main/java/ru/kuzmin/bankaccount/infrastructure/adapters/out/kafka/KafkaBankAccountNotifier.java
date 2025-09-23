package ru.kuzmin.bankaccount.infrastructure.adapters.out.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.kuzmin.bankaccount.domain.event.DomainEvent;
import ru.kuzmin.bankaccount.domain.models.entity.BankAccount;
import ru.kuzmin.bankaccount.domain.ports.out.BankAccountNotificationPort;

@Component
public class KafkaBankAccountNotifier implements BankAccountNotificationPort {
    @Value("${spring.kafka.topic}")
    String topic;
    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    public KafkaBankAccountNotifier(KafkaTemplate<String, DomainEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void notify(DomainEvent event) {
//        String event = String.format("Account %d: %s. Balance: %s",
//                account.getId(), message, account.getBalance());
        kafkaTemplate.send(topic, event);
    }
}