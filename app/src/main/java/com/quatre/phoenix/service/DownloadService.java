package com.quatre.phoenix.service;

import org.jsoup.select.Elements;
import java.io.IOException;

public interface DownloadService {
    Elements getAllPicturesFromUrl(String url, String cssQuery) throws IOException;
}
