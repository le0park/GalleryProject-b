package com.example.galleryproject.Model.dbscan;

import android.util.Log;

import com.example.galleryproject.Model.Image;
import com.example.galleryproject.Model.UnitImage;
import com.example.galleryproject.Model.imagehash.Hamming;

import java.io.File;


public class ImageDataPoint implements DataPoint {
    /**
     * Image --> UnitImage 라고 가정
     */
    private int clusterId = -1;
    private Image file;

    public ImageDataPoint(Image file) {

        if (file instanceof UnitImage) {
            this.file = file;
        }

        this.file = null;
    }

    public double distance(DataPoint datapoint) {
        ImageDataPoint p2 = null;
        if (datapoint instanceof ImageDataPoint) {
            p2 = (ImageDataPoint) datapoint;
        }

        if (p2 == null) {
            Log.e("IMAGE_DATA_POINT", "서로 다른 형식의 DataPoint 입니다. ");
            return 99999999.9;
        }

        return (double) new Hamming(this.getHash(), p2.getHash())
                                .getHammingDistance();
    }

    public Image getFile() {
        return this.file;
    }

    private String getHash() {

        if (this.file instanceof UnitImage){
            UnitImage ui = (UnitImage) this.file;
            return ui.getImageHash();
        }

        return null;
    }

    @Override
    public void setCluster(int id) {
        this.clusterId = id;
    }

    @Override
    public int getCluster() {
        return this.clusterId;
    }

    @Override
    public int getX() { return 0; }

    @Override
    public int getY() { return 0; }

    @Override
    public String toString() {
        return file.getFile().toPath().toString();
    }
}
