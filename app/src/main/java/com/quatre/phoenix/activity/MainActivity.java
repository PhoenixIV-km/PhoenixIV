package com.quatre.phoenix.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.quatre.phoenix.R;
import com.quatre.phoenix.entity.Manga;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.info("!!!!!!!!!!!!!!!!!!! PHOENIX IV STARTING !!!!!!!!!!!!!!!!!!!");

        // configure generic UI
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        openMangaActivity();
    }

    private void openMangaActivity() {
        final Manga manga = new Manga("https://manhuaplus.com/manga/demon-magic-emperor01/", "ul > li.wp-manga-chapter > a", "Magic Emperor");
        final var intent = new Intent(MainActivity.this, MangaActivity.class);
        intent.putExtra("manga", manga);
        startActivity(intent);
    }
}
