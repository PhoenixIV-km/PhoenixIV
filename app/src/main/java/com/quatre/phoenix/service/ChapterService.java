package com.quatre.phoenix.service;

import com.google.common.util.concurrent.ListenableFuture;
import com.quatre.phoenix.entity.Chapter;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface ChapterService {
    // inherited from AbstractWebBrowserServiceImpl
    void onDestroy();

    // implemented
    ListenableFuture<List<Chapter>> getAllChapters();
    ListenableFuture<Void> update(Chapter chapter);
    ListenableFuture<Void> insertAllNewChapters(List<Chapter> chapters) throws ExecutionException, InterruptedException;
    ListenableFuture<Void> deleteAllChaptersFromManga(String idManga);
    ListenableFuture<Void> markAllChaptersAsRead(String idManga, boolean isRead);
}
