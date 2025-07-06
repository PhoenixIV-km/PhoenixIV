package com.quatre.phoenix.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quatre.phoenix.R;
import com.quatre.phoenix.adapter.MangaListAdapter;
import com.quatre.phoenix.entity.Manga;
import com.quatre.phoenix.impl.MangaServiceImpl;
import com.quatre.phoenix.service.MangaService;
import com.quatre.phoenix.utils.SnackbarMaker;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LibraryActivity extends AppCompatActivity {

    private MangaService mangaService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // init services
        mangaService = new MangaServiceImpl();

        // init view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        RecyclerView recyclerView = findViewById(R.id.libraryView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // generate manga list
        final List<Manga> mangaList;
        final var rootView = findViewById(android.R.id.content);

        try {
            mangaList = mangaService.getAllMangas().get();
            SnackbarMaker.showCustomSnackbar(rootView, "Loading library complete!", Boolean.TRUE);
        } catch (ExecutionException | InterruptedException e) {
            SnackbarMaker.showCustomSnackbar(rootView, "Loading library failed!", Boolean.FALSE);
            throw new RuntimeException(e);
        }
        // TODO REMOVE
        if (mangaList.isEmpty()) {
            mangaList.add(new Manga("https://manhuaplus.com/manga/demon-magic-emperor01/", "ul > li.wp-manga-chapter > a", "Magic Emperor"));
        }

        // display manga list
        MangaListAdapter adapter = new MangaListAdapter(this, mangaList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mangaService.onDestroy();
    }
}
