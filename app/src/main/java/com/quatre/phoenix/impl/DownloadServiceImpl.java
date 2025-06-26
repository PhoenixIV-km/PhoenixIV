package com.quatre.phoenix.impl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.quatre.phoenix.service.DownloadService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DownloadServiceImpl implements DownloadService {

    @Override
    public Elements getAllPicturesFromUrl(String url, String cssQuery) throws IOException {
        Document doc = Jsoup.connect(url).get(); // Fetch the HTML content
        return doc.select(cssQuery); // Select elements using the CSS query
    }

    @Override
    public void storeAllPicturesOnInternalMemory(Elements elements, String mangaName, String chapter, String contextPath) throws IOException {
        var i = 0;
        for (final var element : elements) {
            storePictureOnInternalMemory(element, mangaName, chapter, contextPath, i++);
        }
    }

    private void storePictureOnInternalMemory(Element element, String mangaName, String chapter, String contextPath, int i) throws IOException {
        try {
            log.info("Storing manga {}, chapter {}, img {} in {}", mangaName, chapter, i, contextPath);

            // Open stream from the URL
            InputStream input = new URL(element.absUrl("src")).openStream();

            // Decode stream into a Bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(input);

            // Create file in internal storage
            final var path = Path.of(contextPath + "/" + mangaName + "/" + chapter + "/" + i + ".jpg");

            // Save bitmap to file
            FileOutputStream output = new FileOutputStream(path.toFile());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);

            output.flush();
            output.close();
            input.close();
        } catch (IOException e) {
            log.error("Error storing picture", e);
            throw e;
        }
    }
}
