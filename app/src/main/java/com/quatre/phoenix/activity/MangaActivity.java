package com.quatre.phoenix.activity;

import android.os.Bundle;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quatre.phoenix.PhoenixIVApplication;
import com.quatre.phoenix.R;
import com.quatre.phoenix.adapter.ChapterListAdapter;
import com.quatre.phoenix.entity.Chapter;
import com.quatre.phoenix.entity.Manga;
import com.quatre.phoenix.impl.ChapterServiceImpl;
import com.quatre.phoenix.impl.MangaServiceImpl;
import com.quatre.phoenix.service.ChapterService;
import com.quatre.phoenix.service.MangaService;
import com.quatre.phoenix.utils.SnackbarMaker;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class MangaActivity extends MenuActivity {

    private MangaService mangaService;
    private ChapterService chapterService;
    private View rootView;
    private Manga manga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // init services
        mangaService = new MangaServiceImpl(PhoenixIVApplication.getMangaDao());
        chapterService = new ChapterServiceImpl(PhoenixIVApplication.getChapterDao());

        // init view
        super.onCreate(savedInstanceState);

        // init menu and put current activity into menu content
        loadContentLayout(R.layout.activity_manga);
        rootView = findViewById(android.R.id.content);

        // get manga
        manga = (Manga) getIntent().getSerializableExtra("manga");

        getNewChapters();
    }

    private void getNewChapters() {
        try {
            assert manga != null;
            final var chapterElements = mangaService.getAllElementsFromUrl(manga.getUrl(), manga.getCssQuery()).get();
            final var newChapters = chapterElements.stream().map(c -> new Chapter(manga.getId(), Objects.requireNonNull(Objects.requireNonNull(c.attribute("href")).getValue()), c.text())).collect(Collectors.toList());
            chapterService.insertAllNewChapters(newChapters).get();
            SnackbarMaker.showCustomSnackbar(rootView, "New chapters loaded!", Boolean.TRUE);
        } catch (ExecutionException | InterruptedException e) {
            SnackbarMaker.showCustomSnackbar(rootView, "Loading new chapters failed!", Boolean.FALSE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Chapter> chapters;
        try {
            chapters = chapterService.getAllChapters().get();
            SnackbarMaker.showCustomSnackbar(rootView, "Chapter list loaded!", Boolean.TRUE);
        } catch (ExecutionException | InterruptedException e) {
            SnackbarMaker.showCustomSnackbar(rootView, "Loading chapter list failed!", Boolean.FALSE);
            return;
        }

        // init element
        RecyclerView recyclerView = findViewById(R.id.mangaView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // generate chapter list
        ChapterListAdapter adapter = new ChapterListAdapter(this, manga, chapters);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    void refreshMangaList() {
        // unused
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mangaService.onDestroy();
        chapterService.onDestroy();
    }
}
