package com.quatre.phoenix;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.quatre.phoenix.impl.DownloadServiceImpl;
import com.quatre.phoenix.service.DownloadService;
import com.quatre.phoenix.utils.FileUtils;
import com.quatre.phoenix.utils.PictureCallback;
import org.jsoup.select.Elements;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class DownloadServiceInstrumentedTest {

    private Context context;
    private DownloadService downloadService;

    @Before
    public void init() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        downloadService = new DownloadServiceImpl();
    }

    @After
    public void teardown() {
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.quatre.phoenix", appContext.getPackageName());
    }

    // asynchronous unit tests needs Instrumented
    @Test
    public void testGetAllPicturesFromUrl() throws IOException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        downloadService.getAllPicturesFromUrl("https://manhuaplus.com/manga/demon-magic-emperor01/chapter-717/", "img", new PictureCallback() {
                    @Override
                    public void onResult(Elements pictures) {
                        assertEquals(10, pictures.size());
                        final var last = pictures.last();
                        assert last != null;
                        assertEquals("img", last.tag().getName());
                        final var src = last.attribute("src");
                        assert src != null;
                        assertEquals("//pixel.quantserve.com/pixel/p-PZmZQZSQ-y1yB.gif", src.getValue());
                        System.out.println("TEST SUCCEEEESSSSS");
                        latch.countDown(); // unblock
                    }

                    @Override
                    public void onError(Exception e) {
                        assertEquals(Boolean.FALSE, Boolean.TRUE);
                        latch.countDown(); // unblock
                    }
                }
        );
        // Wait for the async call to finish (timeout after 10 seconds to avoid hanging forever)
        latch.await(10, TimeUnit.SECONDS);
    }

    // BitmapFactory.decodeStream() needs Instrumented
    // Stores in /data/data/com.quatre.phoenix/files/MagicEmperor/717/0.jpg
    @Test
    public void testStoreAllPicturesOnInternalMemory() throws IOException {
        downloadService.getAllPicturesFromUrl("https://manhuaplus.com/manga/demon-magic-emperor01/chapter-717/", "img", new PictureCallback() {
                    @Override
                    public void onResult(Elements pictures) {
                        final var path = Paths.get(context.getFilesDir().getPath());
                        try {
                            downloadService.storeAllPicturesOnInternalMemory(pictures, "MagicEmperor", "717", path.toAbsolutePath().toString());
                            final var files = FileUtils.getFilesFromFolder(path);
                            assertEquals(10, files.size());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        assertEquals(Boolean.FALSE, Boolean.TRUE);
                    }
                }
        );
    }
}