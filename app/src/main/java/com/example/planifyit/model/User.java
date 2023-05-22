package com.example.planifyit.model;

import java.util.ArrayList;

public class User {
    private static ArrayList<Task> tasks = new ArrayList<>();

    public static ArrayList<Task> getTasks() {
        return tasks;
    }

}
