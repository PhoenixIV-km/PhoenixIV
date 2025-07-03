package com.quatre.phoenix.dao;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.quatre.phoenix.entity.Chapter;
import java.util.List;

public interface ChapterDao {
    @Query("SELECT * FROM chapter")
    List<Chapter> getAll();

    @Insert
    void insertAll(Chapter... chapters);

    @Delete
    void delete(Chapter chapter);
}
