package com.raspberry.practicalparent.model;

/**
 * A Kid class that holds a given kid's name and age
 * Age to be added in later just in case
 */
public class Kid {

    private int age; // Optional addition information
    private String name;

    public Kid(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        if (age < 0 || age > 18) {
            return;
        }
        this.age = age;
    }

    public String getName() {
        return name;
    }
}
