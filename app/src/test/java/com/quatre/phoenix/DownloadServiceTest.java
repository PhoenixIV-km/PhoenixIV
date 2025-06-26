package com.quatre.phoenix;

import static org.junit.Assert.assertEquals;

import com.quatre.phoenix.impl.DownloadServiceImpl;
import com.quatre.phoenix.service.DownloadService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.io.IOException;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(JUnit4.class)
public class DownloadServiceTest {

    private DownloadService downloadService;

    @Before
    public void init() {
        downloadService = new DownloadServiceImpl();
    }

    @After
    public void teardown() {
    }

    @Test
    public void testGetAllPicturesFromUrl() throws IOException {
        final var pictures = downloadService.getAllPicturesFromUrl("https://manhuaplus.com/manga/demon-magic-emperor01/chapter-717/", "img");
        assertEquals(10, pictures.size());
        final var last = pictures.last();
        assert last != null;
        assertEquals("img", last.tag().getName());
        final var src = last.attribute("src");
        assert src != null;
        assertEquals("//pixel.quantserve.com/pixel/p-PZmZQZSQ-y1yB.gif", src.getValue());
    }
}