package com.example.galleryproject.ui.map;

public class MarkerItem {
    double latitude;
    double longitude;
    int photoNum;
    String filePath;

    public MarkerItem(){
        // Seoul lat, long
        latitude = 37.56;
        longitude = 126.97;
        photoNum = 0;
        filePath = null;
    }
    public MarkerItem(double latitude, double longitude, int photoNum, String filePath){
        this.latitude = latitude;
        this.longitude = longitude;
        this.photoNum = photoNum;
        this.filePath = filePath;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getPhotoNum() {
        return photoNum;
    }

    public void setPhotoNum(int photoNum) {
        this.photoNum = photoNum;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
