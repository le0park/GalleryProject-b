package com.example.galleryproject;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BottomCalendarLayout extends LinearLayout {
    private RecyclerView monthRecyclerView;
    private RecyclerView.Adapter adapter;
    private LinearLayout bottom_LinearLayout;
    private boolean isExpanded;

    private ImageButton bottomCalendar_yearLeftButton;
    private ImageButton bottomCalendar_yearRightButton;
    private TextView bottom_YearTextView;

    private Animation slidingDownAnim;
    private Animation slidingUpAnim;

    private OnCalendarClickListener listener;

    public BottomCalendarLayout(Context context) {
        super(context);
        init(context);
    }

    public BottomCalendarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.bottom_calendar, this, true);
        bottom_LinearLayout = (LinearLayout) findViewById(R.id.bottom_LinearLayout);

        monthRecyclerView = (RecyclerView) findViewById(R.id.monthRecyclerView);
        monthRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));

        ArrayList<String> months = new ArrayList<String>();
        for(int i=1;i<10;i++)
            months.add(" " + i);
        months.add("10");
        months.add("11");
        months.add("12");
        adapter = new Adapter(months);
        monthRecyclerView.setAdapter(adapter);

        bottom_LinearLayout.setVisibility(GONE);
        isExpanded = false;

        slidingDownAnim = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_calendar_down_flow);
        slidingUpAnim = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_calendar_up_flow);

        SlidingCalendarAnimation animListener = new SlidingCalendarAnimation();
        slidingDownAnim.setAnimationListener(animListener);
        slidingUpAnim.setAnimationListener(animListener);

        bottomCalendar_yearLeftButton = (ImageButton) findViewById(R.id.bottomCalendar_yearLeftButton);
        bottomCalendar_yearLeftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String rep_year = bottom_YearTextView.getText().toString();
                int rep_year_num = Integer.parseInt(rep_year);
                setBottom_YearTextView(String.valueOf(--rep_year_num));
            }
        });
        bottomCalendar_yearRightButton = (ImageButton) findViewById(R.id.bottomCalendar_yearRightButton);
        bottomCalendar_yearRightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String rep_year = bottom_YearTextView.getText().toString();
                int rep_year_num = Integer.parseInt(rep_year);

                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
                int cur_year = Integer.parseInt(yearFormat.format(currentTime));

                if(!(rep_year_num >= cur_year))
                    setBottom_YearTextView(String.valueOf(rep_year_num+1));
                else
                    Toast.makeText(getContext(), "지금은 " + cur_year +"년 입니다.", Toast.LENGTH_LONG).show();
            }
        });

        bottom_YearTextView = (TextView) findViewById(R.id.bottom_YearTextView);

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
        String month = monthFormat.format(currentTime);
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        String year = yearFormat.format(currentTime);

        setBottom_YearTextView(year);
    }

    public void setOnCalendarClickListener(OnCalendarClickListener listener){
        this.listener = listener;
    }

    public void setBottom_YearTextView(String year){
        bottom_YearTextView.setText(year);
    }

    public void setVisibility(){
        Log.e("VISIBILITY", "CHANGE" + isExpanded);
        if(isExpanded) {
//            bottom_LinearLayout.setVisibility(GONE);
//            isExpanded = false;

            bottom_LinearLayout.startAnimation(slidingUpAnim);
        }
        else {
//            bottom_LinearLayout.setVisibility(VISIBLE);
//            isExpanded = true;
            bottom_LinearLayout.setVisibility(VISIBLE);
            bottom_LinearLayout.startAnimation(slidingDownAnim);

        }
    }

    private class SlidingCalendarAnimation implements Animation.AnimationListener{
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if(isExpanded) {
                bottom_LinearLayout.setVisibility(GONE);
                isExpanded = false;
            }
            else {
                isExpanded = true;
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{
        ArrayList<String> months;

        public Adapter(ArrayList<String> months) {
            this.months = months;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView calenderItem = null;

            ViewHolder(View itemView) {
                super(itemView) ;
                calenderItem = (TextView) itemView.findViewById(R.id.calendarItem);
                calenderItem.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.OnCalendarClick(bottom_YearTextView.getText().toString(),
                                calenderItem.getText().toString());
                    }
                });
            }
        }

        @NonNull
        @Override
        public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.calender_item, parent, false);

            // set the view's size, margins, paddings and layout parameters

            Adapter.ViewHolder vh = new Adapter.ViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
            String month = months.get(position);
            holder.calenderItem.setText(month);
        }

        @Override
        public int getItemCount() {
            return months.size();
        }
    }

}
