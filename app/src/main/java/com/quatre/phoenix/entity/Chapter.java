package com.quatre.phoenix.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;
import java.util.UUID;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@RequiredArgsConstructor
@NoArgsConstructor
public class Chapter implements Serializable {
    @PrimaryKey
    @NonNull
    private String id = UUID.randomUUID().toString();

    // foreign key Manga.id
    @NonNull
    private String idManga;

    @NonNull
    private String url;

    @NonNull
    private String name;

    private boolean isRead = false;

    private boolean isDownloaded = false;

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getIdManga() {
        return idManga;
    }

    public void setIdManga(@NonNull String idManga) {
        this.idManga = idManga;
    }

    @NonNull
    public String getUrl() {
        return url;
    }

    public void setUrl(@NonNull String url) {
        this.url = url;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }
}
