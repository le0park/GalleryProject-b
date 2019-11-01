package com.example.galleryproject;

import android.media.ExifInterface;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SimplePhoto implements Photo, Serializable {
    private String filePath;
    private String date;
    private String latitude;
    private String longitude;

    public SimplePhoto(String filePath) {
        setFilePath(filePath);
        try {
            ExifInterface exif = new ExifInterface(filePath);
            setDate(exif.getAttribute(ExifInterface.TAG_DATETIME));
//            Log.e("InSimplePhoto : ", date);
            setLatitude(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE));
            setLongitude(exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MakePhotoError", e.toString());
        }
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        if (date != null) {
            this.date = date;
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");

            File file = new File(filePath);
            Date lastModDate = new Date(file.lastModified());
            this.date = simpleDateFormat.format(lastModDate);
        }
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

}
