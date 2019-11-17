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

    private void loadImageGroups() {

        // Todo: imageGroups를 가져오기 위한 비동기 코드 구현 필요함.
    }
}