package com.quatre.phoenix.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.quatre.phoenix.R;
import com.quatre.phoenix.activity.DownloaderActivity;
import com.quatre.phoenix.entity.Chapter;
import com.quatre.phoenix.entity.Manga;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChapterListAdapter extends RecyclerView.Adapter<ChapterListAdapter.TextViewHolder> {

    private final Context context;
    private final Manga manga;
    private final List<Chapter> chapters;

    @NonNull
    @Override
    public TextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text, parent, false);
        return new TextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TextViewHolder holder, int position) {
        final var chapter = chapters.get(position);
        holder.textView.setText(chapter.getName());

        // Set click listener
        holder.textView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DownloaderActivity.class);
            intent.putExtra("manga", manga);
            intent.putExtra("chapter", chapter);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    public static class TextViewHolder extends RecyclerView.ViewHolder {
        final TextView textView;

        public TextViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textItem);
        }
    }
}


