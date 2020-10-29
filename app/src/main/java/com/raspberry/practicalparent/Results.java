package com.raspberry.practicalparent;

public class Results {
    private boolean wonFlip;
    private String sideChosen;
    private String dateFlip;

    public Results(boolean won, String side, String date) {
        this.wonFlip = won;
        this.sideChosen = side;
        this.dateFlip = date;
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
}
