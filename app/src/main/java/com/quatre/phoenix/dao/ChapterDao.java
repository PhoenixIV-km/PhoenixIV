package com.quatre.phoenix.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.quatre.phoenix.entity.Chapter;
import java.util.List;

@Dao
public interface ChapterDao {
    @Query("SELECT * FROM chapter")
    List<Chapter> getAll();

    @Insert
    void insertAll(List<Chapter> chapters);

    @Query("DELETE FROM chapter WHERE idManga = :idManga")
    void deleteAll(String idManga);

    @Query("UPDATE chapter SET isRead = :isRead WHERE id = :idChapter")
    void markChapterAsRead(String idChapter, boolean isRead);

    @Query("UPDATE chapter SET isRead = :isRead WHERE idManga = :idManga")
    void markAllChaptersAsRead(String idManga, boolean isRead);

    @Query("UPDATE chapter SET isDownloaded = :isDownloaded WHERE id = :idChapter")
    void markChapterAsDownloaded(String idChapter, boolean isDownloaded);
}
