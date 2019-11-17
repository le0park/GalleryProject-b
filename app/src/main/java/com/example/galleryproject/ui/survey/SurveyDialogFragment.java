package com.example.galleryproject.ui.survey;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryproject.MainActivity;
import com.example.galleryproject.Model.Category;
import com.example.galleryproject.R;

import java.util.ArrayList;
import java.util.List;

public class SurveyDialogFragment extends DialogFragment implements SurveyAdapter.StartDragListener{
    private TextView survey_textView;
    private ImageButton survey_complete_button;
    private RecyclerView survey_recyclerView;

    private SurveyAdapter adapter;
    private ItemTouchHelper mItemTouchHelper;

    private ArrayList<String> categories;

    private SurveyClickListener listener;

    private static ArrayList<Integer> OBJECT_CATEGORIES;

    public SurveyDialogFragment() {
        OBJECT_CATEGORIES = new ArrayList<>();
        OBJECT_CATEGORIES.add(Category.PERSON);
        OBJECT_CATEGORIES.add(Category.FOOD);
        OBJECT_CATEGORIES.add(Category.PET);
        OBJECT_CATEGORIES.add(Category.SCENERY);
    }

    public static SurveyDialogFragment getInstance() {
        SurveyDialogFragment surveyDialogFragment = new SurveyDialogFragment();
        return surveyDialogFragment;
    }

    public void setSurveyClickListener(SurveyClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.create().getWindow().setLayout(200,400);
        View view = getActivity().getLayoutInflater().inflate(R.layout.survey_dialog, null);
        builder.setView(view);

        survey_textView = view.findViewById(R.id.survey_textView);
        survey_complete_button = view.findViewById(R.id.survey_complete_button);

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        survey_complete_button.setOnClickListener((v) -> {
            listener.OnSurveyClick(categories);
            this.dismiss();
        });

        survey_recyclerView = view.findViewById(R.id.survey_recyclerView);
        survey_recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        categories = new ArrayList<>();
        for (Integer category: OBJECT_CATEGORIES) {
            categories.add(Category.getName(category));
        }

        adapter = new SurveyAdapter(categories, this);

        ItemTouchHelper.Callback mCallback = new SurveyItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(mCallback);
        mItemTouchHelper.attachToRecyclerView(survey_recyclerView);

        survey_recyclerView.setAdapter(adapter);

        return dialog;
    }

    @Override
    public void requestDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    public List<String> getObjectPriority(){
        return categories;
    }
}
