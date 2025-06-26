package com.quatre.phoenix.impl;

import com.quatre.phoenix.service.DownloadService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;

public class DownloadServiceImpl implements DownloadService {

    @Override
    public Elements getAllPicturesFromUrl(String url, String cssQuery) throws IOException {
        Document doc = Jsoup.connect(url).get(); // Fetch the HTML content
        return doc.select(cssQuery); // Select elements using the CSS query
    }
}
