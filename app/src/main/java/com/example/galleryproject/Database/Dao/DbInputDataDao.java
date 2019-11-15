package com.example.galleryproject.Database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.galleryproject.Database.Entity.DbImage;
import com.example.galleryproject.Database.Entity.DbInputData;

import java.util.List;

@Dao
public interface DbInputDataDao {
    @Query("SELECT * FROM input_data")
    List<DbInputData> getAll();

    @Insert
    void insert(DbInputData dbInputData);

    @Insert
    void insertAll(DbInputData... dbInputData);


    @Query("DELETE FROM input_data WHERE id > 0")
    void reset();
}
