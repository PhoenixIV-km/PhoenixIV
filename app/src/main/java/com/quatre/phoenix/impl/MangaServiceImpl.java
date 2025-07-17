package com.quatre.phoenix.impl;

import com.google.common.util.concurrent.ListenableFuture;
import com.quatre.phoenix.PhoenixIVApplication;
import com.quatre.phoenix.entity.Manga;
import com.quatre.phoenix.service.MangaService;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MangaServiceImpl extends AbstractWebBrowserServiceImpl implements MangaService {

    @Override
    public ListenableFuture<List<Manga>> getAllMangas() {
        return listeningExecutor.submit(() -> PhoenixIVApplication.getDatabase().mangaDao().getAll());
    }

    @Override
    public ListenableFuture<Void> addManga(final Manga manga) {
        return listeningExecutor.submit(() -> {
            PhoenixIVApplication.getDatabase().mangaDao().insert(manga);
            return null;
        });
    }

    @Override
    public ListenableFuture<Void> addAllMangas(final List<Manga> mangas) {
        return listeningExecutor.submit(() -> {
            PhoenixIVApplication.getDatabase().mangaDao().insertAll(mangas);
            return null;
        });
    }

    @Override
    public ListenableFuture<Void> loadAllMangasFromCsv(final File csvFile) throws IOException {
        if (!csvFile.exists()) {
            throw new IllegalStateException("CSV not found");
        }
        final var mangas = readCsv(csvFile);
        return addAllMangas(mangas);
    }

    private List<Manga> readCsv(File csvFile) throws IOException {
        List<Manga> mangas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(";");
                mangas.add(new Manga(fields[0].trim(), fields[1].trim(), fields[2].trim()));
            }
        }
        return mangas;
    }

    @Override
    public ListenableFuture<Void> deleteAllMangas() {
        return listeningExecutor.submit(() -> {
            PhoenixIVApplication.getDatabase().mangaDao().deleteAll();
            return null;
        });
    }

    @Override
    public ListenableFuture<Void> deleteManga(final Manga manga) {
        return listeningExecutor.submit(() -> {
            PhoenixIVApplication.getDatabase().mangaDao().delete(manga);
            return null;
        });
    }
}
