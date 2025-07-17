package com.quatre.phoenix.service;

import com.google.common.util.concurrent.ListenableFuture;
import com.quatre.phoenix.entity.Manga;
import org.jsoup.nodes.Element;
import java.io.File;
import java.io.IOException;
import java.util.List;

public interface MangaService {
    // inherited from AbstractWebBrowserServiceImpl
    void onDestroy();
    ListenableFuture<List<Element>> getAllElementsFromUrl(final String url, final String cssQuery);

    // implemented
    ListenableFuture<List<Manga>> getAllMangas();
    ListenableFuture<Void> addManga(final Manga manga);
    ListenableFuture<Void> addAllMangas(final List<Manga> mangas);
    ListenableFuture<Void> deleteManga(final Manga manga);
    ListenableFuture<Void> deleteAllMangas();
    ListenableFuture<Void> loadAllMangasFromCsv(final File csvFile) throws IOException;
}
