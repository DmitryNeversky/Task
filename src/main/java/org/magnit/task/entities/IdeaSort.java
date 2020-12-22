package org.magnit.task.entities;

public enum IdeaSort {
    POPULAR("Популярное"),
    NEW("Новое"),
    OLD("Старое");

    private final String name;

    IdeaSort(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
