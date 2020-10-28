package com.raspberry.practicalparent.model;

import java.util.ArrayList;
import java.util.List;

// Kid
class Kid {
    int age; // Optional addition information
    String name;
    //Result stats; // To be added: personal stats for given kid
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
}

// A manager class to keep track of all kids
// Allows addition, editing and deletion
public class KidManager {
    private static KidManager instance;
    private List<Kid> kids = new ArrayList<>();
    private int num; // Number of kids in the KidManager

    public KidManager getInstance() {
        if (instance == null) {
            instance = new KidManager();
        }
        return instance;
    }

    private KidManager() {
        // Made private for singleton support
        num = 0;
    }

    public void addKid(String name) {
        Kid kid = new Kid(name);
        kids.add(kid);
        num++;
    }

    public void deleteKid(int i) {
        if (i < 0 || i > num) {
            return;
        }
        kids.remove(i);
        num--;
    }

    public void changeName(int i, String name) {
        if (i < 0 || i > num) {
            return;
        }
        kids.get(i).setName(name);
    }

}
