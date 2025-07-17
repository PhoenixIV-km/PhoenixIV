package com.quatre.phoenix;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.quatre.phoenix.entity.Chapter;
import com.quatre.phoenix.impl.ChapterServiceImpl;
import com.quatre.phoenix.service.ChapterService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

@RunWith(AndroidJUnit4.class)
public class ChapterServiceInstrumentedTest {

    private final String ID_MANGA = "42";

    private Context context;
    private ChapterService chapterService;

    @Before
    public void init() throws ExecutionException, InterruptedException {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        chapterService = new ChapterServiceImpl();
        chapterService.deleteAllChaptersFromManga(ID_MANGA).get();
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        assertEquals("com.quatre.phoenix", context.getPackageName());
    }

    // asynchronous unit tests needs Instrumented
    @Test
    public void testCRUDChapter() throws InterruptedException, ExecutionException {
        // test empty
        var chapters = chapterService.getAllChapters().get();
        assertEquals(0, chapters.size());

        // insert chapters and test full
        chapterService.insertAllNewChapters(List.of(
                        new Chapter("something else", "https://manhuaplus.com/manga/demon-magic-emperor01/chapter-726/", "Magic Emperor - Chapter 726"),
                        new Chapter(ID_MANGA, "https://manhuaplus.com/manga/demon-magic-emperor01/chapter-725/", "Magic Emperor - Chapter 725"),
                        new Chapter(ID_MANGA, "https://manhuaplus.com/manga/demon-magic-emperor01/chapter-724/", "Magic Emperor - Chapter 724"))
                )
                .get();
        chapters = chapterService.getAllChapters().get();
        assertEquals(3, chapters.size());
        assertEquals("something else", chapters.get(0).getIdManga());
        assertEquals("Magic Emperor - Chapter 725", chapters.get(1).getName());
        assertEquals("https://manhuaplus.com/manga/demon-magic-emperor01/chapter-724/", chapters.get(2).getUrl());

        // delete some and test remaining
        chapterService.deleteAllChaptersFromManga(ID_MANGA).get();
        chapters = chapterService.getAllChapters().get();
        assertEquals(1, chapters.size());
        assertEquals("something else", chapters.get(0).getIdManga());
    }

    @Test
    public void testUpdateRead() throws ExecutionException, InterruptedException {
        // insert chapters
        chapterService.insertAllNewChapters(List.of(
                        new Chapter(ID_MANGA, "https://manhuaplus.com/manga/demon-magic-emperor01/chapter-725/", "Magic Emperor - Chapter 725"),
                        new Chapter(ID_MANGA, "https://manhuaplus.com/manga/demon-magic-emperor01/chapter-724/", "Magic Emperor - Chapter 724"))
                )
                .get();

        // test all unread by default
        var chapters = chapterService.getAllChapters().get();
        assertEquals(2, chapters.stream().filter(Predicate.not(Chapter::isRead)).count());

        // mark one as read and test again
        chapterService.markChaptersAsRead(chapters.get(0).getId(), Boolean.TRUE).get();

        // test 1 unread 1 read
        chapters = chapterService.getAllChapters().get();
        assertEquals(1, chapters.stream().filter(Chapter::isRead).count());
        assertEquals(1, chapters.stream().filter(Predicate.not(Chapter::isRead)).count());

        // mark all as read
        chapterService.markAllChaptersAsRead(ID_MANGA, Boolean.TRUE).get();

        // test all read
        chapters = chapterService.getAllChapters().get();
        assertEquals(2, chapters.stream().filter(Chapter::isRead).count());
    }

    @Test
    public void testUpdateDownloaded() throws ExecutionException, InterruptedException {
        // insert chapters
        chapterService.insertAllNewChapters(List.of(
                        new Chapter(ID_MANGA, "https://manhuaplus.com/manga/demon-magic-emperor01/chapter-725/", "Magic Emperor - Chapter 725"),
                        new Chapter(ID_MANGA, "https://manhuaplus.com/manga/demon-magic-emperor01/chapter-724/", "Magic Emperor - Chapter 724"))
                )
                .get();

        // test all not downloaded by default
        var chapters = chapterService.getAllChapters().get();
        assertEquals(2, chapters.stream().filter(Predicate.not(Chapter::isDownloaded)).count());

        // mark one as downloaded
        chapterService.markChapterAsDownloaded(chapters.get(0).getId(), Boolean.TRUE).get();

        // test 1 downloaded 1 not downloaded
        chapters = chapterService.getAllChapters().get();
        assertEquals(1, chapters.stream().filter(Chapter::isDownloaded).count());
        assertEquals(1, chapters.stream().filter(Predicate.not(Chapter::isDownloaded)).count());
    }
}