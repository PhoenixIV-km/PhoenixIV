package com.quatre.phoenix.impl;

import com.google.common.util.concurrent.ListenableFuture;
import com.quatre.phoenix.PhoenixIVApplication;
import com.quatre.phoenix.entity.Chapter;
import com.quatre.phoenix.service.ChapterService;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChapterServiceImpl extends AbstractWebBrowserServiceImpl implements ChapterService {

    @Override
    public ListenableFuture<List<Chapter>> getAllChapters() {
        return listeningExecutor.submit(() -> PhoenixIVApplication.getDatabase().chapterDao().getAll());
    }

    @Override
    public ListenableFuture<Void> insertAllNewChapters(List<Chapter> chapters) throws ExecutionException, InterruptedException {
        final var oldChaptersUrl = getAllChapters().get().stream().map(Chapter::getUrl).collect(Collectors.toList());
        final var newChapters = chapters.stream().filter(c -> !oldChaptersUrl.contains(c.getUrl())).collect(Collectors.toList());
        return listeningExecutor.submit(() -> {
            PhoenixIVApplication.getDatabase().chapterDao().insertAll(newChapters);
            return null;
        });
    }

    @Override
    public ListenableFuture<Void> deleteAllChaptersFromManga(String idManga) {
        return listeningExecutor.submit(() -> {
            PhoenixIVApplication.getDatabase().chapterDao().deleteAll(idManga);
            return null;
        });
    }

    @Override
    public ListenableFuture<Void> markChaptersAsRead(String idChapter, boolean isRead) {
        return listeningExecutor.submit(() -> {
            PhoenixIVApplication.getDatabase().chapterDao().markChapterAsRead(idChapter, isRead);
            return null;
        });
    }

    @Override
    public ListenableFuture<Void> markAllChaptersAsRead(String idManga, boolean isRead) {
        return listeningExecutor.submit(() -> {
            PhoenixIVApplication.getDatabase().chapterDao().markAllChaptersAsRead(idManga, isRead);
            return null;
        });
    }

    @Override
    public ListenableFuture<Void> markChapterAsDownloaded(String idChapter, boolean isDownloaded) {
        return listeningExecutor.submit(() -> {
            PhoenixIVApplication.getDatabase().chapterDao().markChapterAsDownloaded(idChapter, isDownloaded);
            return null;
        });
    }
}
