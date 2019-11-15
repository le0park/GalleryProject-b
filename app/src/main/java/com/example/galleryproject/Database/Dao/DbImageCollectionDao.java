package com.example.galleryproject.Database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.galleryproject.Database.Entity.DbImageCollection;
import com.example.galleryproject.Database.Entity.DbImageGroup;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class DbImageCollectionDao {
    @Query("SELECT * FROM image_collection")
    public abstract List<DbImageCollection> getAll();

    @Query("SELECT * FROM image_collection WHERE id = (:id)")
    public abstract List<DbImageCollection> loadAllById(int id);

    @Insert
    public abstract void insertAll(DbImageCollection... dbImageCollections);

    @Transaction
    public List<Long> insertWithGroups(DbImageCollection collection, List<DbImageGroup> groups) {
        final long collectionId = insert(collection);

        List<Long> groupIds = new ArrayList<>();
        for (DbImageGroup group: groups) {
            group.setCollectionId((int) collectionId);
            Long groupId = insert(group);

            groupIds.add(groupId);
        }

        return groupIds;
    }

    @Insert
    public abstract long insert(DbImageCollection dbImageCollection);

    @Insert
    public abstract long insert(DbImageGroup dbImageGroup);

    @Delete
    public abstract void delete(DbImageCollection dbImageCollection);
}