package com.example.spring_kafka_producer.dto.request;

public record CreateUserRequest(String email, String username, String password) {
}
