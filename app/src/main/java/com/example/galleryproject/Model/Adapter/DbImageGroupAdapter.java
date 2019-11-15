package com.example.galleryproject.Model.Adapter;

import android.os.Parcel;
import com.example.galleryproject.Database.Entity.DbImageGroup;
import com.example.galleryproject.Model.Image;
import com.example.galleryproject.Model.ImageGroup;
import java.util.ArrayList;
import java.util.List;

public class DbImageGroupAdapter extends ImageGroup {
    private DbImageGroup dbImageGroup;

    public DbImageGroupAdapter(DbImageGroup dbImageGroup, List<Image> images) {
        super(images);

        this.dbImageGroup = dbImageGroup;
    }

    /**
     * Implements Parcelable
     */
    public DbImageGroupAdapter(Parcel in) {
        super();

        List<Image> images = new ArrayList<>();
        in.readList(images, Image.class.getClassLoader());
        this.setImages(images);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeList(images);
    }

    public static final Creator<DbImageGroupAdapter> CREATOR = new Creator<DbImageGroupAdapter>() {
        @Override
        public DbImageGroupAdapter createFromParcel(Parcel in) {
            return new DbImageGroupAdapter(in);
        }

        @Override
        public DbImageGroupAdapter[] newArray(int size) {
            return new DbImageGroupAdapter[size];
        }
    };
}
