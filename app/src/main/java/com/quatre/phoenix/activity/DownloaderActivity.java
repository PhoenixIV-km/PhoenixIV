package com.quatre.phoenix.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.quatre.phoenix.R;
import com.quatre.phoenix.entity.Chapter;
import com.quatre.phoenix.entity.Manga;
import com.quatre.phoenix.impl.DownloadServiceImpl;
import com.quatre.phoenix.service.DownloadService;
import com.quatre.phoenix.utils.SnackbarMaker;
import org.jsoup.nodes.Element;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DownloaderActivity extends MenuActivity {

    private DownloadService downloadService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // init services
        downloadService = new DownloadServiceImpl();

        // init view
        super.onCreate(savedInstanceState);

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
        displayButton.setEnabled(false); // Initially disabled
        downloadButton.setOnClickListener(v -> manageImages(displayButton, manga, chapter));
        displayButton.setOnClickListener(v -> openReadActivity(manga.getName(), chapter));
    }

    private void manageImages(final View displayButton, final Manga manga, final Chapter chapter) {
        final List<Element> pictures;
        final var rootView = findViewById(android.R.id.content);

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

    @Override
    void refreshMangaList() {
        // unused
    }
}
