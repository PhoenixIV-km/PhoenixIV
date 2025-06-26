package com.quatre.phoenix;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.quatre.phoenix.impl.DownloadServiceImpl;
import com.quatre.phoenix.service.DownloadService;
import com.quatre.phoenix.utils.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
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

    // TODO
    // BitmapFactory.decodeStream() needs Instrumented
    @Test
    public void testStoreAllPicturesOnInternalMemory() throws IOException {
        final var pictures = downloadService.getAllPicturesFromUrl("https://manhuaplus.com/manga/demon-magic-emperor01/chapter-717/", "img");
        final var path = Paths.get(context.getFilesDir().getPath());
        downloadService.storeAllPicturesOnInternalMemory(pictures, "MagicEmperor", "717", path.toAbsolutePath().toString());
        final var files = FileUtils.getFilesFromFolder(path);
        assertEquals(10, files.size());
    }
}