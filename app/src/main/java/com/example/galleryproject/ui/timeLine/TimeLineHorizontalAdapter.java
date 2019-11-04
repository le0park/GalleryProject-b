package com.example.galleryproject.ui.timeLine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.galleryproject.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TimeLineHorizontalAdapter extends RecyclerView.Adapter<TimeLineHorizontalAdapter.TimeLineHorizontalViewHolder> {

    private List<String> paths;
    private Context context;

    public TimeLineHorizontalAdapter(Context context, List<String> paths){
        this.context = context;
        this.paths = paths;
    }

    public class TimeLineHorizontalViewHolder extends RecyclerView.ViewHolder{
        protected ImageView timeLine_solo_ImageView;

        public TimeLineHorizontalViewHolder(View view) {
            super(view);
            timeLine_solo_ImageView = view.findViewById(R.id.timeLine_solo_ImageView);
        }
    }

    @NonNull
    @Override
    public TimeLineHorizontalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.timeline_solo_image, null);

        return new TimeLineHorizontalAdapter.TimeLineHorizontalViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeLineHorizontalViewHolder holder, int position) {
        Glide.with(context)
                .load(new File(paths.get(position)))
                .into(holder.timeLine_solo_ImageView);
    }

    @Override
    public int getItemCount() {
        return paths.size();
    }
}
