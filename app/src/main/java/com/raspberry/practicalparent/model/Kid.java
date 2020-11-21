package com.raspberry.practicalparent.model;

import android.graphics.Bitmap;

/**
 * A Kid class that holds a given kid's name and age
 */
public class Kid {

    private int age; // Optional feature
    private String name;
    private String filename; // Filename of the kid's portrait

    public Kid(String name) {
        this.name = name;
        filename = "None"; // Default, no profile pic
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename(){
        return filename;
    }

    /*public Bitmap getPortrait() {
        // Get the portrait
    }*/

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
