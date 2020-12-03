package org.magnit.task.entities;

import lombok.Getter;
import lombok.Setter;

public enum IdeaStatus {

    APPROVED("Одобрено"),
    LOOKING("На рассмотрении"),
    DENIED("Отказано");

    private @Getter @Setter String name;

    IdeaStatus(String name) {
        this.name = name;
    }
}