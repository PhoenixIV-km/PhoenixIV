package com.quatre.phoenix.service;

import com.quatre.phoenix.utils.PictureCallback;
import org.jsoup.select.Elements;
import java.io.IOException;

public interface DownloadService {
    void onDestroy();

    void getAllPicturesFromUrl(String url, String cssQuery, PictureCallback pictureCallback) throws IOException;
    void storeAllPicturesOnInternalMemory(Elements elements, String mangaName, String chapter, String contextPath) throws IOException;
}
