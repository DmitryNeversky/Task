package org.magnit.task.entities;

import lombok.Getter;
import lombok.Setter;

public enum IdeaStatus {

    ALL("Все статусы"),
    APPROVED("Одобрено"),
    LOOKING("На рассмотрении"),
    DENIED("Отказано");

    private @Getter @Setter String name;

    IdeaStatus(String name) {
        this.name = name;
    }

    public static IdeaStatus getValueByName(String name){
        return switch (name) {
            case "Одобрено" -> APPROVED;
            case "На рассмотрении" -> LOOKING;
            case "Отказано" -> DENIED;
            default -> null;
        };
    }
}