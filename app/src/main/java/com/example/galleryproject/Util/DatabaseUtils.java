package com.example.galleryproject.Util;

import androidx.annotation.WorkerThread;

import com.example.galleryproject.Database.AppDatabase;
import com.example.galleryproject.Database.Entity.DbImage;
import com.example.galleryproject.Database.Entity.DbImageCollection;
import com.example.galleryproject.Database.Entity.DbImageGroup;
import com.example.galleryproject.Model.Adapter.DbImageAdapter;
import com.example.galleryproject.Model.Adapter.DbImageCollectionAdapter;
import com.example.galleryproject.Model.Adapter.DbImageGroupAdapter;
import com.example.galleryproject.Model.Image;
import com.example.galleryproject.Model.ImageCollection;
import com.example.galleryproject.Model.ImageGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseUtils {
    @WorkerThread
    public static List<ImageCollection> getCollectionsFromDbByRange(AppDatabase mDb, int count, int offset) {
        List<ImageCollection> collections = new ArrayList<>();
        List<DbImageCollection> dbCollections =
                mDb.dbImageCollectionDao().getRange(count, offset);

        for (DbImageCollection dbCollection: dbCollections) {
            List<DbImageGroup> dbImageGroups =
                    mDb.dbImageGroupDao().loadAllWithCollectionId(dbCollection.id);

            List<ImageGroup> imageGroups = new ArrayList<>();
            for (DbImageGroup group: dbImageGroups) {
                List<DbImage> dbImages = mDb.dbImageDao()
                        .loadWithGroupId(group.id);

                List<Image> newImages = dbImages.stream()
                        .map(DbImageAdapter::new)
                        .collect(Collectors.toList());

                imageGroups.add(new DbImageGroupAdapter(group, newImages));
            }

            List<DbImage> dbImages = mDb.dbRepImageDao().getRepImageForCollection(dbCollection.id);
            List<Image> repImages = dbImages.stream()
                    .map(DbImageAdapter::new)
                    .collect(Collectors.toList());

            collections.add(new DbImageCollectionAdapter(dbCollection, imageGroups, repImages));
        }

        return collections;
    }
}
