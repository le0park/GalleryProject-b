package com.example.galleryproject.ui.all;

public class MyData {
    String name;
//    int imageId;
    String path;

//    public MyData(String name, int imageId) {
//        this.name = name;
//        this.imageId = imageId;
//    }

    public MyData(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public int getImageId() {
//        return imageId;
//    }
//
//    public void setImageId(int imageId) {
//        this.imageId = imageId;
//    }
}
