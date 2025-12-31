package org.example.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record UserCreatedEvent(Integer userId, String username, String email) implements Serializable {

    @JsonCreator
    public UserCreatedEvent(@JsonProperty("userId") Integer userId,
                            @JsonProperty("username") String username,
                            @JsonProperty("email") String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
    }
}
