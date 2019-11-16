package com.example.galleryproject.Database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.galleryproject.Database.Entity.DbLabel;
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


    /**
     * RepImage (ImageCollection - Image) 관계 호출
     * @param collectionId
     * @return
     */
    @Query("SELECT * FROM rep_image WHERE collection_id = (:collectionId)")
    public abstract List<DbRepImage> loadAllByImageGroupId(int collectionId);

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
