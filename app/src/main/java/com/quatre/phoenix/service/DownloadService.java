package com.quatre.phoenix.service;

import com.google.common.util.concurrent.ListenableFuture;
import org.jsoup.nodes.Element;
import java.io.File;
import java.util.List;

public interface DownloadService {
    void onDestroy();

    ListenableFuture<List<Element>> getAllPicturesFromUrl(String url, String cssQuery);

    ListenableFuture<List<File>> storeAllPicturesOnInternalMemory(List<Element> elements, String mangaName, String chapter, String contextPath);
}
