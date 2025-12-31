package com.example.spring_kafka_consumer.listener;

import com.example.spring_kafka_consumer.entity.ProcessedEvent;
import com.example.spring_kafka_consumer.entity.constant.EventType;
import com.example.spring_kafka_consumer.repository.ProcessedEventRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.event.UserWelcomeNotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class UserWelcomeNotificationEventListener {

    private static final Logger log = LoggerFactory.getLogger(UserWelcomeNotificationEventListener.class);

    private final ProcessedEventRepository processedEventRepository;

    public UserWelcomeNotificationEventListener(ProcessedEventRepository processedEventRepository) {
        this.processedEventRepository = processedEventRepository;
    }

    @KafkaListener(topics = "user-notifications", groupId = "user-service-group")
    public void handleUserWelcomeNotification(ConsumerRecord<String, UserWelcomeNotificationEvent> userWelcomeNotificationEventConsumerRecord, Acknowledgment ack) {
        String key = userWelcomeNotificationEventConsumerRecord.key();
        UserWelcomeNotificationEvent userWelcomeNotificationEvent = userWelcomeNotificationEventConsumerRecord.value();
        log.info("Received UserWelcomeNotificationEvent: {}", userWelcomeNotificationEvent);

        boolean alreadyProcessed = processedEventRepository
                .findByEventIdAndEventType(key, EventType.USER_WELCOME_NOTIFICATION)
                .isPresent();

        if (!alreadyProcessed) {
            log.info("Processing: {}", userWelcomeNotificationEvent);
            processedEventRepository.save(new ProcessedEvent(key, EventType.USER_WELCOME_NOTIFICATION));
        }

        ack.acknowledge();
    }
}
