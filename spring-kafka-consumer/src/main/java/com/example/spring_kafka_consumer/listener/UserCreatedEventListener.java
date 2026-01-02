package com.example.spring_kafka_consumer.listener;

import com.example.spring_kafka_consumer.entity.ProcessedEvent;
import com.example.spring_kafka_consumer.entity.constant.EventType;
import com.example.spring_kafka_consumer.repository.ProcessedEventRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.event.UserCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserCreatedEventListener {

    private static final Logger log = LoggerFactory.getLogger(UserCreatedEventListener.class);

    private final ProcessedEventRepository processedEventRepository;

    public UserCreatedEventListener(ProcessedEventRepository processedEventRepository) {
        this.processedEventRepository = processedEventRepository;
    }

    @Transactional
    @KafkaListener(topics = "users", groupId = "user-service-group")
    public void handleUserCreated(ConsumerRecord<String, UserCreatedEvent> userCreatedEventConsumerRecord, Acknowledgment ack) {
        String key = userCreatedEventConsumerRecord.key();
        UserCreatedEvent userCreatedEvent = userCreatedEventConsumerRecord.value();
        log.info("Received UserCreatedEvent: {}", userCreatedEvent);

        boolean alreadyProcessed = processedEventRepository
                .findByEventIdAndEventType(key, EventType.USER_CREATED)
                .isPresent();

        if (!alreadyProcessed) {
            log.info("Processing: {}", userCreatedEvent);
            processedEventRepository.save(new ProcessedEvent(key, EventType.USER_CREATED));
        }

        ack.acknowledge();
    }
}
