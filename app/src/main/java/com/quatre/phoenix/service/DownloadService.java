package com.quatre.phoenix.service;

import com.google.common.util.concurrent.ListenableFuture;
import com.quatre.phoenix.entity.Manga;
import org.jsoup.nodes.Element;
import java.io.File;
import java.util.List;

public interface DownloadService {
    // inherited from AbstractWebBrowserServiceImpl
    void onDestroy();
    ListenableFuture<List<Element>> getAllElementsFromUrl(final String url, final String cssQuery);

    // implemented
    ListenableFuture<List<File>> storeAllPicturesOnInternalMemory(List<Element> elements, Manga manga, String chapter, String contextPath);
}
