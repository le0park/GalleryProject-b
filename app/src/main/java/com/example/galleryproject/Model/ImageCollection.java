package com.example.galleryproject.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ImageCollection implements Parcelable {
    private List<ImageGroup> groups;
    private String memo;

    public ImageCollection() {
        this(new ArrayList<>(), "");
    }

    public ImageCollection(List<ImageGroup> groups) {
        this(groups, "");
    }

    public ImageCollection(List<ImageGroup> groups, String memo) {
        this.groups = groups;
        this.memo = memo;
    }


    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public List<ImageGroup> getGroups() { return this.groups; }

    public void setGroups(List<ImageGroup> groups) { this.groups = groups; }

    public LocalDateTime getDate() {
        List<ImageGroup> groups = this.getGroups();

        return groups.size() > 0
                ? groups.get(0).getDate()
                : LocalDateTime.now();
    }


    /**
     * Implements Parcelable
     */
    public ImageCollection(Parcel in) {
        super();

        List<ImageGroup> imageGroups = new ArrayList<>();
        in.readList(imageGroups, ImageGroup.class.getClassLoader());
        this.setGroups(imageGroups);

        String memo = in.readString();
        this.setMemo(memo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeList(this.groups);
        parcel.writeString(this.memo);
        parcel.writeString(this.memo);
    }

    public static final Creator<ImageCollection> CREATOR = new Creator<ImageCollection>() {
        @Override
        public ImageCollection createFromParcel(Parcel in) {
            return new ImageCollection(in);
        }

        @Override
        public ImageCollection[] newArray(int size) {
            return new ImageCollection[size];
        }
    };
}
