package com.raspberry.practicalparent.model;

import android.content.res.Resources;
import android.util.Log;

import com.google.gson.internal.$Gson$Preconditions;

import java.util.Random;

/**
 * A Task class that holds a task and whose turn it is to do it
 */
public class Task {
    private String name;
    private String kidName; // The name of whose kid's turn it is to do the task
    private String description;

    private int i; // Index of kidName in KidManager singleton

    public Task(String name, String description) {
        KidManager kids = KidManager.getInstance();
        Random rand = new Random();
        if (name == null) {
            return;
        }
        if (kids.getNum() <= 0) {
            i = 0;
            this.kidName = "Unassigned";
            this.name = name;
            this.description = description;
            return;
        }
        this.name = name;
        this.description = description;
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
        if (this.i == kids.getNum()) {
            this.i = 0;
        }
        this.i = (this.i + 1) % (kids.getNum());
        kidName = kids.getKidAt(this.i).getName();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIndex() {
        return i;
    }

    public void setIndex(int i) {
        this.i = i;
    }

    public void setKidName(String kidName) {
        this.kidName = kidName;
    }

    public void updateKidName() {
        KidManager kids = KidManager.getInstance();
        if (kids.getNum() <= 0) {
            this.kidName = "Unassigned";
        } else {
            this.kidName = kids.getKidAt(i).getName();
        }
    }

    public String getKidName() {
        return kidName;
    }
}
