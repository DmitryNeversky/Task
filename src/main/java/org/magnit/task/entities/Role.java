package org.magnit.task.entities;

import lombok.Getter;
import lombok.Setter;

public enum Role {

    USER("Пользователь"),
    MODERATOR("Модератор");

    private @Getter @Setter
    String name;

    Role(String name) {
        this.name = name;
    }
}