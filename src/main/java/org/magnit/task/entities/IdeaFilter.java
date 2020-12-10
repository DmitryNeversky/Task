package org.magnit.task.entities;

public enum IdeaFilter {
    POPULAR("Популярное"),
    UNPOPULAR("Менее популярное"),
    FRESH("Свежее"),
    OLD("Старое");

    private final String name;

    IdeaFilter(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
