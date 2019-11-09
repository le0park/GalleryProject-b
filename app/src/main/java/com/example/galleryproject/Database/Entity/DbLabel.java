package com.example.galleryproject.Database.Entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.galleryproject.Model.Label;


@Entity(tableName = "mlkit_label",
        foreignKeys = @ForeignKey(entity = DbImage.class,
                                  parentColumns = "id",
                                  childColumns = "image_id"),
        indices = {
            @Index("image_id")
        })
public class DbLabel {
    @PrimaryKey(autoGenerate = true)
    public Integer id;

    @ColumnInfo(name = "text")
    public String text;

    @ColumnInfo(name = "confidence")
    public double confidence;

    @ColumnInfo(name = "entity_id")
    public String entityId;

    @ColumnInfo(name = "image_id")
    public Integer imageId;

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

}
