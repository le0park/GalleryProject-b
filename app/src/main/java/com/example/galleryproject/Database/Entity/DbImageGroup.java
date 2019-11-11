package com.example.galleryproject.Database.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.galleryproject.Model.Image;
import com.example.galleryproject.Model.ImageGroup;

import java.time.LocalDateTime;
import java.util.Date;


@Entity(tableName = "image_group")
public class DbImageGroup {
    @PrimaryKey (autoGenerate = true)
    public Integer id;

    @ColumnInfo(name = "start_time")
    public LocalDateTime startTime;

    @ColumnInfo(name = "finish_time")
    public LocalDateTime finishTime;

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setFinishTime(LocalDateTime finishTime) {
        this.finishTime = finishTime;
    }
}
