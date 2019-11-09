package com.example.galleryproject.Database.Dao;

import android.os.Debug;
import android.util.Log;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Transaction;

import com.example.galleryproject.Database.Entity.DbImage;
import com.example.galleryproject.Database.Entity.DbImageGroup;
import com.example.galleryproject.Database.Entity.DbLabel;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public abstract class DbImagesWithImageGroupDao {
    @Transaction
    public void insertImagesWithImageGroup(DbImageGroup group, List<DbImage> images, List<List<DbLabel>> labels) {
        // Save rowId of inserted CompanyEntity as companyId
        final long groupId = insert(group);

        // Set companyId for all related employeeEntities
        int imageCount = images.size();
        int labelCount = labels.size();

        assert(imageCount == labelCount);

        for (int iIdx = 0; iIdx < imageCount; iIdx++) {
            DbImage image = images.get(iIdx);

            image.setGroupId((int) groupId);
            long imageId = insert(image);

            List<DbLabel> labelGroup = labels.get(iIdx);
            for (int lIdx = 0; lIdx < labelGroup.size(); lIdx++) {
                DbLabel label = labelGroup.get(lIdx);

                label.setImageId((int) imageId);
                insert(label);
            }
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
