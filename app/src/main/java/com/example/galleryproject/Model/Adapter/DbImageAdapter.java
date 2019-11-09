package com.example.galleryproject.Model.Adapter;

import android.os.Parcel;

import com.example.galleryproject.Database.Entity.DbImage;
import com.example.galleryproject.Model.Image;
import com.example.galleryproject.Model.UnitImage;

import java.io.File;
import java.time.LocalDateTime;

public class DbImageAdapter extends Image {
    private DbImage dbImage;

    public DbImageAdapter(DbImage dbImage) {
        super(dbImage.path);

        this.dbImage = dbImage;
    }

    public DbImageAdapter(Parcel in) {
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

    public static final Creator<DbImageAdapter> CREATOR = new Creator<DbImageAdapter>() {
        @Override
        public DbImageAdapter createFromParcel(Parcel in) {
            return new DbImageAdapter(in);
        }

        @Override
        public DbImageAdapter[] newArray(int size) {
            return new DbImageAdapter[size];
        }
    };
}
