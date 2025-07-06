package com.quatre.phoenix;

import android.app.Application;
import androidx.room.Room;
import lombok.Getter;

public class PhoenixIVApplication extends Application {

    @Getter
    private static AppDatabase appDatabase;

    @Override
    public void onCreate() {
        super.onCreate();

        appDatabase = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "app-database"
        ).build();
    }
}
