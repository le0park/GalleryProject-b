package com.example.galleryproject.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.galleryproject.Database.Entity.DbLabel;
import com.example.galleryproject.Database.Entity.DbPriority;

import java.util.List;

@Dao
public interface DbPriorityDao {
    @Query("SELECT * FROM priority ORDER BY rank DESC;")
    List<DbPriority> getAll();

    @Query("DELETE FROM priority WHERE id > 0")
    void deleteAll();

    @Insert
    long insert(DbPriority priority);
}
