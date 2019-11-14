package com.example.galleryproject.Database.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

import static androidx.room.ForeignKey.CASCADE;
import static androidx.room.ForeignKey.SET_NULL;


@Entity(tableName = "image_collection")
public class DbImageCollection {
    @PrimaryKey (autoGenerate = true)
    public Integer id;

    @ColumnInfo(name = "start_time")
    public LocalDateTime startTime;

    @ColumnInfo(name = "finish_time")
    public LocalDateTime finishTime;

    @ColumnInfo(name = "memo")
    public String memo;

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setFinishTime(LocalDateTime finishTime) {
        this.finishTime = finishTime;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
