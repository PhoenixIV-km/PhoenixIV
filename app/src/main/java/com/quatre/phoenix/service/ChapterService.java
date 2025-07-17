package com.quatre.phoenix.service;

import com.google.common.util.concurrent.ListenableFuture;
import com.quatre.phoenix.entity.Chapter;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface ChapterService {
    ListenableFuture<List<Chapter>> getAllChapters();
    ListenableFuture<Void> insertAllNewChapters(List<Chapter> chapters) throws ExecutionException, InterruptedException;
    ListenableFuture<Void> deleteAllChaptersFromManga(String idManga);
    ListenableFuture<Void> markChaptersAsRead(String idChapter, boolean isRead);
    ListenableFuture<Void> markAllChaptersAsRead(String idManga, boolean isRead);
    ListenableFuture<Void> markChapterAsDownloaded(String idChapter, boolean isDownloaded);
}
