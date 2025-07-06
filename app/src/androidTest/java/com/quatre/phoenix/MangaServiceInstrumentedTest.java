package com.quatre.phoenix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.quatre.phoenix.entity.Manga;
import com.quatre.phoenix.impl.MangaServiceImpl;
import com.quatre.phoenix.service.MangaService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@RunWith(AndroidJUnit4.class)
public class MangaServiceInstrumentedTest {

    public static final String URL = "https://manhuaplus.com/manga/demon-magic-emperor01/";
    public static final String CSS_QUERY = "ul > li.wp-manga-chapter > a";

    private Context context;
    private MangaService mangaService;

    @Before
    public void init() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        mangaService = new MangaServiceImpl();
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        assertEquals("com.quatre.phoenix", context.getPackageName());
    }

    // asynchronous unit tests needs Instrumented
    @Test
    public void testGetAllChaptersFromUrl() throws InterruptedException, ExecutionException {
        final var chapters = mangaService.getAllElementsFromUrl(URL, CSS_QUERY).get();
        assertTrue(720 < chapters.size());
        final var last = chapters.get(chapters.size() - 1);
        assertEquals("a", last.tag().getName());
        assertEquals("https://manhuaplus.com/manga/demon-magic-emperor01/chapter-1/", Objects.requireNonNull(last.attribute("href")).getValue());
        assertEquals("Chapter 1", last.text());
    }

    @Test
    public void testGetAllMangas() throws ExecutionException, InterruptedException {
        final var m1 = new Manga("url", "css", "name");
        final var m2 = new Manga("url2", "css2", "name2");
        PhoenixIVApplication.getAppDatabase().mangaDao().insertAll(m1, m2);
        final var mangas = mangaService.getAllMangas().get();
        assertEquals(2, mangas.size());
        assertEquals("name", mangas.get(0).getName());
    }
}