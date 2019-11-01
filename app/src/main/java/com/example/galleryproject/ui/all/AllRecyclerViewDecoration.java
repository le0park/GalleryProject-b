package com.example.galleryproject.ui.all;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AllRecyclerViewDecoration extends RecyclerView.ItemDecoration{
    private final int divHeight;

    public AllRecyclerViewDecoration(int divHeight) {
        this.divHeight = divHeight;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.top = divHeight;
        outRect.bottom = divHeight;
        outRect.right = divHeight;
        outRect.left = divHeight;
    }
}
