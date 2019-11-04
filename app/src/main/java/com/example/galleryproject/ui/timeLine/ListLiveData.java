package com.example.galleryproject.ui.timeLine;

import androidx.lifecycle.MutableLiveData;

import com.example.galleryproject.Model.ImageGroup;

import java.util.ArrayList;
import java.util.List;

public class ListLiveData<T> extends MutableLiveData<List<T>> {

    public ListLiveData(){
        this(new ArrayList<>());
    }

    public ListLiveData(List<T> list){
        setValue(list);
    }

    public void add(T item){
        List<T> items = getValue();
        items.add(item);
        setValue(items);
    }

    public void addAll(List<T> list){
        List<T> items = getValue();
        items.addAll(list);
        setValue(items);
    }

    public void clear(boolean notify) {
        List<T> items = getValue();
        items.clear();
        if(notify){
            setValue(items);
        }
    }

    public void remove(T item){
        List<T> items = getValue();
        items.remove(item);
        setValue(items);
    }

    public void notifyChanged(){
        List<T> items = getValue();
        setValue(items);
    }

}