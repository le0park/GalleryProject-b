package com.example.galleryproject.Database;


import androidx.room.TypeConverter;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;


public class Converters {
    @TypeConverter
    public static LocalDateTime fromTimestamp(Long value) {
        return value == null ? null : LocalDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneOffset.UTC);
    }

    @TypeConverter
    public static Long dateToTimestamp(LocalDateTime date) {
        return date == null ? null : date.atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    @TypeConverter
    public static File fromString(String path) { return path == null ? null : new File(path); }

    @TypeConverter
    public static String fromFile(File file) { return file == null ? null : file.toPath().toString(); }
}

