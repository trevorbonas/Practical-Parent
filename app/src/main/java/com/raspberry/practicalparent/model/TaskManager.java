package com.raspberry.practicalparent.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A manager class to keep track of all Tasks
 * Allows addition, editing and deletion
 * Is a singleton
 */
public class TaskManager {
    private static TaskManager instance;
    private List<Task> tasks = new ArrayList<>();

    static public TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    private TaskManager() {
        // Made private for singleton support
    }

    public void addTask(String name, String description) {
        Task task = new Task(name, description);
        tasks.add(task);
    }

    public void deleteTask(int i) {
        if (i < 0 || i >= tasks.size()) {
            return;
        }
        tasks.remove(i);
    }

    public void changeName(String name, int i) {
        if (i < 0 || i >= tasks.size()) {
            return;
        }
        if (name == null) {
            return;
        }
        tasks.get(i).setName(name);
    }

    public int getNum() {
        return tasks.size();
    }

    public Task getTaskAt(int i) {
        if (i < 0 || i >= tasks.size()) {
            return null;
        }
        return tasks.get(i);
    }

    public List<Task> getList() {
        return tasks;
    }

    public void setList(List<Task> list) {
        tasks = list;
    }
}
