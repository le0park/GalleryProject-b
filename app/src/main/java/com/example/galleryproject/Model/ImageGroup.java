package com.example.galleryproject.Model;

import android.os.Parcelable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class ImageGroup implements Parcelable {

    protected List<Image> images;
    protected String memo;

    public ImageGroup() {
        this(new ArrayList<>(), "");
    }

    public ImageGroup(List<Image> images) {
        this(images, "");
    }

    public ImageGroup(List<Image> images, String memo){
        this.images = images;
        this.memo = memo;
    }

    public void addImage(Image image) {
        images.add(image);
    }

    public List<String> getFilePaths() {
        List<String> paths = new ArrayList<>();
        for (Image image: images) {
            paths.add(image.getFile()
                           .toPath()
                           .toString());
        }

        return paths;
    }

    public List<Image> getImages() {
        return this.images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public LocalDateTime getDate() {
        return getStartDate();
    }

    public LocalDateTime getStartDate() {
        LocalDateTime start = LocalDateTime.now();
        for (Image image: images) {
            LocalDateTime t = image.getCreationTime();

            if (start.compareTo(t) >= 0) {
                start = t;
            }
        }

        return start;
    }


    public LocalDateTime getLastDate() {
        LocalDateTime last = LocalDateTime.now();
        for (Image image: images) {
            LocalDateTime t = image.getCreationTime();

            if (last.compareTo(t) < 0) {
                last = t;
            }
        }

        return last;
    }
}

