package com.raspberry.practicalparent.model;

import javax.xml.transform.Result;

/**
 * A Kid class that holds a given kid's name and age
 * Age to be added in later just in case
 */
public class Kid {

    private int age; // Optional addition information
    private String name;
    private ResultsManager stats = new ResultsManager(); //personal stats for given kid

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
    public ResultsManager getResults() {
        return stats;
    }
}
