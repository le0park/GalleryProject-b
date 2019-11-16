package com.example.galleryproject.Database.Entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "rep_image",
        foreignKeys = {
            @ForeignKey(entity = DbImage.class,
                        parentColumns = "id",
                        childColumns = "image_id"),
            @ForeignKey(entity = DbImageCollection.class,
                    parentColumns = "id",
                    childColumns = "collection_id")
        },
        indices = {
            @Index("collection_id"),
            @Index("image_id")
        })

public class DbRepImage {
    @PrimaryKey(autoGenerate = true)
    public Integer id;

    @ColumnInfo(name = "image_id")
    public Integer imageId;

    @ColumnInfo(name = "collection_id")
    public Integer collectionId;

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public void setCollectionId(Integer collectionId) {
        this.collectionId = collectionId;
    }
}
