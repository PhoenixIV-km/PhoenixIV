package com.quatre.phoenix.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.quatre.phoenix.entity.Manga;
import java.util.List;

@Dao
public interface MangaDao {
    @Query("SELECT * FROM manga")
    List<Manga> getAll();

    @Insert
    void insertAll(Manga... mangas);

    @Delete
    void delete(Manga manga);
}
