package com.quatre.phoenix.activity;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quatre.phoenix.R;
import com.quatre.phoenix.entity.Chapter;
import com.quatre.phoenix.entity.Manga;
import com.quatre.phoenix.impl.MangaServiceImpl;
import com.quatre.phoenix.service.MangaService;
import com.quatre.phoenix.utils.ChapterListAdapter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class MangaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // init services
        MangaService mangaService = new MangaServiceImpl();

        // init view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga);
        RecyclerView recyclerView = findViewById(R.id.mangaView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // get chapter
        final var manga = (Manga) getIntent().getSerializableExtra("manga");

        // get and display urls
        List<Chapter> chaptersUrls;
        try {
            assert manga != null;
            final var chapters = mangaService.getAllElementsFromUrl(manga.getUrl(), manga.getCssQuery()).get();
            chaptersUrls = chapters.stream().map(c -> new Chapter(manga.getId(), Objects.requireNonNull(Objects.requireNonNull(c.attribute("href")).getValue()), c.text())).collect(Collectors.toList());
            Toast.makeText(MangaActivity.this, "Loading complete!", Toast.LENGTH_SHORT).show();
        } catch (ExecutionException | InterruptedException e) {
            Toast.makeText(MangaActivity.this, "Loading failed!", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }
        ChapterListAdapter adapter = new ChapterListAdapter(this, manga.getName(), chaptersUrls);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
