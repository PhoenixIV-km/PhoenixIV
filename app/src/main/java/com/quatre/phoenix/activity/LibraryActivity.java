package com.quatre.phoenix.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.quatre.phoenix.PhoenixIVApplication;
import com.quatre.phoenix.R;
import com.quatre.phoenix.adapter.MangaListAdapter;
import com.quatre.phoenix.entity.Manga;
import com.quatre.phoenix.impl.MangaServiceImpl;
import com.quatre.phoenix.service.MangaService;
import com.quatre.phoenix.utils.SnackbarMaker;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LibraryActivity extends MenuActivity {

    private MangaService mangaService;
    private View rootView;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // init services
        mangaService = new MangaServiceImpl(PhoenixIVApplication.getMangaDao());

        // init view
        super.onCreate(savedInstanceState);
        rootView = findViewById(android.R.id.content);

        // init menu and put current activity into menu content
        loadContentLayout(R.layout.activity_library);

        // init manga list
        recyclerView = findViewById(R.id.libraryView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // init add button
        FloatingActionButton fab = findViewById(R.id.fab_add_manga);
        fab.setOnClickListener(view -> showAddMangaDialog());

        // generate manga list
        refreshMangaList();
    }

    private void showAddMangaDialog() {
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_manga, null);

        new AlertDialog.Builder(this)
                .setTitle("Add Manga")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    EditText urlInput = dialogView.findViewById(R.id.input_url);
                    EditText nameInput = dialogView.findViewById(R.id.input_name);
                    EditText cssInput = dialogView.findViewById(R.id.input_css_query);

                    String url = urlInput.getText().toString().trim();
                    String name = nameInput.getText().toString().trim();
                    String css = cssInput.getText().toString().trim();

                    if (!url.isEmpty() && !name.isEmpty() && !css.isEmpty()) {
                        try {
                            mangaService.addManga(new Manga(url, name, css)).get();
                            SnackbarMaker.showCustomSnackbar(rootView, "Manga added!", Boolean.TRUE);
                            refreshMangaList();
                        } catch (ExecutionException | InterruptedException e) {
                            SnackbarMaker.showCustomSnackbar(rootView, "Manga adding failed!", Boolean.FALSE);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    void refreshMangaList() {
        final List<Manga> mangaList;

        try {
            mangaList = mangaService.getAllMangas().get();
        } catch (ExecutionException | InterruptedException e) {
            SnackbarMaker.showCustomSnackbar(rootView, "Loading library failed!", Boolean.FALSE);
            return;
        }
        // TODO REMOVE
        if (mangaList.isEmpty()) {
            mangaList.add(new Manga("https://manhuaplus.com/manga/demon-magic-emperor01/", "Magic Emperor", "ul > li.wp-manga-chapter > a"));
            mangaList.add(new Manga("https://manhuaplus.com/manga/tales-of-demons-and-gods01/", "Tales of demons and gods", "ul > li.wp-manga-chapter > a"));
            mangaList.add(new Manga("https://www.toongod.org/webtoon/the-beginning-after-the-end-manhwa-a00cbc/", "The Beginning After The End", "ul > li.wp-manga-chapter > a"));
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
