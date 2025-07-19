package com.quatre.phoenix;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.quatre.phoenix.dao.ChapterDao;
import com.quatre.phoenix.dao.MangaDao;
import lombok.Getter;

public class PhoenixIVApplication extends Application {

    @Getter
    private static AppDatabase database;
    @Getter
    private static MangaDao mangaDao;
    @Getter
    private static ChapterDao chapterDao;

    @Override
    public void onCreate() {
        super.onCreate();

        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "app-database")
                .addMigrations(MIGRATION_1_2)
                .build();

        mangaDao = database.mangaDao();
        chapterDao = database.chapterDao();
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // For example: adding a new column
            database.execSQL("ALTER TABLE Manga ADD COLUMN 'order' INTEGER NOT NULL DEFAULT 99");
        }
    };
}
