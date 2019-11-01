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

import com.example.galleryproject.PhotoGroupActivity;
import com.example.galleryproject.R;

import java.util.ArrayList;

import xyz.sangcomz.stickytimelineview.TimeLineRecyclerView;

public class TimeLineRecyclerViewAdapter extends TimeLineRecyclerView.Adapter<TimeLineRecyclerViewAdapter.TimeLineRecyclerViewHolder> {
    private ArrayList<PhotoGroup> photoGroups;
    private Context context;

    public TimeLineRecyclerViewAdapter(Context context, ArrayList<PhotoGroup> photoGroups) {
        this.context = context;
        this.photoGroups = photoGroups;
    }

    public class TimeLineRecyclerViewHolder extends RecyclerView.ViewHolder {
        public TextView timeLineMemo;
        protected RecyclerView timeLine_Image_RecyclerView;

        public TimeLineRecyclerViewHolder(View itemView) {
            super(itemView);
            timeLineMemo = (TextView) itemView.findViewById(R.id.timeLineMemo);
            timeLine_Image_RecyclerView = (RecyclerView) itemView.findViewById(R.id.timeLine_Image_RecyclerView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        PhotoGroup photoGroup = photoGroups.get(position);
                        Intent intent = new Intent(context, PhotoGroupActivity.class);
                        intent.putExtra("PhotoGroup", photoGroup);
                        context.startActivity(intent);
                    }
                }
            });
        }
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
        ArrayList<String> paths = photoGroups.get(position).getFilePaths();

        TimeLineHorizontalAdapter adapter = new TimeLineHorizontalAdapter(context, paths);
        holder.timeLine_Image_RecyclerView.setHasFixedSize(true);
        holder.timeLine_Image_RecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.timeLine_Image_RecyclerView.setAdapter(adapter);

        //TODO decorator 없애야함 margin이나 padding으로 대체할 것
        TimeLineHorizontalDecorator decorator = new TimeLineHorizontalDecorator(10);
        holder.timeLine_Image_RecyclerView.addItemDecoration(decorator);

//        holder.timeLineMemo.setText(photoGroups.get(position).getMemo());
        setReadMore(holder.timeLineMemo, photoGroups.get(position).getMemo(), 2);
    }

    @Override
    public int getItemCount() {
        return photoGroups.size();
    }

    public static void setReadMore(final TextView view, final String text, final int maxLine) {
        final Context context = view.getContext();
        final String expanedText = " ... 더보기";

        if (view.getTag() != null && view.getTag().equals(text)) { //Tag로 전값 의 text를 비교하여똑같으면 실행하지 않음.
            return;
        }
        view.setTag(text); //Tag에 text 저장
        view.setText(text); // setText를 미리 하셔야  getLineCount()를 호출가능
        view.post(new Runnable() { //getLineCount()는 UI 백그라운드에서만 가져올수 있음
            @Override
            public void run() {
                if (view.getLineCount() >= maxLine) { //Line Count가 설정한 MaxLine의 값보다 크다면 처리시작

                    int lineEndIndex = view.getLayout().getLineVisibleEnd(maxLine - 1); //Max Line 까지의 text length

                    String[] split = text.split("\n"); //text를 자름
                    int splitLength = 0;

                    String lessText = "";
                    for (String item : split) {
                        splitLength += item.length() + 1;
                        if (splitLength >= lineEndIndex) { //마지막 줄일때!
                            if (item.length() >= expanedText.length()) {
                                lessText += item.substring(0, item.length() - (expanedText.length())) + expanedText;
                            } else {
                                lessText += item + expanedText;
                            }
                            break; //종료
                        }
                        lessText += item + "\n";
                    }
                    SpannableString spannableString = new SpannableString(lessText);
                    spannableString.setSpan(new ClickableSpan() {//클릭이벤트
                        @Override
                        public void onClick(View v) {
                            view.setText(text);
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) { //컬러 처리
                            ds.setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                        }
                    }, spannableString.length() - expanedText.length(), spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    view.setText(spannableString);
                    view.setMovementMethod(LinkMovementMethod.getInstance());
                }
            }
        });


    }

}
