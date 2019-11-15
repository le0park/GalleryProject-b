package com.example.galleryproject.Database.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "input_data")
public class DbInputData {
    @PrimaryKey(autoGenerate = true)
    public Integer id;

    @ColumnInfo(name = "json_input")
    public String json;

    @ColumnInfo(name = "selected")
    public int selected;

    public void setJson(String json) {
        this.json = json;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public String getJson() {
        return json;
    }

    public int isSelected() {
        return selected;
    }
}
