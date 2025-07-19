package com.quatre.phoenix;

import android.content.Context;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import com.quatre.phoenix.dao.ChapterDao;
import com.quatre.phoenix.dao.MangaDao;
import org.junit.Before;

public abstract class AbstractRoomInstrumentedTest {

    public MangaDao mangaDao;
    public ChapterDao chapterDao;

    @Before
    public void initDb() {
        Context testContext = ApplicationProvider.getApplicationContext();

        final var testDb = Room.databaseBuilder(testContext, AppDatabase.class, "test-db")
                .allowMainThreadQueries() // only for testing
                .build();

        // Inject testDb into your DAO/service
        mangaDao = testDb.mangaDao();
        chapterDao = testDb.chapterDao();
    }
}
