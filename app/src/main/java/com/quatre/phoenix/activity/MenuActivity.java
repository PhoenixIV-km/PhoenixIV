package com.quatre.phoenix.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.FrameLayout;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.quatre.phoenix.R;
import com.quatre.phoenix.impl.MangaServiceImpl;
import com.quatre.phoenix.service.MangaService;
import com.quatre.phoenix.utils.SnackbarMaker;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class MenuActivity extends AppCompatActivity {

    public static final String CSV_FOLDER = "PhoenixIV";
    public static final String CSV_FILE = "load.csv";

    private MangaService mangaService;
    private View rootView;
    private File csvFolder;
    private File csvFile;

    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // init services
        mangaService = new MangaServiceImpl();

        // init files
        csvFolder = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), CSV_FOLDER);
        csvFile = new File(csvFolder, CSV_FILE);

        // configure UI
        super.onCreate(savedInstanceState);
        rootView = findViewById(android.R.id.content);
        setContentView(R.layout.activity_menu);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, LibraryActivity.class));
            } else if (id == R.id.delete) {
                showDeleteLibraryDialog();
            } else if (id == R.id.load_csv) {
                showLoadCsvDialog();
            } else if (id == R.id.csv_path) {
                copyCsvPathToClipboard();
            } else if (id == R.id.csv_open) {
                openCsv();
            } else if (id == R.id.init_csv) {
                initCsvFromCode();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    // children should now call this method to include their view in the content_frame, instead of their own setContentView()
    protected void loadContentLayout(@LayoutRes int layoutId) {
        FrameLayout contentFrame = findViewById(R.id.content_frame);
        if (contentFrame == null) {
            throw new IllegalStateException("content_frame not found. Did you call setContentView()? or call it 2 times with wrap and content?");
        }
        View content = getLayoutInflater().inflate(layoutId, contentFrame, false);
        contentFrame.addView(content);
    }

    // Only children that need a refresh will implement that
    void onLoadCsv() {
        try {
            mangaService.loadAllMangasFromCsv(csvFile).get();
            SnackbarMaker.showCustomSnackbar(rootView, "CSV loaded!", Boolean.TRUE);
            refreshMangaList();
        } catch (ExecutionException | InterruptedException e) {
            SnackbarMaker.showCustomSnackbar(rootView, "CSV loading failed!", Boolean.FALSE);
        } catch (IOException e) {
            SnackbarMaker.showCustomSnackbar(rootView, "CSV not found!", Boolean.FALSE);
        }
    }

    // Only children that need a refresh will implement that
    void onDeleteLibrary() {
        try {
            mangaService.deleteAllMangas().get();
            SnackbarMaker.showCustomSnackbar(rootView, "Library deleted!", Boolean.TRUE);
            refreshMangaList();
        } catch (ExecutionException | InterruptedException e) {
            SnackbarMaker.showCustomSnackbar(rootView, "Library deleting failed!", Boolean.FALSE);
        }
    }

    private void showLoadCsvDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Load CSV")
                .setPositiveButton("Load", (dialog, which) -> onLoadCsv())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteLibraryDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete library")
                .setPositiveButton("Delete", (dialog, which) -> onDeleteLibrary())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void copyCsvPathToClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("csv_path", csvFile.getAbsolutePath());
        clipboard.setPrimaryClip(clip);
        SnackbarMaker.showCustomSnackbar(rootView, "CSV path copied to clipboard!", Boolean.TRUE);
    }

    private void openCsv() {
        final var context = this;
        Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", csvFile);

        final var intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, "text/csv");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            SnackbarMaker.showCustomSnackbar(rootView, "No app found to open CSV file", Boolean.FALSE);
        }
    }

    private void initCsvFromCode() {
        if (!csvFolder.exists() && !csvFolder.mkdirs()) {
            SnackbarMaker.showCustomSnackbar(rootView, "Failed to create directory: " + csvFolder.getAbsolutePath(), Boolean.FALSE);
            return;
        }

        if (csvFile.exists() && !csvFile.delete()) {
            SnackbarMaker.showCustomSnackbar(rootView, "Failed to delete csv: " + csvFile.getAbsolutePath(), Boolean.FALSE);
            return;
        }
        try {
            if (!csvFile.createNewFile()) {
                SnackbarMaker.showCustomSnackbar(rootView, "Failed to create csv: " + csvFile.getAbsolutePath(), Boolean.FALSE);
                return;
            }
            try (FileWriter writer = new FileWriter(csvFile)) {
                writer.write("https://manhuaplus.com/manga/demon-magic-emperor01/; Magic Emperor; ul > li.wp-manga-chapter > a\n");
                writer.write("https://manhuaplus.com/manga/tales-of-demons-and-gods01/; Tales of demons and gods; ul > li.wp-manga-chapter > a\n");
                writer.write("https://w9.solomax-level.com/; Solo Max-Level Newbie; ul#itemList > li > a\n");
            }
        } catch (IOException e) {
            SnackbarMaker.showCustomSnackbar(rootView, "Failed to load csv: " + csvFile.getAbsolutePath(), Boolean.FALSE);
            return;
        }
        SnackbarMaker.showCustomSnackbar(rootView, "CSV loaded!", Boolean.TRUE);
    }

    abstract void refreshMangaList();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mangaService.onDestroy();
    }
}

