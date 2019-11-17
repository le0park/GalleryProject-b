package com.example.galleryproject.ui.timeLine;

import com.example.galleryproject.Model.ImageCollection;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImageCollectionLiveData extends ListLiveData<ImageCollection> {
    public ImageCollectionLiveData() {
        this(new ArrayList<>());
    }

    public ImageCollectionLiveData(List<ImageCollection> list){
        if (list.size() > 0) {
            Collections.sort(list, (t1, t2) -> {
                LocalDateTime d1 = t1.getDate();
                LocalDateTime d2 = t2.getDate();

                return d2.compareTo(d1);
            });
        }

        setValue(list);
    }

    // Todo:  add 할 때 시간 데이터를 보고 다른 그룹과 합치는 과정도 있어야함.
    @Override
    public void add(ImageCollection item){
        List<ImageCollection> groups = getValue();

        int position = searchInsertPosition(groups, item);
        groups.add(position, item);

        setValue(groups);
    }

    @Override
    public void addAll(List<ImageCollection> items){
        List<ImageCollection> groups = getValue();

        for (ImageCollection item: items) {
            int position = searchInsertPosition(groups, item);
            groups.add(position, item);
        }

        setValue(groups);
    }

    private static int searchInsertPosition(List<ImageCollection> list, ImageCollection target) {
        LocalDateTime tt = target.getDate();

        int i = 0;
        int j = list.size() - 1;

        while (i <= j) {
            int mid = (i + j) / 2;

            if (tt.compareTo(list.get(mid).getDate()) <  0){
                i = mid + 1;
            } else if (tt.compareTo(list.get(mid).getDate()) > 0){
                j = mid - 1;
            } else {
                return mid;
            }
        }

        return i;
    }
}
