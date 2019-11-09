package com.example.galleryproject.Database.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.galleryproject.Model.Image;
import com.example.galleryproject.Model.UnitImage;

import java.io.File;

import static androidx.room.ForeignKey.CASCADE;
import static androidx.room.ForeignKey.SET_NULL;

@Entity(tableName = "image",
        foreignKeys = @ForeignKey(entity = DbImageGroup.class,
                                  parentColumns = "id",
                                  childColumns = "group_id",
                                  onUpdate = CASCADE,
                                  onDelete = SET_NULL),
        indices = @Index("group_id"))
public class DbImage {
    @PrimaryKey (autoGenerate = true)
    public Integer id;

    @ColumnInfo(name = "path")
    public File path;

    @ColumnInfo(name = "imagehash")
    public String hash;

    /**
     * 0: 자동으로 입력이 된 상태
     * 1: 사용자가 수동 조작으로 이미지 그룹에 연결된 상태
     */
    @ColumnInfo(name = "status")
    public int status;

    @ColumnInfo(name = "group_id")
    public Integer groupId;

    public void setPath(File path) {
        this.path = path;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
