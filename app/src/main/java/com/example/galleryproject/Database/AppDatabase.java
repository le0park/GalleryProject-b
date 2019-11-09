package com.example.galleryproject.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.galleryproject.Database.Dao.DbImageDao;
import com.example.galleryproject.Database.Dao.DbImagesWithImageGroupDao;
import com.example.galleryproject.Database.Dao.DbMlkitLabelDao;
import com.example.galleryproject.Database.Entity.DbImage;
import com.example.galleryproject.Database.Entity.DbImageGroup;
import com.example.galleryproject.Database.Entity.DbLabel;
import com.example.galleryproject.Database.Entity.DbPost;
import com.example.galleryproject.Database.Entity.DbRepImage;


@Database(entities = {
                DbImage.class,
                DbLabel.class,
                DbImageGroup.class,
                DbRepImage.class,
                DbPost.class
            },
          version = 1,
          exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract DbImagesWithImageGroupDao imagesWithImageGroupDao();

    private static volatile AppDatabase sInstance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                   sInstance = Room
                        .databaseBuilder(context.getApplicationContext(), AppDatabase.class, "liflow_database")
                        .build();
                }
            }
        }

        return sInstance;
    }
}