package com.example.galleryproject.Model.Adapter;

import com.example.galleryproject.Database.Entity.DbImageCollection;
import com.example.galleryproject.Database.Entity.DbImageGroup;
import com.example.galleryproject.Model.ImageCollection;
import com.example.galleryproject.Model.ImageGroup;

import java.time.LocalDateTime;
import java.util.List;

public class ImageCollectionAdapter extends DbImageCollection {

    public ImageCollectionAdapter(ImageCollection imageCollection) {
        super();

        LocalDateTime start = null;
        LocalDateTime finish = null;

        List<ImageGroup> imageGroups = imageCollection.getGroups();

        for (ImageGroup group: imageGroups) {
            LocalDateTime target = group.getDate();

            if (start == null || start.isAfter(target)) {
                start = target;
            }

            if (finish == null || finish.isBefore(target)) {
                finish = target;
            }
        }

        this.setStartTime(start);
        this.setFinishTime(finish);

        String memo = imageCollection.getMemo();
        this.setMemo(memo);
    }
}
