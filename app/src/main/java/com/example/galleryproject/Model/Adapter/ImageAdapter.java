package com.example.galleryproject.Model.Adapter;

import com.example.galleryproject.Database.Entity.DbImage;
import com.example.galleryproject.Model.Image;
import com.example.galleryproject.Model.UnitImage;

import java.io.File;

public class ImageAdapter extends DbImage {

    public ImageAdapter(Image image) {
        super();

        File file = image.getFile();
        this.setPath(file);

        if (image instanceof UnitImage) {
            UnitImage uimage = (UnitImage) image;
            String hash = uimage.getImageHash();
            this.setHash(hash);
        }

        this.setStatus(0);
    }
}
