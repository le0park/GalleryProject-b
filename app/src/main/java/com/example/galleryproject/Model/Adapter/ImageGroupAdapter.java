package com.example.galleryproject.Model.Adapter;

import androidx.room.Ignore;

import com.example.galleryproject.Database.Entity.DbImageGroup;
import com.example.galleryproject.Model.Image;
import com.example.galleryproject.Model.ImageGroup;

import java.time.LocalDateTime;

public class ImageGroupAdapter extends DbImageGroup {
    public ImageGroupAdapter(ImageGroup imageGroup) {
        super();

        LocalDateTime start = null;
        LocalDateTime finish = null;

        for (Image image: imageGroup.getImages()) {
            LocalDateTime target = image.getCreationTime();

            if (start == null || start.isAfter(target)) {
                start = target;
            }

            if (finish == null || finish.isBefore(target)) {
                finish = target;
            }
        }

        this.setStartTime(start);
        this.setFinishTime(finish);
    }
}
