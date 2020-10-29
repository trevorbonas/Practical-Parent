package com.raspberry.practicalparent;

public class Results {
    private boolean wonFlip;
    private String sideChosen;
    private String dateFlip;
    private String childName;

    public Results(boolean won, String side, String date, String name) {
        this.wonFlip = won;
        this.sideChosen = side;
        this.dateFlip = date;
        this.childName = name;
    }

    public boolean isWonFlip() {
        return wonFlip;
    }

    public String getSideChosen() {
        return sideChosen;
    }

    public String getDateFlip() {
        return dateFlip;
    }

    public String getChildName() {
        return childName;
    }
}
