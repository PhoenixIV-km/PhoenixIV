package com.quatre.phoenix.impl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.quatre.phoenix.entity.Manga;
import com.quatre.phoenix.service.DownloadService;
import com.quatre.phoenix.utils.FileUtils;
import com.quatre.phoenix.utils.UrlUtils;
import org.jsoup.nodes.Element;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DownloadServiceImpl extends AbstractWebBrowserServiceImpl implements DownloadService {

    @Override
    public ListenableFuture<List<File>> storeAllPicturesOnInternalMemory(List<Element> elements, Manga manga, String chapter, String contextPath) {
        log.info("Storing manga {}, chapter {}, @{}", manga.getName(), chapter, contextPath);
        List<ListenableFuture<File>> futures = new ArrayList<>();
        var i = 0;
        for (final var element : elements) {
            final var index = i; // lambda needs final
            ListenableFuture<File> future = listeningExecutor.submit(() -> storePictureOnInternalMemory(element, manga, chapter, contextPath, index));
            i++;
            futures.add(future);
        }
        return Futures.allAsList(futures);
    }

    private File storePictureOnInternalMemory(Element element, Manga manga, String chapterName, String contextPath, int i) throws IOException {
        log.info("Storing img {}.jpg", i);
        try {
            // Open stream from URL
            URL url = new URL(element.absUrl("src"));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");  // Spoof browser
            connection.setRequestProperty("Referer", UrlUtils.extractBaseUrl(manga.getUrl()));
            InputStream input = connection.getInputStream();

            // Decode stream into a Bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(input);

            // Create file in internal storage in /data/user/0/com.quatre.phoenix/files/MagicEmperor/717/0.jpg
            File file = new File(contextPath + "/" + manga.getName() + "/" + chapterName + "/" + i + ".jpg");
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
        } catch (MalformedURLException e) {
            log.error("Error extracting url {}", element.absUrl("src"), e);
            throw e;
        } catch (IOException e) {
            log.error("Error storing picture", e);
            throw e;
        }
    }

    @Override
    public ListenableFuture<Void> deleteAllPicturesOnInternalMemory(String mangaName, String chapterName, String contextPath) throws RuntimeException {
        log.info("Storing manga {}, chapter {}, @{}", mangaName, chapterName, contextPath);
        return listeningExecutor.submit(() -> {
            final var folder = new File(contextPath + "/" + mangaName + "/" + chapterName);
            if (!FileUtils.deleteDirectory(folder)) {
                throw new RuntimeException("Couldn't delete directory");
            }
            return null;
        });
    }
}
