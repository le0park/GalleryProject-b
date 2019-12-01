package com.example.galleryproject.ui.all;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.galleryproject.Model.Image;
import com.example.galleryproject.Model.ImageCollection;
import com.example.galleryproject.Model.ImageGroup;
import com.example.galleryproject.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AllRecyclerViewAdapter extends RecyclerView.Adapter<AllRecyclerViewAdapter.ViewHolder>{
//    private ArrayList<MyData> dataset = null;
    private ArrayList<String> filePaths = null;

    private List<Image> images;

    private FragmentActivity activity;
    private PhotoClickListener listner;


//    public AllRecyclerViewAdapter(FragmentActivity activity, ArrayList<String> filePaths) {
//        this.activity = activity;
//        this.filePaths = filePaths;
//    }

    public AllRecyclerViewAdapter(FragmentActivity activity, List<Image> images){
        this.activity = activity;
        this.images = images;
    }

    public void setPhotoClickListener(PhotoClickListener listner){
        this.listner = listner;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView all_item_imageView;
        public TextView all_item_textView;

        ViewHolder(View itemView) {
            super(itemView) ;
            all_item_imageView = (ImageView)itemView.findViewById(R.id.all_item_imageView);
            //all_item_textView = (TextView)itemView.findViewById(R.id.all_item_textView);
            // 뷰 객체에 대한 참조. (hold strong reference)
        }
    }

    @NonNull
    @Override
    public AllRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.all_item, parent, false);

        // set the view's size, margins, paddings and layout parameters

        final ViewHolder vh = new ViewHolder(v);

        vh.all_item_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listner != null) {
                    listner.onItemClick(vh.getAdapterPosition());
                }
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull AllRecyclerViewAdapter.ViewHolder holder, int position) {

//        Glide.with(holder.itemView.getContext())
//                .load(new File(filePaths.get(position)))
//                .into(holder.all_item_imageView);

        Glide.with(holder.itemView.getContext())
                .load(new File(images.get(position).getFilePath()))
                .into(holder.all_item_imageView);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public int getTimePosition(String year, String month) {
        int position = 0;
        int click_year = Integer.parseInt(year.trim());
        int click_month = Integer.parseInt(month.trim());

        for (Image im : images) {

            int collection_year = im.getCreationTime().getYear();
            int collection_month = im.getCreationTime().getMonth().getValue();

//            Log.e("sort", "y : " + collection_year + ", m : " + collection_month);
            if (click_year > collection_year || (click_year >= collection_year && click_month >= collection_month)) {
//                Log.e("Position : ", "click year : " + click_year + ", click month : "  + click_month);
//                Log.e("Position : ", position + ", find year : " + collection_year + ", find month : " + collection_month);
                return position;
            } else {
                position++;
            }
        }
//        Log.e("can't find position : ", position + " , length : " + imageCollections.size());
        return -1;
    }

    private int setSimpleSize(BitmapFactory.Options options, int requestWidth, int requestHeight){
        int originalWidth = options.outWidth;
        int originalHeight = options.outHeight;

        // 원본 이미지 비율인 1로 초기화
        int size = 1;

        // 해상도가 깨지지 않을만한 요구되는 사이즈까지 2의 배수의 값으로 원본 이미지를 나눈다.
//        while(requestWidth < originalWidth || requestHeight < originalHeight){
//            originalWidth = originalWidth / 2;
//            originalHeight = originalHeight / 2;
//
//            size = size * 2;
//        }
        if(originalHeight > requestHeight || originalWidth > requestWidth){
            if(originalWidth > originalHeight){
                size = Math.round((float)originalHeight / (float)requestHeight);
            }else{
                size = Math.round((float)originalWidth / (float)requestWidth);
            }
        }

        return size;
    }
}
