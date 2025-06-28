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
import com.quatre.phoenix.R;
import com.quatre.phoenix.impl.DownloadServiceImpl;
import com.quatre.phoenix.service.DownloadService;
import com.quatre.phoenix.utils.PictureCallback;
import org.jsoup.select.Elements;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainActivity extends AppCompatActivity {

    private DownloadService downloadService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.info("!!!!!!!!!!!!!!!!!!! PHOENIX IV STARTING !!!!!!!!!!!!!!!!!!!");

        // init services
        downloadService = new DownloadServiceImpl();

        // configure UI
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // manage UI
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
        try {
            downloadService.getAllPicturesFromUrl("https://manhuaplus.com/manga/demon-magic-emperor01/chapter-717/", "img", new PictureCallback() {
                @Override
                public void onResult(Elements elements) {
                    runOnUiThread(() -> {
                        // Enable the display button when download is complete
                        displayButton.setEnabled(true);
                        Toast.makeText(MainActivity.this, "Download complete!", Toast.LENGTH_SHORT).show();
                        try {
                            downloadService.storeAllPicturesOnInternalMemory(elements, "MagicEmperor", "717", getFilesDir().getAbsolutePath());
                        } catch (IOException e) {
                            // TODO generalize
                            log.error("Failed to store images with ", e);
                            throw new RuntimeException(e);
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    // TODO generalize
                    log.error("Failed to download images with ", e);
                    throw new RuntimeException(e);
                }
            });

        } catch (IOException e) {
            // TODO generalize
            log.error("Failed to download images with ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        downloadService.onDestroy();
    }
}
