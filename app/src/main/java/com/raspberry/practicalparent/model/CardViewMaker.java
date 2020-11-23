package com.raspberry.practicalparent.model;

/**
 * Cards for populating history of flip results RecyclerView
 */

public class CardViewMaker {
    private int image;
    private String childName;
    private String wonFlip;
    private String date;
    private String sideChosen;
    private String portraitPath;

    public CardViewMaker(int imageFile, String name, String win, String dateFlip, String side, String path) {
        image = imageFile;
        childName = name;
        wonFlip = win;
        date = dateFlip;
        sideChosen = side;
        portraitPath = path;
    }

    public int getImage() {
        return image;
    }

    public String getChildName() {
        return childName;
    }

    public String getWonFlip() {
        return wonFlip;
    }

    public String getDate() {
        return date;
    }

    public String getSideChosen() {
        return sideChosen;
    }

    public String getPortraitPath() {
        return portraitPath;
    }
}
