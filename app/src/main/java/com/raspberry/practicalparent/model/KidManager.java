package com.raspberry.practicalparent.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A manager class to keep track of all kids
 * Allows addition, editing and deletion
 * Is a singleton
  */
public class KidManager {
    private static KidManager instance;
    private List<Kid> kids = new ArrayList<>();
    private Kid currentKid; // The current kid whose qualities will be changed
    private int currentIndex; // The index of the current kid in the ArrayList

    static public KidManager getInstance() {
        if (instance == null) {
            instance = new KidManager();
        }
        return instance;
    }

    private KidManager() {
        // Made private for singleton support
    }

    public void addKid(String name) {
        Kid kid = new Kid(name);
        kids.add(kid);
        // If we had an empty list make this first added kid the
        // current kid (whose turn it is)
        if (kids.size() == 1) {
            changeKid(0);
        }
    }

    public void deleteKid(int i) {
        if (i < 0 || i >= kids.size()) {
            return;
        }
        kids.remove(i);
        // If the kid we just deleted was our current kid (whose turn it is)
        // go to the next kid
        if (i == currentIndex && kids.size() > 1) {
            kids.remove(i);
            nextKid();
        }
        // If the kid we just deleted was the only kid
        // make current values uninitialized
        else if (i == currentIndex && kids.size() <= 1) {
            currentIndex = -1; // No current index
            currentKid = null; // No current kid selected
            kids.remove(i);
        }
        else {
            kids.remove(i);
        }
    }

    public void changeName(String name) {
        if (currentKid == null) {
            return;
        }
        kids.get(currentIndex).setName(name);
    }

    public void changeKid(int i) {
        if (i < 0 || i >= kids.size()) {
            return;
        }
        currentIndex = i;
        currentKid = kids.get(currentIndex);
    }

    // Makes the current kid whose qualities are being changed
    // the next kid in the list
    // Loops around
    public void nextKid() {
        currentIndex = (currentIndex + 1) % kids.size();
        currentKid = kids.get(currentIndex);
    }

    // Gets the current kid's Result
    // so it can be displayed or edited
    /*public Result getStats() {
        return currentKid.getResult();
    }*/

    public int getNum() {
        return kids.size();
    }

    public String getName() {
        return currentKid.getName();
    }

    public Kid getKidAt(int i) {
        if (i < 0 || i >= kids.size()) {
            return null;
        }
        return kids.get(i);
    }

    public List<Kid> getList() {
        return kids;
    }

    public void setList(List<Kid> list) {
        kids = list;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }
}
