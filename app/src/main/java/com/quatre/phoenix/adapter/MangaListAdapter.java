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
import com.quatre.phoenix.activity.MangaActivity;
import com.quatre.phoenix.entity.Manga;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MangaListAdapter extends RecyclerView.Adapter<MangaListAdapter.TextViewHolder> {

    private final Context context;
    private final List<Manga> mangas;

    @NonNull
    @Override
    public TextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text, parent, false);
        return new TextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TextViewHolder holder, int position) {
        final var manga = mangas.get(position);
        holder.textView.setText(manga.getName());

        // Set click listener
        holder.textView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MangaActivity.class);
            intent.putExtra("manga", manga);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mangas.size();
    }

    public static class TextViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public TextViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textItem);
        }
    }
}


