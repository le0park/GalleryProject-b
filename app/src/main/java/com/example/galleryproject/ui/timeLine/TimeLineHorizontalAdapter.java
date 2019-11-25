package com.example.galleryproject.ui.timeLine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.galleryproject.Model.Image;
import com.example.galleryproject.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TimeLineHorizontalAdapter extends RecyclerView.Adapter<TimeLineHorizontalAdapter.TimeLineHorizontalViewHolder> {

    private List<Image> images;
    private Context context;

    public TimeLineHorizontalAdapter(Context context, List<Image> images){
        this.context = context;
        this.images = images;
    }

    public class TimeLineHorizontalViewHolder extends RecyclerView.ViewHolder{
        protected ImageView container;

        public TimeLineHorizontalViewHolder(View view) {
            super(view);
            container = view.findViewById(R.id.timeLine_solo_ImageView);
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
        holder.container.setDuplicateParentStateEnabled(true);
        Glide.with(context)
                .load(images.get(position).getFile())
                .into(holder.container);
    }


    @Override
    public int getItemCount() {
        return images.size();
    }
}
