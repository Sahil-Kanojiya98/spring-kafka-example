package org.example.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record UserWelcomeNotificationEvent(Integer userId, String message) implements Serializable {

    @JsonCreator
    public UserWelcomeNotificationEvent(@JsonProperty("userId") Integer userId,
                                        @JsonProperty("message") String message) {
        this.userId = userId;
        this.message = message;
    }
}
