package com.quatre.phoenix.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class Manga implements Serializable {
    @PrimaryKey
    @Setter(AccessLevel.NONE)
    @NonNull
    private String id = UUID.randomUUID().toString();

    @NonNull
    private String url;

    @NonNull
    private String name;

    private String picturePath;
}
