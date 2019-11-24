package com.example.galleryproject.ui.timeLine;

import androidx.lifecycle.ViewModel;

import java.util.List;

import com.example.galleryproject.Model.ImageCollection;

public class TimeLineViewModel extends ViewModel {

    private volatile ImageCollectionLiveData imageCollections;

    public ImageCollectionLiveData getImageGroups() {
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