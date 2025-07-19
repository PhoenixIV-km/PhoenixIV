package com.quatre.phoenix.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.quatre.phoenix.PhoenixIVApplication;
import com.quatre.phoenix.R;
import com.quatre.phoenix.entity.Chapter;
import com.quatre.phoenix.entity.Manga;
import com.quatre.phoenix.impl.ChapterServiceImpl;
import com.quatre.phoenix.impl.DownloadServiceImpl;
import com.quatre.phoenix.service.ChapterService;
import com.quatre.phoenix.service.DownloadService;
import com.quatre.phoenix.utils.SnackbarMaker;
import org.jsoup.nodes.Element;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DownloaderActivity extends MenuActivity {

    private DownloadService downloadService;
    private ChapterService chapterService;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // init services
        downloadService = new DownloadServiceImpl();
        chapterService = new ChapterServiceImpl(PhoenixIVApplication.getChapterDao());

        // init view
        super.onCreate(savedInstanceState);
        rootView = findViewById(android.R.id.content);

        // init menu and put current activity into menu content
        loadContentLayout(R.layout.activity_downloader);

        // get extra
        final Manga manga = (Manga) getIntent().getSerializableExtra("manga");
        final Chapter chapter = (Chapter) getIntent().getSerializableExtra("chapter");
        assert chapter != null;
        assert manga != null;

        // init buttons
        final var downloadButton = findViewById(R.id.downloadButton);
        final var displayButton = findViewById(R.id.displayButton);
        final var readButton = findViewById(R.id.readButton);
        displayButton.setEnabled(chapter.isDownloaded());
        downloadButton.setOnClickListener(v -> manageImages(displayButton, manga, chapter));
        displayButton.setOnClickListener(v -> openReadActivity(manga.getName(), chapter));
        readButton.setOnClickListener(v -> manageReadChapter(displayButton, manga.getName(), chapter, getFilesDir().getAbsolutePath()));
    }

    private void manageImages(final View displayButton, final Manga manga, final Chapter chapter) {
        final List<Element> pictures;

        // load all pictures info
        try {
            pictures = downloadService.getAllElementsFromUrl(chapter.getUrl(), "img").get();
            SnackbarMaker.showCustomSnackbar(rootView, "Loading complete!", Boolean.TRUE);
        } catch (ExecutionException | InterruptedException e) {
            SnackbarMaker.showCustomSnackbar(rootView, "Download failed!", Boolean.FALSE);
            return;
        }

        // store all pictures internally
        runOnUiThread(() -> {
            try {
                downloadService.storeAllPicturesOnInternalMemory(pictures, manga, chapter.getName(), getFilesDir().getAbsolutePath()).get();
                chapter.setDownloaded(Boolean.TRUE);
                chapterService.update(chapter).get();
                displayButton.setEnabled(true);
                SnackbarMaker.showCustomSnackbar(rootView, "Download complete!", Boolean.TRUE);
            } catch (ExecutionException | InterruptedException e) {
                displayButton.setEnabled(false);
                SnackbarMaker.showCustomSnackbar(rootView, "Download failed!", Boolean.FALSE);
            }
        });
    }

    private void openReadActivity(final String mangaName, final Chapter chapter) {
        final var intent = new Intent(DownloaderActivity.this, ReaderActivity.class);
        intent.putExtra("mangaName", mangaName);
        intent.putExtra("chapterName", chapter.getName());
        startActivity(intent);
    }

    private void manageReadChapter(final View displayButton, final String mangaName, final Chapter chapter, String contextPath) {
        try {
            downloadService.deleteAllPicturesOnInternalMemory(mangaName, chapter.getName(), contextPath).get();
        } catch (RuntimeException | ExecutionException | InterruptedException e) {
            SnackbarMaker.showCustomSnackbar(rootView, "Chapter pictures deletion failed!", Boolean.FALSE);
        }
        chapter.setRead(Boolean.TRUE);
        chapter.setDownloaded(Boolean.FALSE);
        try {
            chapterService.update(chapter).get();
        } catch (ExecutionException | InterruptedException e) {
            SnackbarMaker.showCustomSnackbar(rootView, "Updating chapter failed!", Boolean.FALSE);
        }
        displayButton.setEnabled(false);
        SnackbarMaker.showCustomSnackbar(rootView, "Chapter read!", Boolean.TRUE);
    }

    @Override
    void refreshMangaList() {
        // unused
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        downloadService.onDestroy();
        chapterService.onDestroy();
    }
}
