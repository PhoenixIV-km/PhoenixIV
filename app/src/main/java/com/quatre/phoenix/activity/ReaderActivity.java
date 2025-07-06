package com.quatre.phoenix.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.quatre.phoenix.R;
import com.quatre.phoenix.adapter.ImageAdapter;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ReaderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        RecyclerView recyclerView = findViewById(R.id.imageRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get your image files (e.g., from /data/data/.../files/mangaName/chapterName)
        final var mangaName = getIntent().getStringExtra("mangaName");
        final var chapterName = getIntent().getStringExtra("chapterName");
        File imagesDir = new File(getFilesDir(), mangaName + "/" + chapterName);
        File[] images = imagesDir.listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".png"));

        if (images != null) {
            Arrays.sort(images); // Optional: sort by name or number
            List<File> imageFiles = Arrays.asList(images);
            recyclerView.setAdapter(new ImageAdapter(imageFiles));
        }
    }
}
