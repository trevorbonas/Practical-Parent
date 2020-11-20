package com.raspberry.practicalparent.model;

import com.google.gson.internal.$Gson$Preconditions;

import java.util.Random;

/**
 * A Task class that holds a task and whose turn it is to do it
 */
public class Task {
    private String name;
    private String kidName; // The name of whose kid's turn it is to do the task
    private int i; // Index of kidName in KidManager singleton

    public Task(String name) {
        KidManager kids = KidManager.getInstance();
        Random rand = new Random();
        if (name == null) {
            return;
        }
        if (kids.getNum() <= 0) {
            i = 0;
            this.kidName = "Unassigned";
            this.name = name;
            return;
        }
        this.name = name;
        i = rand.nextInt(kids.getNum());
        this.kidName = kids.getKidAt(i).getName();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setKid(int i) {
        KidManager kids = KidManager.getInstance();
        if (i < 0 || i >= kids.getNum()) {
            return;
        }
        this.i = i;
        kidName = kids.getKidAt(this.i).getName();
    }

    public String getName() {
        return name;
    }

    public void next() {
        KidManager kids = KidManager.getInstance();
        if (kids.getNum() <= 0) {
            return;
        }
        this.i = (this.i + 1) % (kids.getNum());
        kidName = kids.getKidAt(this.i).getName();
    }

    public String getKidName() {
        return kidName;
    }
}
