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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryproject.Model.ImageGroup;
import com.example.galleryproject.PhotoGroupActivity;
import com.example.galleryproject.R;

import java.util.List;

import xyz.sangcomz.stickytimelineview.TimeLineRecyclerView;

public class TimeLineRecyclerViewAdapter extends TimeLineRecyclerView.Adapter<TimeLineRecyclerViewAdapter.TimeLineRecyclerViewHolder> {
    private List<ImageGroup> imageGroups;
    private Context context;

    public TimeLineRecyclerViewAdapter(Context context, List<ImageGroup> imageGroups) {
        this.context = context;
        this.imageGroups = imageGroups;
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
        List<String> paths = imageGroups.get(position).getFilePaths();

        TimeLineHorizontalAdapter adapter = new TimeLineHorizontalAdapter(context, paths);
        holder.imageRecyclerView.setHasFixedSize(true);
        holder.imageRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.imageRecyclerView.setAdapter(adapter);

        setReadMore(holder.memoView, imageGroups.get(position).getMemo(), 2);
    }

    @Override
    public int getItemCount() {
        return imageGroups.size();
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
        public TextView memoView;
        protected RecyclerView imageRecyclerView;

        public TimeLineRecyclerViewHolder(View itemView) {

            super(itemView);
            memoView = itemView.findViewById(R.id.timeLineMemo);
            imageRecyclerView = itemView.findViewById(R.id.timeLine_Image_RecyclerView);


            itemView.setOnClickListener((View view) -> {

                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    ImageGroup imageGroup = imageGroups.get(position);

                    Intent intent = new Intent(context, PhotoGroupActivity.class);
                    intent.putExtra("ImageGroup", imageGroup);
                    context.startActivity(intent);
                }
            });
        }
    }
}
