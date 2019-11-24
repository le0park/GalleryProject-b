package com.example.galleryproject.Model;

import android.util.Log;

import com.example.galleryproject.Model.dbscan.Cluster;
import com.example.galleryproject.Model.dbscan.DBSCAN;
import com.example.galleryproject.Model.dbscan.DataPoint;
import com.example.galleryproject.Model.dbscan.ImageDataPoint;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SimGroupAlgorithm implements IGroupAlgorithm {
    private static final int MAX_DISTANCE = 20;
    private static final int MIN_POINTS = 1;

    @Override
    public List<ImageGroup> processImages(List<Image> images) {

        // DataPoint 초기화
        List<DataPoint> points = new ArrayList<>();
        for (Image image: images) {
            points.add(new ImageDataPoint(image));
        }


        // DBSCAN cluster
        DBSCAN dbscan = new DBSCAN(MAX_DISTANCE, MIN_POINTS);
        dbscan.setPoints(points);
        dbscan.cluster();


        // Group화
        List<ImageGroup> groups = new ArrayList<>();

        List<Cluster> clusters = dbscan.getClusters();
        for (Cluster c: clusters) {
            groups.add(new UnitImageGroup());
            ImageGroup group = groups.get(groups.size() - 1);

            for (DataPoint point: c.getPoints()) {
                ImageDataPoint iPoint;

                if (point instanceof ImageDataPoint) {
                    iPoint = (ImageDataPoint) point;
                    Image file = iPoint.getFile();

                    group.addImage(file);
                }
            }
        }

        return groups;
    }








}
