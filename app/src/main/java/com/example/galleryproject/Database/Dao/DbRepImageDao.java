package com.example.galleryproject.Database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.galleryproject.Database.Entity.DbImage;
import com.example.galleryproject.Database.Entity.DbRepImage;

import java.util.List;

@Dao
public abstract class DbRepImageDao {

    @Query("SELECT * FROM rep_image")
    public abstract List<DbRepImage> getAll();

    @Query("SELECT * FROM rep_image WHERE id = (:id)")
    public abstract List<DbRepImage> loadAllById(int id);

    @Query("SELECT * FROM rep_image WHERE id IN (:ids)")
    public abstract List<DbRepImage> loadAllByIds(int[] ids);

    @Query("SELECT * FROM image INNER JOIN rep_image " +
            "ON image.id = rep_image.image_id " +
            "WHERE rep_image.collection_id = :collectionId")
    public abstract List<DbImage> getRepImageForCollection(final int collectionId);


    /**
     * RepImage (ImageCollection - Image) 관계 호출
     * @param collectionId
     * @return
     */
    @Query("SELECT * FROM rep_image WHERE collection_id = (:collectionId)")
    public abstract List<DbRepImage> loadAllByCollectionId(int collectionId);

    /**
     * RepImage 저장
     * @param repImage
     * @param collectionId
     * @param imageId
     */
    @Transaction
    public void insertWithCollectionAndImage (DbRepImage repImage, int collectionId, int imageId) {
        repImage.setCollectionId(collectionId);
        repImage.setImageId(imageId);
        insert(repImage);
    }

    @Insert
    public abstract void insertAll(DbRepImage... dbRepImages);

    @Insert
    public abstract void insert(DbRepImage dbRepImage);

    @Delete
    public abstract void delete(DbRepImage dbRepImage);
}
