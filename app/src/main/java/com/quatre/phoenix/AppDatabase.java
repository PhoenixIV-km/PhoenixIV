package com.quatre.phoenix;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.quatre.phoenix.dao.ChapterDao;
import com.quatre.phoenix.dao.MangaDao;
import com.quatre.phoenix.entity.Chapter;
import com.quatre.phoenix.entity.Manga;

// exportSchema = true (default)
@Database(entities = {Manga.class, Chapter.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MangaDao mangaDao();
    public abstract ChapterDao chapterDao();
}
