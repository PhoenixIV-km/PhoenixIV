package com.quatre.phoenix.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import java.io.Serializable;
import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity(foreignKeys = @ForeignKey(entity = Manga.class, parentColumns = "id", childColumns = "idManga"))
@Getter
@Setter
@RequiredArgsConstructor
public class Chapter implements Serializable {
    @PrimaryKey
    @NonNull
    private String id = String.valueOf(UUID.randomUUID());

    @NonNull
    private String idManga;

    @NonNull
    private String url;

    @NonNull
    private String name;

    private boolean isRead = false;
}
