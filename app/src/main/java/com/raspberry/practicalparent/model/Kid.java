package com.raspberry.practicalparent.model;

import android.net.Uri;

/**
 * A Kid class that holds a given kid's name and age
 */
public class Kid {

    private int age; // Optional feature
    private String name;
    private String picPath; // Path to kid's portrait

    public Kid(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPicPath(String path) {
        this.picPath = path;
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

    // Retrieves the kid's portrait from sd card/secondary memory
    // Useful for setting images in the UI quickly to kid's portrait
    public String getPicPath() {
        return picPath;
    }
}