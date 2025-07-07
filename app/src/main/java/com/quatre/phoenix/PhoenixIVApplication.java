package com.quatre.phoenix;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import lombok.Getter;

public class PhoenixIVApplication extends Application {

    @Getter
    private static AppDatabase appDatabase;

    @Override
    public void onCreate() {
        super.onCreate();

        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "app-database")
                .addMigrations(MIGRATION_1_2)
                .build();
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // For example: adding a new column
            database.execSQL("ALTER TABLE Manga ADD COLUMN 'order' INTEGER NOT NULL DEFAULT 99");
        }
    };
}
