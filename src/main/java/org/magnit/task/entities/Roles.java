package org.magnit.task.entities;

public enum Roles {

    USER("Сотрудник"),
    MODERATOR("Эксперт");

    private final String name;

    Roles(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
