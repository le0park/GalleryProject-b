package com.example.galleryproject.Database.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.galleryproject.Model.Image;
import com.example.galleryproject.Model.ImageGroup;

import java.time.LocalDateTime;
import java.util.Date;

import static androidx.room.ForeignKey.CASCADE;
import static androidx.room.ForeignKey.SET_NULL;


@Entity(tableName = "image_group",
        foreignKeys = @ForeignKey(entity = DbImageCollection.class,
                parentColumns = "id",
                childColumns = "collection_id",
                onUpdate = CASCADE,
                onDelete = SET_NULL),
        indices = @Index("collection_id"))
public class DbImageGroup {
    @PrimaryKey (autoGenerate = true)
    public Integer id;

    @ColumnInfo(name = "collection_id")
    public Integer collectionId;

    public void setCollectionId(Integer collectionId) {
        this.collectionId = collectionId;
    }
}
