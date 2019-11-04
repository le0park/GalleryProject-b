package com.example.galleryproject.ui.timeLine;

import com.example.galleryproject.Model.ImageGroup;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ImageGroupListLiveData extends ListLiveData<ImageGroup> {
    public ImageGroupListLiveData() {
        this(new ArrayList<>());
    }

    public ImageGroupListLiveData(List<ImageGroup> list){
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
    public void add(ImageGroup item){
        List<ImageGroup> groups = getValue();

        int position = searchInsertPosition(groups, item);
        groups.add(position, item);

        setValue(groups);
    }

    @Override
    public void addAll(List<ImageGroup> items){
        List<ImageGroup> groups = getValue();

        for (ImageGroup item: items) {
            int position = searchInsertPosition(groups, item);
            groups.add(position, item);
        }

        setValue(groups);
    }

    private static int searchInsertPosition(List<ImageGroup> list, ImageGroup target) {
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
