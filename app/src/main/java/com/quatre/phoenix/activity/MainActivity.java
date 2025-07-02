package com.quatre.phoenix.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.quatre.phoenix.R;
import com.quatre.phoenix.impl.DownloadServiceImpl;
import com.quatre.phoenix.service.DownloadService;
import com.quatre.phoenix.utils.FileUtils;
import org.jsoup.nodes.Element;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainActivity extends AppCompatActivity {

    private DownloadService downloadService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.info("!!!!!!!!!!!!!!!!!!! PHOENIX IV STARTING !!!!!!!!!!!!!!!!!!!");

        // init services
        downloadService = new DownloadServiceImpl();

        // configure generic UI
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // configure reader specific UI
        final var downloadButton = findViewById(R.id.downloadButton);
        final var displayButton = findViewById(R.id.displayButton);
        displayButton.setEnabled(false); // Initially disabled
        downloadButton.setOnClickListener(v -> manageImages(displayButton));
        displayButton.setOnClickListener(v -> openReadActivity());

    }

    private void openReadActivity() {
        final var intent = new Intent(MainActivity.this, ReaderActivity.class);
        intent.putExtra("mangaName", "MagicEmperor");
        intent.putExtra("chapterName", "717");
        startActivity(intent);
    }

    private void manageImages(View displayButton) {
        final Retryer<List<Element>> loadingRetryer = FileUtils.getRetryer();
        final Retryer<List<File>> storingRetryer = FileUtils.getRetryer();
        final List<Element> pictures;

        // load all pictures info
        try {
            pictures = loadingRetryer.call(() -> downloadService.getAllPicturesFromUrl("https://manhuaplus.com/manga/demon-magic-emperor01/chapter-717/", "img").get());
            Toast.makeText(MainActivity.this, "Loading complete!", Toast.LENGTH_SHORT).show();
        } catch (ExecutionException | RetryException e) {
            Toast.makeText(MainActivity.this, "Loading failed!", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }

        // store all pictures internally
        runOnUiThread(() -> {
            try {
                storingRetryer.call(() -> downloadService.storeAllPicturesOnInternalMemory(pictures, "MagicEmperor", "717", getFilesDir().getAbsolutePath()).get());
                displayButton.setEnabled(true); // Enable the display button when download is complete
                Toast.makeText(MainActivity.this, "Download complete!", Toast.LENGTH_SHORT).show();
            } catch (ExecutionException | RetryException e) {
                displayButton.setEnabled(false); // Enable the display button when download is complete
                Toast.makeText(MainActivity.this, "Download failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        downloadService.onDestroy();
    }
}
