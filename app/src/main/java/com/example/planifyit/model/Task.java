package com.example.planifyit.model;

import java.time.LocalDateTime;

public class Task {
    private String title;
    private String description;
    private LocalDateTime date;

    public Task(String title, String description, LocalDateTime date) {
        this.title = title;
        this.description = description.equals("") ? ".............." : description;
        this.date = date;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String
    toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                '}';
    }
}
