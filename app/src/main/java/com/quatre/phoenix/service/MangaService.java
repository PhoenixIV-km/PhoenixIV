package com.quatre.phoenix.service;

import com.google.common.util.concurrent.ListenableFuture;
import com.quatre.phoenix.entity.Manga;
import org.jsoup.nodes.Element;
import java.util.List;

public interface MangaService {
    void onDestroy();
    ListenableFuture<List<Element>> getAllElementsFromUrl(final String url, final String cssQuery);
    ListenableFuture<List<Manga>> getAllMangas();
    ListenableFuture<Void> addManga(Manga manga);
}
