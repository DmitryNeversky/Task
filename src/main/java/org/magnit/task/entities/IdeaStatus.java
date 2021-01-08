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
        switch (name) {
            case "Одобрено": return IdeaStatus.APPROVED;
            case "На рассмотрении": return IdeaStatus.LOOKING;
            case "Отказано": return IdeaStatus.DENIED;
            default: return null;
        }
    }
}