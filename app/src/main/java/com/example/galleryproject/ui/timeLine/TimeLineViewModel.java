package com.example.galleryproject.ui.timeLine;

import androidx.lifecycle.ViewModel;

import com.example.galleryproject.Model.ImageGroup;

import java.util.List;

public class TimeLineViewModel extends ViewModel {

    private volatile ImageGroupListLiveData imageGroups;

    public ImageGroupListLiveData getImageGroups() {
        if (imageGroups == null) {
            synchronized (TimeLineViewModel.class) {
                if (imageGroups == null) {
                    imageGroups = new ImageGroupListLiveData();
                }
            }
        }

        return imageGroups;
    }

    public void insert(ImageGroup item) {
        imageGroups.add(item);
    }


    public void insertAll(List<ImageGroup> items) {
        imageGroups.addAll(items);
    }

    private void loadImageGroups() {

        // Todo: imageGroups를 가져오기 위한 비동기 코드 구현 필요함.
    }
}