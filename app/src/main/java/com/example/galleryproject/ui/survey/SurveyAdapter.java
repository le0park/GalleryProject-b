package com.example.galleryproject.ui.survey;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryproject.R;

import java.util.ArrayList;
import java.util.Collections;

class SurveyAdapter extends RecyclerView.Adapter<SurveyAdapter.ViewHolder>
    implements  SurveyItemTouchHelperCallback.ItemTouchHelperContract{

    private ArrayList<String> categories = null;
    private final StartDragListener dragListener;

    public interface StartDragListener{
        void requestDrag(RecyclerView.ViewHolder viewHolder);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView survey_item_textView;
        public Button survey_item_button;
        View rowView;
        ViewHolder(View itemView) {
            super(itemView) ;
            // 뷰 객체에 대한 참조. (hold strong reference)
            rowView = itemView;
            survey_item_textView = itemView.findViewById(R.id.survey_item_textView);
            survey_item_button = itemView.findViewById(R.id.survey_item_button);
        }
    }

    public SurveyAdapter(ArrayList<String> list, StartDragListener listener){
        categories = list;
        this.dragListener = listener;
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(categories, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(categories, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(ViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onRowClear(ViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.WHITE);
    }

    @NonNull
    @Override
    public SurveyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.survey_item, parent, false);
        SurveyAdapter.ViewHolder vh = new SurveyAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull SurveyAdapter.ViewHolder holder, int position) {
        String text = categories.get(position);
        holder.survey_item_textView.setText(text);

        holder.survey_item_button.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    dragListener.requestDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
