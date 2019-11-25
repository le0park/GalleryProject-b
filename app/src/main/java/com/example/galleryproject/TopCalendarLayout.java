package com.example.galleryproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.media.Image;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TopCalendarLayout extends LinearLayout {
    private TextView yearTextView;
    private TextView monthTextView;
    private ImageButton showCalendarButton;
    private boolean isExpanded;
    private OnExpandListener listener;



    public TopCalendarLayout(Context context) {
        super(context);
        init(context);
    }

    public TopCalendarLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.top_calendar, this, true);

        yearTextView = (TextView) findViewById(R.id.yearTextView);
        monthTextView = (TextView) findViewById(R.id.monthTextView);
        showCalendarButton = (ImageButton) findViewById(R.id.showCalendarButton);
        showCalendarButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onExpand();
            }
        });

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
        String month = monthFormat.format(currentTime);
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        String year = yearFormat.format(currentTime);

        setYearTextView(year);
        setMonthTextView(month);

        isExpanded = false;
    }

    public void buttonChange(){
        if (isExpanded) {
            showCalendarButton.setImageResource(R.drawable.icons8_expand_arrow_50);
            isExpanded = false;
        } else {
            showCalendarButton.setImageResource(R.drawable.icons8_collapse_arrow_50);
            isExpanded = true;
        }
    }

    public String getYearText(){
        return yearTextView.getText().toString();
    }

    public void setYearTextView(String year){
        yearTextView.setText(year + "년");
    }

    public void setMonthTextView(String month){
        monthTextView.setText(month + "월");
    }

    public void setOnExpandListener(OnExpandListener listener){
        this.listener = listener;
    }
}
