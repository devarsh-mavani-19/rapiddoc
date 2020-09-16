package com.jayshreegopalapps.imagetopdf;

class RecycleListInsideFolder {
    String path;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    String type; //FILE/DIR
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    int pageNumber;

}
