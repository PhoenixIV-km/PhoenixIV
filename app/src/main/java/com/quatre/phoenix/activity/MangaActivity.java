package com.quatre.phoenix.activity;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quatre.phoenix.R;
import com.quatre.phoenix.adapter.ChapterListAdapter;
import com.quatre.phoenix.entity.Chapter;
import com.quatre.phoenix.entity.Manga;
import com.quatre.phoenix.impl.MangaServiceImpl;
import com.quatre.phoenix.service.MangaService;
import com.quatre.phoenix.utils.SnackbarMaker;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class MangaActivity extends MenuActivity {

    private MangaService mangaService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // init services
        mangaService = new MangaServiceImpl();

        // init view
        super.onCreate(savedInstanceState);

        // init menu and put current activity into menu content
        loadContentLayout(R.layout.activity_manga);

        // init element
        RecyclerView recyclerView = findViewById(R.id.mangaView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // get chapter
        final var manga = (Manga) getIntent().getSerializableExtra("manga");

        // get and display urls
        List<Chapter> chaptersUrls;
        final var rootView = findViewById(android.R.id.content);

        try {
            assert manga != null;
            final var chapters = mangaService.getAllElementsFromUrl(manga.getUrl(), manga.getCssQuery()).get();
            chaptersUrls = chapters.stream().map(c -> new Chapter(manga.getId(), Objects.requireNonNull(Objects.requireNonNull(c.attribute("href")).getValue()), c.text())).collect(Collectors.toList());
            SnackbarMaker.showCustomSnackbar(rootView, "Loading chapter list complete!", Boolean.TRUE);
        } catch (ExecutionException | InterruptedException e) {
            SnackbarMaker.showCustomSnackbar(rootView, "Loading chapter list failed!", Boolean.FALSE);
            return;
        }

        // generate chapter list
        ChapterListAdapter adapter = new ChapterListAdapter(this, manga, chaptersUrls);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    void refreshMangaList() {
        // unused
    }
}
