package com.example.galleryproject;

import androidx.lifecycle.ViewModel;

import com.example.galleryproject.Model.ImageCollection;
import com.example.galleryproject.ui.timeLine.ImageCollectionLiveData;

import java.util.List;

public class ImageCollectionViewModel extends ViewModel {
    private volatile ImageCollectionLiveData imageCollections;

    public ImageCollectionLiveData getImageCollections() {
        if (imageCollections == null) {
            synchronized (ImageCollectionViewModel.class) {
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