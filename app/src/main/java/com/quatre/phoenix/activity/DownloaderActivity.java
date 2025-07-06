package com.quatre.phoenix.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.quatre.phoenix.R;
import com.quatre.phoenix.entity.Chapter;
import com.quatre.phoenix.impl.DownloadServiceImpl;
import com.quatre.phoenix.service.DownloadService;
import com.quatre.phoenix.utils.FileUtils;
import org.jsoup.nodes.Element;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DownloaderActivity extends AppCompatActivity {

    private DownloadService downloadService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // init services
        downloadService = new DownloadServiceImpl();

        // init view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloader);

        // get extra
        final String mangaName = getIntent().getStringExtra("mangaName");
        final Chapter chapter = (Chapter) getIntent().getSerializableExtra("chapter");

        final var downloadButton = findViewById(R.id.downloadButton);
        final var displayButton = findViewById(R.id.displayButton);
        displayButton.setEnabled(false); // Initially disabled
        downloadButton.setOnClickListener(v -> manageImages(displayButton, mangaName, chapter));
        displayButton.setOnClickListener(v -> {
            assert chapter != null;
            openReadActivity(mangaName, chapter);
        });
    }

    private void manageImages(final View displayButton, final String mangaName, final Chapter chapter) {
        final Retryer<List<Element>> loadingRetryer = FileUtils.getRetryer();
        final Retryer<List<File>> storingRetryer = FileUtils.getRetryer();
        final List<Element> pictures;

        // load all pictures info
        try {
            pictures = loadingRetryer.call(() -> downloadService.getAllElementsFromUrl(chapter.getUrl(), "img").get());
            Toast.makeText(DownloaderActivity.this, "Loading complete!", Toast.LENGTH_SHORT).show();
        } catch (ExecutionException | RetryException e) {
            Toast.makeText(DownloaderActivity.this, "Loading failed!", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }

        // store all pictures internally
        runOnUiThread(() -> {
            try {
                storingRetryer.call(() -> downloadService.storeAllPicturesOnInternalMemory(pictures, mangaName, chapter.getName(), getFilesDir().getAbsolutePath()).get());
                displayButton.setEnabled(true); // Enable the display button when download is complete
                Toast.makeText(DownloaderActivity.this, "Download complete!", Toast.LENGTH_SHORT).show();
            } catch (ExecutionException | RetryException e) {
                displayButton.setEnabled(false); // Enable the display button when download is complete
                Toast.makeText(DownloaderActivity.this, "Download failed!", Toast.LENGTH_SHORT).show();
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
