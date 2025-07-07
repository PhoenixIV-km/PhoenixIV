package com.quatre.phoenix.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.quatre.phoenix.R;
import com.quatre.phoenix.entity.Chapter;
import com.quatre.phoenix.entity.Manga;
import com.quatre.phoenix.impl.DownloadServiceImpl;
import com.quatre.phoenix.service.DownloadService;
import com.quatre.phoenix.utils.FileUtils;
import com.quatre.phoenix.utils.SnackbarMaker;
import org.jsoup.nodes.Element;
import java.io.File;
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
        // init menu and but current activity into menu content
        loadContentLayout(R.layout.activity_downloader);

        // get extra
        final Manga manga = (Manga) getIntent().getSerializableExtra("manga");
        final Chapter chapter = (Chapter) getIntent().getSerializableExtra("chapter");

        final var downloadButton = findViewById(R.id.downloadButton);
        final var displayButton = findViewById(R.id.displayButton);
        displayButton.setEnabled(false); // Initially disabled
        downloadButton.setOnClickListener(v -> manageImages(displayButton, manga, chapter));
        displayButton.setOnClickListener(v -> {
            assert chapter != null;
            assert manga != null;
            openReadActivity(manga.getName(), chapter);
        });
    }

    private void manageImages(final View displayButton, final Manga manga, final Chapter chapter) {
        final Retryer<List<Element>> loadingRetryer = FileUtils.getRetryer();
        final Retryer<List<File>> storingRetryer = FileUtils.getRetryer();
        final List<Element> pictures;
        final var rootView = findViewById(android.R.id.content);

        // load all pictures info
        try {
            pictures = loadingRetryer.call(() -> downloadService.getAllElementsFromUrl(chapter.getUrl(), "img").get());
            SnackbarMaker.showCustomSnackbar(rootView, "Loading complete!", Boolean.TRUE);
        } catch (ExecutionException | RetryException e) {
            SnackbarMaker.showCustomSnackbar(rootView, "Download failed!", Boolean.FALSE);
            throw new RuntimeException(e);
        }

        // store all pictures internally
        runOnUiThread(() -> {
            try {
                storingRetryer.call(() -> downloadService.storeAllPicturesOnInternalMemory(pictures, manga, chapter.getName(), getFilesDir().getAbsolutePath()).get());
                displayButton.setEnabled(true); // Enable the display button when download is complete
                SnackbarMaker.showCustomSnackbar(rootView, "Download complete!", Boolean.TRUE);
            } catch (ExecutionException | RetryException e) {
                displayButton.setEnabled(false); // Enable the display button when download is complete
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
    protected void onDestroy() {
        super.onDestroy();
        downloadService.onDestroy();
    }
}
