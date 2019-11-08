package com.example.galleryproject.ui.survey;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryproject.MainActivity;
import com.example.galleryproject.R;

import java.util.ArrayList;

public class SurveyDialogFragment extends DialogFragment implements SurveyAdapter.StartDragListener{
    private TextView survey_textView;
    private Button survey_complete_button;
    private RecyclerView survey_recyclerView;

    private SurveyAdapter adapter;
    private ItemTouchHelper mItemTouchHelper;

    private ArrayList<String> categories;

    public SurveyDialogFragment() {}

    public static SurveyDialogFragment getInstance() {
        SurveyDialogFragment surveyDialogFragment = new SurveyDialogFragment();
        return surveyDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.survey_dialog, null);

        builder.setView(view);

        survey_textView = view.findViewById(R.id.survey_textView);
        survey_complete_button = view.findViewById(R.id.survey_complete_button);

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        survey_complete_button.setOnClickListener((v) -> {
//            MainActivity activity = (MainActivity)getActivity();
//            String text = "";
//
//            for(String t : categories)
//                text += t + " ";
//
//            Log.e("CATEGORIZE_FRAGMENT", text);
            ((MainActivity) getActivity()).setObjectPriority(categories);
            this.dismiss();
        });

        survey_recyclerView = view.findViewById(R.id.survey_recyclerView);
        survey_recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false)) ;

        categories = new ArrayList<>();
        categories.add("사람");
        categories.add("음식");
        categories.add("반려동물");
        categories.add("풍경");
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
}
