package com.example.galleryproject;

import java.text.SimpleDateFormat;

public interface Photo {
    public String getFilePath();
    public String getDate();
    public String getLatitude();
    public String getLongitude();

    public void setFilePath(String filePath);
    public void setDate(String date);
    public void setLatitude(String latitude);
    public void setLongitude(String longitude);
}
