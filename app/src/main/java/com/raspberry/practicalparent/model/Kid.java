package com.raspberry.practicalparent.model;

/**
 * A Kid class that holds a given kid's name and age
 * Age to be added in later just in case
 */
public class Kid {
    int age; // Optional addition information
    String name;
    //ResultManager stats = new ResultManger(); // To be added: personal stats for given kid
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

    // Returns the kid's Result
    // so it can be changed or displayed
    /*public ResultManager getResult() {
        return stats;
    }*/
}
