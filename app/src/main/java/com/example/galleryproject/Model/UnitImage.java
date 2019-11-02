package com.example.galleryproject.Model;

import android.os.Parcel;
import android.util.Log;
import androidx.exifinterface.media.ExifInterface;

import com.example.galleryproject.Model.Image;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import kotlin.Unit;

public class UnitImage extends Image {

//    private File file;
//    private LocalDateTime creationTime;
//
//    private double latitude;
//    private double longitude;

    public UnitImage(String path) {
        super(path);
    }

    public UnitImage(Parcel in) {
        super();

        File file = (File) in.readSerializable();
        this.setFile(file);

        LocalDateTime time = (LocalDateTime) in.readSerializable();
        this.setCreationTime(time);

        double lat = in.readDouble();
        double lng = in.readDouble();

        this.setLatitude(lat);
        this.setLongitude(lng);
    }

    /**
     * Implements Parcelable
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeSerializable(getFile());
        parcel.writeSerializable(getCreationTime());
        parcel.writeDouble(getLatitude());
        parcel.writeDouble(getLongitude());
    }

    public static final Creator<UnitImage> CREATOR = new Creator<UnitImage>() {
        @Override
        public UnitImage createFromParcel(Parcel in) {
            return new UnitImage(in);
        }

        @Override
        public UnitImage[] newArray(int size) {
            return new UnitImage[size];
        }
    };
}
