package com.quatre.phoenix.impl;

import androidx.room.Update;
import com.google.common.util.concurrent.ListenableFuture;
import com.quatre.phoenix.dao.ChapterDao;
import com.quatre.phoenix.entity.Chapter;
import com.quatre.phoenix.service.ChapterService;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ChapterServiceImpl extends AbstractWebBrowserServiceImpl implements ChapterService {

    private final ChapterDao chapterDao;

    @Override
    public ListenableFuture<List<Chapter>> getAllChapters() {
        return listeningExecutor.submit(chapterDao::getAll);
    }

    @Update
    public ListenableFuture<Void> update(Chapter chapter) {
        return listeningExecutor.submit(() -> {
            chapterDao.update(chapter);
            return null;
        });
    }

    @Override
    public ListenableFuture<Void> insertAllNewChapters(List<Chapter> chapters) throws ExecutionException, InterruptedException {
        final var oldChaptersUrl = getAllChapters().get().stream().map(Chapter::getUrl).collect(Collectors.toList());
        final var newChapters = chapters.stream().filter(c -> !oldChaptersUrl.contains(c.getUrl())).collect(Collectors.toList());
        return listeningExecutor.submit(() -> {
            chapterDao.insertAll(newChapters);
            return null;
        });
    }

    @Override
    public ListenableFuture<Void> deleteAllChaptersFromManga(String idManga) {
        return listeningExecutor.submit(() -> {
            chapterDao.deleteAll(idManga);
            return null;
        });
    }

    @Override
    public ListenableFuture<Void> markAllChaptersAsRead(String idManga, boolean isRead) {
        return listeningExecutor.submit(() -> {
            chapterDao.markAllChaptersAsRead(idManga, isRead);
            return null;
        });
    }
}
