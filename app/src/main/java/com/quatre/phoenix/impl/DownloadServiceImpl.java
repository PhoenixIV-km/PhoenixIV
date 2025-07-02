package com.quatre.phoenix.impl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.quatre.phoenix.service.DownloadService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DownloadServiceImpl implements DownloadService {

    private final ListeningExecutorService listeningExecutor = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

    @Override
    public void onDestroy() {
        listeningExecutor.shutdown();
    }

    @Override
    public ListenableFuture<List<Element>> getAllPicturesFromUrl(String url, String cssQuery) {
        return listeningExecutor.submit(() -> {
            try {
                Document doc = Jsoup.connect(url).get(); // Fetch the HTML content
                return doc.select(cssQuery).asList();
            } catch (IOException e) {
                log.error("Error while processing url {}", url, e);
                throw e;
            }
        });
    }

    @Override
    public ListenableFuture<List<File>> storeAllPicturesOnInternalMemory(List<Element> elements, String mangaName, String chapter, String contextPath) {
        log.info("Storing manga {}, chapter {}, @{}", mangaName, chapter, contextPath);
        List<ListenableFuture<File>> futures = new ArrayList<>();
        var i = 0;
        for (final var element : elements) {
            final var index = i; // lambda needs final
            ListenableFuture<File> future = listeningExecutor.submit(() -> storePictureOnInternalMemory(element, mangaName, chapter, contextPath, index));
            i++;
            futures.add(future);
        }
        return Futures.allAsList(futures);
    }

    private File storePictureOnInternalMemory(Element element, String mangaName, String chapter, String contextPath, int i) throws IOException {
        log.info("Storing img {}.jpg", i);
        try {
            // Open stream from URL
            URL url = new URL(element.absUrl("src"));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");  // Spoof browser
            connection.setRequestProperty("Referer", "https://manhuaplus.com/");
            InputStream input = connection.getInputStream();

            // Decode stream into a Bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(input);

            // Create file in internal storage in /data/user/0/com.quatre.phoenix/files/MagicEmperor/717/0.jpg
            File file = new File(contextPath + "/" + mangaName + "/" + chapter + "/" + i + ".jpg");
            // Make sure all parent directories exist
            File parent = file.getParentFile();
            assert parent == null || parent.exists() || parent.mkdirs();
            // Make sure file exists
            assert file.exists() || file.createNewFile();

            // Save bitmap to file
            FileOutputStream output = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);

            output.flush();
            output.close();
            input.close();

            return file;
        } catch (IOException e) {
            log.error("Error storing picture", e);
            throw e;
        }
    }
}
