package com.example.galleryproject.ui.map;

import androidx.lifecycle.ViewModel;

import com.example.galleryproject.Model.ImageCollection;
import com.example.galleryproject.ui.timeLine.ImageCollectionLiveData;
import com.example.galleryproject.ui.timeLine.TimeLineViewModel;

import java.util.List;

public class MapViewModel extends ViewModel {
    private ImageCollectionLiveData imageCollections;

    public ImageCollectionLiveData getImageCollections() {
        if (imageCollections == null) {
            synchronized (TimeLineViewModel.class) {
                if (imageCollections == null) {
                    imageCollections = new ImageCollectionLiveData();
                }
            }
        }

        return imageCollections;
    }

    public void insert(ImageCollection item) {
        imageCollections.add(item);
    }

    public void insertAll(List<ImageCollection> items) {
        imageCollections.addAll(items);
    }
}