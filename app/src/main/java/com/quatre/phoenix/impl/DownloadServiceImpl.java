package com.quatre.phoenix.impl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.quatre.phoenix.service.DownloadService;
import com.quatre.phoenix.utils.PictureCallback;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DownloadServiceImpl implements DownloadService {

    private final ExecutorService executorPool = Executors.newCachedThreadPool();

    @Override
    public void onDestroy() {
        executorPool.shutdown();
    }

    @Override
    public void getAllPicturesFromUrl(String url, String cssQuery, PictureCallback callback) {
        executorPool.execute(() -> {
            try {
                Document doc = Jsoup.connect(url).get(); // Fetch the HTML content
                Elements elements = doc.select(cssQuery);
                callback.onResult(elements);
            } catch (IOException e) {
                log.error("Error while processing url {}", url, e);
                callback.onError(e);
            }
        });
    }

    @Override
    public void storeAllPicturesOnInternalMemory(Elements elements, String mangaName, String chapter, String contextPath) {
        var i = 0;
        for (final var element : elements) {
            final var index = i;
            executorPool.execute(() -> storePictureOnInternalMemory(element, mangaName, chapter, contextPath, index));
            i++;
        }
    }

    private void storePictureOnInternalMemory(Element element, String mangaName, String chapter, String contextPath, int i) {
        try {
            log.info("Storing manga {}, chapter {}, img {} in {}", mangaName, chapter, i, contextPath);

            // Open stream from the URL
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
        } catch (IOException e) {
            log.error("Error storing picture", e);
        }
    }
}
