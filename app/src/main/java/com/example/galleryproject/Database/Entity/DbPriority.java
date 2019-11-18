package com.example.galleryproject.Database.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "priority")
public class DbPriority {
    @PrimaryKey (autoGenerate = true)
    public Integer id;

    @ColumnInfo(name = "rank")
    public Integer rank;

    @ColumnInfo(name = "category")
    public Integer category;

    public void setCategory(Integer category) {
        this.category = category;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }
}
