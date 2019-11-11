package com.example.galleryproject.ui.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MarkerItem implements ClusterItem {

    private LatLng location;

    private double latitude;
    private double longitude;
    private int photoNum;
    private String filePath;

    public MarkerItem(){
        // Seoul lat, long
        latitude = 37.56;
        longitude = 126.97;
        this.location = new LatLng(latitude, longitude);
        photoNum = 0;
        filePath = null;
    }

    public MarkerItem(LatLng location, String filePath){
        this.location = location;
        this.filePath = filePath;
    }

    public MarkerItem(double latitude, double longitude, int photoNum, String filePath){
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = new LatLng(latitude, longitude);
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

    @Override
    public LatLng getPosition() {
        return this.location;
    }

    @Override
    public String getTitle() {
        return filePath.substring(0, 5);
    }

    @Override
    public String getSnippet() {
        return photoNum + "개";
    }
}
