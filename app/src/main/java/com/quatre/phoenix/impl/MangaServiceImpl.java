package com.quatre.phoenix.impl;

import com.google.common.util.concurrent.ListenableFuture;
import com.quatre.phoenix.PhoenixIVApplication;
import com.quatre.phoenix.entity.Manga;
import com.quatre.phoenix.service.MangaService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MangaServiceImpl extends AbstractWebBrowserServiceImpl implements MangaService {

    @Override
    public ListenableFuture<List<Manga>> getAllMangas() {
        return listeningExecutor.submit(() -> PhoenixIVApplication.getAppDatabase().mangaDao().getAll());
    }
}
