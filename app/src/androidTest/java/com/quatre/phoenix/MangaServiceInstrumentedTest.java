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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@RunWith(AndroidJUnit4.class)
public class MangaServiceInstrumentedTest extends AbstractRoomInstrumentedTest {

    public static final String URL = "https://manhuaplus.com/manga/demon-magic-emperor01/";
    public static final String CSS_QUERY = "ul > li.wp-manga-chapter > a";
    public static final String FILE_NAME = "loadMock.csv";

    private Context context;
    private MangaService mangaService;

    @Before
    public void init() throws ExecutionException, InterruptedException {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        mangaService = new MangaServiceImpl(mangaDao);
        mangaService.deleteAllMangas().get();
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
    public void testAddManga() throws ExecutionException, InterruptedException {
        // test empty
        var mangas = mangaService.getAllMangas().get();
        assertEquals(0, mangas.size());
        // add 1 and test not empty
        final var m1 = new Manga("url", "name", "css");
        mangaService.addManga(m1).get();
        mangas = mangaService.getAllMangas().get();
        assertEquals(1, mangas.size());
        assertEquals("name", mangas.get(0).getName());
    }

    @Test
    public void testAddAllMangas() throws ExecutionException, InterruptedException {
        final var m1 = new Manga("url", "name", "css");
        final var m2 = new Manga("url2", "name2", "css2");
        mangaService.addAllMangas(List.of(m1, m2)).get();
        final var mangas = mangaService.getAllMangas().get();
        assertEquals(2, mangas.size());
        assertEquals("name", mangas.get(0).getName());
    }

    @Test
    public void testDeleteManga() throws ExecutionException, InterruptedException {
        // test empty
        var mangas = mangaService.getAllMangas().get();
        assertEquals(0, mangas.size());

        // add 1 and test not empty
        final var m1 = new Manga("url", "name", "css");
        mangaService.addManga(m1).get();
        mangas = mangaService.getAllMangas().get();
        assertEquals(1, mangas.size());
        assertEquals("name", mangas.get(0).getName());

        // delete and test empty
        mangaService.deleteManga(mangas.get(0)).get();
        mangas = mangaService.getAllMangas().get();
        assertEquals(0, mangas.size());
    }

    @Test
    public void testDeleteAllMangas() throws ExecutionException, InterruptedException {
        // add 2 and test not empty
        final var m1 = new Manga("url", "name", "css");
        final var m2 = new Manga("url2", "name2", "css2");
        mangaService.addAllMangas(List.of(m1, m2)).get();
        var mangas = mangaService.getAllMangas().get();
        assertEquals(2, mangas.size());
        assertEquals("name", mangas.get(0).getName());

        // delete and test empty
        mangaService.deleteAllMangas().get();
        mangas = mangaService.getAllMangas().get();
        assertEquals(0, mangas.size());
    }

    @Test
    public void loadAllMangasFromCsv() throws IOException, ExecutionException, InterruptedException {
        try (var inputStream = context.getAssets().open(FILE_NAME)) {
            final var file = copyAssetToFile(inputStream);
            mangaService.loadAllMangasFromCsv(file).get();
        }
        var mangas = mangaService.getAllMangas().get();
        assertEquals(3, mangas.size());
        assertEquals("Magic Emperor", mangas.get(0).getName());
        assertEquals("https://manhuaplus.com/manga/tales-of-demons-and-gods01/", mangas.get(1).getUrl());
        assertEquals("ul#itemList > li > a", mangas.get(2).getCssQuery());
    }

    private File copyAssetToFile(final InputStream inputStream) throws IOException {
        File outFile = new File(context.getCacheDir(), FILE_NAME);
        try (FileOutputStream outputStream = new FileOutputStream(outFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }
        return outFile;
    }
}