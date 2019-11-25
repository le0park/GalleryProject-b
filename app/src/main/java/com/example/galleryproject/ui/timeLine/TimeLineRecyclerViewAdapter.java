package com.example.galleryproject.ui.timeLine;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryproject.Model.Image;
import com.example.galleryproject.Model.ImageCollection;
import com.example.galleryproject.PhotoGroupActivity;
import com.example.galleryproject.R;

import java.util.List;

import xyz.sangcomz.stickytimelineview.TimeLineRecyclerView;

public class TimeLineRecyclerViewAdapter extends TimeLineRecyclerView.Adapter<TimeLineRecyclerViewAdapter.TimeLineRecyclerViewHolder> {
    private List<ImageCollection> imageCollections;
    private Context context;
    private int lastPosition = -1;

    public TimeLineRecyclerViewAdapter(Context context, List<ImageCollection> imageCollections) {
        this.context = context;
        this.imageCollections = imageCollections;

    }

    @NonNull
    @Override
    public TimeLineRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.timeline_item, parent, false);
        TimeLineRecyclerViewHolder vh = new TimeLineRecyclerViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull TimeLineRecyclerViewHolder holder, int position) {
        setAnimation(holder.container, position);

        ImageCollection collection = imageCollections.get(position);
        List<Image> images = collection.getRepImages();
        TimeLineHorizontalAdapter adapter = new TimeLineHorizontalAdapter(context, images);
        holder.imageContainer.setHasFixedSize(true);
        holder.imageContainer.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.imageContainer.setAdapter(adapter);
        holder.imageContainer.suppressLayout(true);

        if(holder.imageContainer.getItemDecorationCount() == 0) {
            TimeLineHorizontalDecorator decorator = new TimeLineHorizontalDecorator(10);
            holder.imageContainer.addItemDecoration(decorator);
        }

        setReadMore(holder.memoView, imageCollections.get(position).getMemo(), 2);
    }

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            AnimationSet animations = new AnimationSet(false);
            Animation pushIn = AnimationUtils.loadAnimation(context, R.anim.push_in_from_top);
            animations.addAnimation(pushIn);
            viewToAnimate.startAnimation(animations);

            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return imageCollections.size();
    }

    public static void setReadMore(final TextView view, final String text, final int maxLine) {
        final Context context = view.getContext();
        final String expanedText = " ... 더보기";

        // Tag로 이전 값의 text를 비교하여 똑같으면 실행하지 않음.
        if (view.getTag() != null && view.getTag().equals(text)) {
            return;
        }

        view.setTag(text);

        // setText를 미리 하셔야  getLineCount()를 호출가능
        view.setText(text);

        // getLineCount()는 UI 백그라운드에서만 가져올수 있음
        view.post(() -> {

            // Line Count가 설정한 MaxLine의 값보다 크다면 처리시작
            if (view.getLineCount() >= maxLine) {

                // Max Line 까지의 text length
                int lineEndIndex = view.getLayout().getLineVisibleEnd(maxLine - 1);

                // text를 자름
                String[] split = text.split("\n");
                int splitLength = 0;

                StringBuffer lessText = new StringBuffer();
                for (String item : split) {
                    splitLength += item.length() + 1;

                    // 마지막 줄일때!
                    if (splitLength >= lineEndIndex) {
                        if (item.length() >= expanedText.length()) {
                            lessText.append(item.substring(0, item.length() - (expanedText.length())) + expanedText);
                        } else {
                            lessText.append(item + expanedText);
                        }

                        //종료
                        break;
                    }
                    lessText.append(item + "\n");
                }



                ClickableSpan clickableSpan = new ClickableSpan() {
                    // 클릭 이벤트 리스너
                    @Override
                    public void onClick(View v) {
                        view.setText(text);
                    }

                    // 컬러 처리
                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                    }
                };

                SpannableString spannableString = new SpannableString(lessText);
                spannableString.setSpan(clickableSpan,
                        spannableString.length() - expanedText.length(),
                        spannableString.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                view.setText(spannableString);
                view.setMovementMethod(LinkMovementMethod.getInstance());
            }
        });
    }



    public class TimeLineRecyclerViewHolder extends RecyclerView.ViewHolder {
        protected LinearLayout container;
        protected RecyclerView imageContainer;
        public TextView memoView;

        public TimeLineRecyclerViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.timeline_item_container);
            imageContainer = itemView.findViewById(R.id.timeLine_Image_RecyclerView);
            memoView = itemView.findViewById(R.id.timeLineMemo);

            container.setOnClickListener((View view) -> {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    ImageCollection collection = imageCollections.get(position);

                    Intent intent = new Intent(context, PhotoGroupActivity.class);
                    intent.putExtra("ImageCollection", collection);
                    context.startActivity(intent);
                }
            });
        }
    }
}
