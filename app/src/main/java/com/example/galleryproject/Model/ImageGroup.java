package com.example.galleryproject.Model;

import android.os.Parcelable;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class ImageGroup implements Parcelable {

    protected List<Image> images;

    public ImageGroup() {
        this(new ArrayList<>());
    }

    public ImageGroup(List<Image> images) {
        this.images = images;
    }


    public void addImage(Image image) {
        images.add(image);
    }

    public List<File> getFiles() {
        List<File> paths = new ArrayList<>();
        for (Image image: images) {
            paths.add(image.getFile());
        }

        return paths;
    }

    public List<Image> getImages() {
        return this.images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
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

