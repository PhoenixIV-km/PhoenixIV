package com.quatre.phoenix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.quatre.phoenix.entity.Manga;
import com.quatre.phoenix.impl.DownloadServiceImpl;
import com.quatre.phoenix.service.DownloadService;
import org.jsoup.HttpStatusException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RunWith(AndroidJUnit4.class)
public class DownloadServiceInstrumentedTest {

    public static final String URL = "https://manhuaplus.com/manga/demon-magic-emperor01/chapter-717/";
    public static final String MANGA_NAME = "MagicEmperor";
    public static final String CHAPTER = "717";
    public static final String URL_CLOUDFLARE = "https://www.toongod.org/webtoon/the-beginning-after-the-end-manhwa-a00cbc/";
    public static final String SLASH = "/";
    public static final String CSS_QUERY = "img";
    public static final int IMAGE_COUNT = 10;
    public static final String CSS_QUERY_CHAPTER_LIST = "ul > li.wp-manga-chapter > a";

    private Context context;
    private DownloadService downloadService;

    @Before
    public void init() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        downloadService = new DownloadServiceImpl();
    }

    @Test
    public void useAppContext() {
        assertEquals("com.quatre.phoenix", context.getPackageName());
    }

    // asynchronous unit tests needs Instrumented
    @Test
    public void testGetAllPicturesFromUrl() throws InterruptedException, ExecutionException {
        final var pictures = downloadService.getAllElementsFromUrl(URL, CSS_QUERY).get();
        assertEquals(IMAGE_COUNT, pictures.size());
        final var last = pictures.get(pictures.size() - 1);
        assertNotNull(last);
        assertEquals(CSS_QUERY, last.tag().getName());
        final var src = last.attribute("src");
        assertNotNull(src);
        assertEquals("//pixel.quantserve.com/pixel/p-PZmZQZSQ-y1yB.gif", src.getValue());
    }

    // BitmapFactory.decodeStream() needs Instrumented
    // Stores in /data/data/com.quatre.phoenix/files/MagicEmperor/717/0.jpg
    @Test
    public void testStoreAllPicturesOnInternalMemory() throws IOException, InterruptedException, ExecutionException {
        final var pictures = downloadService.getAllElementsFromUrl(URL, CSS_QUERY).get();
        final var path = Paths.get(context.getFilesDir().getPath());
        final var manga = new Manga(URL, MANGA_NAME, CSS_QUERY_CHAPTER_LIST);
        final var files = downloadService.storeAllPicturesOnInternalMemory(pictures, manga, CHAPTER, path.toAbsolutePath().toString()).get();
        assertEquals(IMAGE_COUNT, files.size());
        final var sortedList = files.stream().sorted().collect(Collectors.toList());
        final var last = sortedList.get(sortedList.size() - 1);
        assertEquals(path + SLASH + MANGA_NAME + SLASH + CHAPTER + SLASH + "9.jpg", last.toPath().toAbsolutePath().toString());
        try (FileChannel imageFileChannel = FileChannel.open(last.toPath())) {
            assertEquals(839, imageFileChannel.size());
        }
    }

    // BitmapFactory.decodeStream() needs Instrumented
    @Test
    public void testCloudfareThrows() {
        try {
            downloadService.getAllElementsFromUrl(URL_CLOUDFLARE, CSS_QUERY).get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();  // Unwrap the real exception
            if (cause instanceof HttpStatusException) {
                HttpStatusException httpEx = (HttpStatusException) cause;
                assertEquals(403, httpEx.getStatusCode());
                assertEquals("HTTP error fetching URL. Status=403, URL=[https://www.toongod.org/webtoon/the-beginning-after-the-end-manhwa-a00cbc/]", httpEx.getMessage());
            } else {
                fail();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();  // Always re-interrupt
            fail();
        }
    }
}