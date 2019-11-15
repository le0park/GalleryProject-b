package com.example.galleryproject.Model.Adapter;

import com.example.galleryproject.Database.Entity.DbLabel;
import com.example.galleryproject.Model.Label;

public class DbLabelAdapter extends Label {

    private DbLabel dbLabel;

    public DbLabelAdapter(DbLabel dbLabel) {
        super();

        this.dbLabel = dbLabel;
        this.text = dbLabel.text;
        this.entityId = dbLabel.entityId;
        this.confidence = (float) dbLabel.confidence;
    }
}