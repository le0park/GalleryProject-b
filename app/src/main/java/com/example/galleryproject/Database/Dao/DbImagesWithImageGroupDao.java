package com.example.galleryproject.Database.Dao;

import android.os.Debug;
import android.util.Log;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Transaction;

import com.example.galleryproject.Database.Entity.DbImage;
import com.example.galleryproject.Database.Entity.DbImageGroup;
import com.example.galleryproject.Database.Entity.DbLabel;
import com.example.galleryproject.Model.Adapter.ImageAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public abstract class DbImagesWithImageGroupDao {
    @Transaction
    public List<Long> insertImagesWithImageGroupId(long groupId, List<DbImage> images) {
        List<Long> imageIds = new ArrayList<>();
        for (DbImage image: images) {
            image.setGroupId((int) groupId);

            long imageId = insert(image);
            imageIds.add(imageId);
        }

        return imageIds;
    }

    @Transaction
    public void insertLabelsWithImageId(long imageId, List<DbLabel> labels) {
        for (DbLabel label: labels) {
            label.setImageId((int) imageId);
            insert(label);
        }
    }


    @Insert
    public abstract long insert(DbImage image);

    @Insert
    public abstract long insert(DbLabel label);

    @Insert
    public abstract void insert(DbLabel... labels);


    // If the @Insert method receives only 1 parameter, it can return a long,
    // which is the new rowId for the inserted item.
    // https://developer.android.com/training/data-storage/room/accessing-data
    @Insert(onConflict = REPLACE)
    public abstract long insert(DbImageGroup group);
}
