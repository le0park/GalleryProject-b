package com.example.galleryproject.Model.Adapter;

import androidx.room.Ignore;

import com.example.galleryproject.Database.Entity.DbLabel;
import com.example.galleryproject.Model.Label;

public class LabelAdapter extends DbLabel {
    public LabelAdapter (Label label) {
        super();

        String text = label.getText();
        double confidence = label.getConfidence();
        String entityId = label.getEntityId();

        this.setText(text);
        this.setConfidence(confidence);
        this.setEntityId(entityId);
    }

}
