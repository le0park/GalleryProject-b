package com.example.galleryproject.Model;

import java.util.ArrayList;
import java.util.List;

public class LabelGroup {

    protected List<Label> labels;

    public LabelGroup() {
        this(new ArrayList<>());
    }

    public LabelGroup(List<Label> labels){
        this.labels = labels;
    }

    public List<Label> getLabels() {
        return this.labels;
    }
}
