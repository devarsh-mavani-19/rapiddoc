package com.jayshreegopalapps.imagetopdf;

import java.util.ArrayList;

class RecycleList {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getimagePath() {
        return imagePath;
    }

    public void setimagePath(String image) {
        this.imagePath = image;
    }

    String name;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    String imagePath;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    String displayName;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(String numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    String dateTime;

    String numberOfPages;
    int imageCount;

    public void setImageCount(int i) {
        this.imageCount = i;
    }

    public int getImageCount() {
        return imageCount;
    }

    public ArrayList<String> getImagesPath() {
        return imagesPath;
    }

    public void setImagesPath(ArrayList<String> imagesPath) {
        this.imagesPath = imagesPath;
    }

    ArrayList<String> imagesPath;

    String folderPath;
}
