package com.example.galleryproject.Model.Adapter;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.galleryproject.Database.Entity.DbImageCollection;
import com.example.galleryproject.Model.ImageCollection;
import com.example.galleryproject.Model.ImageGroup;

import java.util.ArrayList;
import java.util.List;

public class DbImageCollectionAdapter extends ImageCollection {
    private DbImageCollection dbImageCollection;

    public DbImageCollectionAdapter(DbImageCollection dbImageCollection, List<ImageGroup> imageGroups) {
        super(imageGroups);

        this.dbImageCollection = dbImageCollection;

        String memo = this.dbImageCollection.memo;
        setMemo(memo);
    }

    /**
     * Implements Parcelable
     */
    public DbImageCollectionAdapter(Parcel in) {
        super();

        List<ImageGroup> groups = new ArrayList<>();
        in.readList(groups, ImageGroup.class.getClassLoader());
        this.setGroups(groups);

        String memo = in.readString();
        this.setMemo(memo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeList(getGroups());
        parcel.writeString(getMemo());
    }

    public static final Parcelable.Creator<DbImageCollectionAdapter> CREATOR = new Parcelable.Creator<DbImageCollectionAdapter>() {
        @Override
        public DbImageCollectionAdapter createFromParcel(Parcel in) {
            return new DbImageCollectionAdapter(in);
        }

        @Override
        public DbImageCollectionAdapter[] newArray(int size) {
            return new DbImageCollectionAdapter[size];
        }
    };
}
