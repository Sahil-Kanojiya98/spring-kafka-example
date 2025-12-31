package com.example.spring_kafka_producer.service;

import com.example.spring_kafka_producer.dto.request.CreateUserRequest;
import com.example.spring_kafka_producer.entity.User;
import com.example.spring_kafka_producer.repository.UserRepository;
import org.example.event.UserCreatedEvent;
import org.example.event.UserWelcomeNotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private static final String WELCOME_MESSAGE_TEMPLATE_STRING = "Hi %s! Welcome to our platform. Your user ID is %d.";
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public UserService(UserRepository userRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional("kafkaTransactionManager")
    public void createUser(CreateUserRequest createUserRequest) {
        String username = createUserRequest.username();
        String email = createUserRequest.email();
        String password = createUserRequest.password();

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        User savedUser = userRepository.save(user);

        Integer id = savedUser.getId();
        String welcomeMessage = String.format(WELCOME_MESSAGE_TEMPLATE_STRING, username, id);

        UserCreatedEvent userCreatedEvent = new UserCreatedEvent(id, username, email);
        kafkaTemplate.send("users", String.valueOf(id), userCreatedEvent);

        UserWelcomeNotificationEvent userWelcomeNotificationEvent = new UserWelcomeNotificationEvent(id, welcomeMessage);
        kafkaTemplate.send("user-notifications", String.valueOf(id), userWelcomeNotificationEvent);
        log.info("User created event and user welcome notification event sent with user id: {}", id);
    }

    @Transactional("kafkaTransactionManager")
    public void createDuplicateUser(CreateUserRequest createUserRequest) {
        String username = createUserRequest.username();
        String email = createUserRequest.email();
        String password = createUserRequest.password();

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        User savedUser = userRepository.save(user);

        Integer id = savedUser.getId();
        String welcomeMessage = String.format(WELCOME_MESSAGE_TEMPLATE_STRING, username, id);

        UserCreatedEvent userCreatedEvent = new UserCreatedEvent(id, username, email);
        UserWelcomeNotificationEvent userWelcomeNotificationEvent = new UserWelcomeNotificationEvent(id, welcomeMessage);

        kafkaTemplate.send("users", String.valueOf(id), userCreatedEvent);
        kafkaTemplate.send("users", String.valueOf(id), userCreatedEvent);
        kafkaTemplate.send("user-notifications", String.valueOf(id), userWelcomeNotificationEvent);
        kafkaTemplate.send("user-notifications", String.valueOf(id), userWelcomeNotificationEvent);
        log.debug("User created event and user welcome notification event sent twice with user id: {}", id);
    }
}