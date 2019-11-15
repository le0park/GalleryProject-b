package com.example.galleryproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.galleryproject.Database.AppDatabase;
import com.example.galleryproject.Database.AppExecutors;
import com.example.galleryproject.Database.Entity.DbInputData;
import com.example.galleryproject.Database.Entity.DbLabel;
import com.example.galleryproject.Model.Adapter.DbLabelAdapter;
import com.example.galleryproject.Model.Category;
import com.example.galleryproject.Model.Image;
import com.example.galleryproject.Model.ImageCollection;
import com.example.galleryproject.Model.ImageGroupLabelAnalyzer;
import com.example.galleryproject.Model.Label;
import com.example.galleryproject.Model.LabelCounter;
import com.example.galleryproject.Model.LabelGroup;
import com.example.galleryproject.Model.LabelGroupWithImage;
import com.example.galleryproject.ui.all.AllRecyclerViewDecoration;
import com.example.galleryproject.Model.ImageGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PhotoGroupActivity extends AppCompatActivity {
    private TextView photoGroup_date_textView;
    private TextView photoGroup_Memo_textView;
    private EditText photoGroup_Memo_editText;
    private Button resetButton;
    private Button dbSaveButton;
    private ImageButton photoGroup_backButton;

    private InputMethodManager imm;

    private Adapter adapter;
    private RecyclerView photoGroup_RecyclerView;

    private ImageCollection imageCollection;
    private List<ImageGroup> imageGroups;
    private List<Boolean> selected;

    private AppDatabase mDb;
    private ImageGroupLabelAnalyzer labelAnalyzer = new ImageGroupLabelAnalyzer();
    private List<LabelGroupWithImage> labelGroupsWithImage;
    private List<Map<Integer, double[]>> categoryMaps;

    private List<double[]> selectedInput = new ArrayList<>();
    private List<double[]> deselectedInput = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_group);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Bundle bundle = getIntent().getExtras();
        imageCollection = bundle.getParcelable("ImageCollection");
        imageGroups = imageCollection.getGroups();

        // image list flattening
        List<Image> images = imageGroups.stream()
                            .map(ImageGroup::getImages)
                            .flatMap(List::stream)
                            .collect(Collectors.toList());

        selected = imageGroups.stream()
                            .map(x -> false)
                            .collect(Collectors.toList());

        List<File> files = images.stream()
                                 .map(Image::getFile)
                                 .collect(Collectors.toList());


        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        resetButton = findViewById(R.id.saveButton);
        dbSaveButton = findViewById(R.id.saveButton2);
        photoGroup_backButton = findViewById(R.id.photoGroup_backButton);
        photoGroup_date_textView = findViewById(R.id.photoGroup_date_textView);

        photoGroup_date_textView.setText(imageCollection.getDate().toString());

        resetButton.setOnClickListener((view) -> {
            resetRepresentImage();
        });

        dbSaveButton.setOnClickListener((view) -> {
            AppExecutors.getInstance().diskIO().execute(() -> {
                saveInput();
            });
        });

        photoGroup_backButton.setOnClickListener((view) -> finish());

        photoGroup_RecyclerView = findViewById(R.id.photoGroup_RecyclerView);
        photoGroup_RecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        adapter = new Adapter(images, (image) -> {

            List<Boolean> nowSelected = imageGroups.stream()
                                                    .map(x -> x.getImages().contains(image))
                                                    .collect(Collectors.toList());

            if (categoryMaps == null) {
                Toast.makeText(getApplicationContext(), "데이터가 생성되지 않았습니다! 잠시만 기다려주세요. ", Toast.LENGTH_SHORT)
                     .show();

                return;
            }


            LabelGroup clickedLabelGroup =
                labelGroupsWithImage.stream()
                                    .filter(x -> x.isOfImage(image))
                                    .collect(Collectors.toList()).get(0)
                                    .getLabelGroup();

            int gIdx = 0;
            for (; gIdx < imageGroups.size(); gIdx++) {
                List<Image> gImages = imageGroups.get(gIdx).getImages();

                if (gImages.contains(image)) {
                    break;
                }
            }

            Integer category = LabelCounter.getCategory(clickedLabelGroup.getLabels());
            double[] input = categoryMaps.get(gIdx).get(category);

            selectRepresentImage(input);

            Toast.makeText(getApplicationContext(),"선택되었습니다. ",Toast.LENGTH_SHORT)
                 .show();
        });

        photoGroup_RecyclerView.setAdapter(adapter);
        photoGroup_RecyclerView.addItemDecoration(new AllRecyclerViewDecoration(10));

        mDb = AppDatabase.getInstance(getApplicationContext());
        AppExecutors.getInstance().diskIO().execute(() -> {
            this.labelGroupsWithImage = new ArrayList<>();

            List<List<LabelGroup>> labelGroups = new ArrayList<>();

            AppExecutors.getInstance().mainThread().execute(() -> {
                Toast.makeText(
                        getApplicationContext(),
                        String.format("Group size: %d", imageCollection.getGroups().size()),
                        Toast.LENGTH_LONG)
                     .show();
            });

            for (ImageGroup group: imageCollection.getGroups()) {
                List<LabelGroup> imageLabels = new ArrayList<>();

                List<Image> similarImages = group.getImages();
                for (Image im: similarImages) {
                    int id = (int) mDb.dbImageDao()
                                      .getId(im.getFile());

                    List<DbLabel> dbLabels = mDb.dbMlkitLabelDao().loadAllByImageId(id);
                    List<Label> labels = dbLabels.stream()
                                                 .map(x -> new DbLabelAdapter(x))
                                                 .collect(Collectors.toList());

                    LabelGroup labelGroup = new LabelGroup(labels);
                    imageLabels.add(labelGroup);

                    labelGroupsWithImage.add(new LabelGroupWithImage(labelGroup, im));
                }

                labelGroups.add(imageLabels);
            }



            labelAnalyzer.setLabelGroups(labelGroups);
            labelAnalyzer.setGroups(imageCollection.getGroups());
            labelAnalyzer.analyze();

            categoryMaps = labelAnalyzer.getInputs();
            deselectedInput.addAll(categoryMaps.stream()
                                            .map(x -> x.entrySet())
                                            .flatMap(Set::stream)
                                            .map(Map.Entry::getValue)
                                            .collect(Collectors.toList()));
        });
    }

    public void selectRepresentImage(double[] input) {
        if (deselectedInput.indexOf(input) >= 0) {
            selectedInput.add(input);
            deselectedInput.remove(input);
        }
    }

    public void resetRepresentImage() {
        selectedInput = new ArrayList<>();
        deselectedInput = new ArrayList<>();
        deselectedInput.addAll(categoryMaps.stream()
                .map(x -> x.entrySet())
                .flatMap(Set::stream)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList()));
    }

    public void saveInput() {
        List<DbInputData> selected = new ArrayList<>();

        selected.addAll(selectedInput.stream()
                                    .map(in -> {
                                        DbInputData data = new DbInputData();
                                        data.setJson(String.format("[%f, %f, %f, %f]", in[0], in[1], in[2], in[3]));
                                        data.setSelected(1);

                                        return data;
                                    })
                                    .collect(Collectors.toList()));

        List<DbInputData> deselected = new ArrayList<>();
        deselected.addAll(deselectedInput.stream()
                                        .map(in -> {
                                            DbInputData data = new DbInputData();
                                            data.setJson(String.format("[%f, %f, %f, %f]", in[0], in[1], in[2], in[3]));
                                            data.setSelected(0);

                                            return data;
                                        })
                                        .collect(Collectors.toList()));

        mDb.dbInputDataDao().insertAll(selected.stream().toArray(DbInputData[]::new));
        mDb.dbInputDataDao().insertAll(deselected.stream().toArray(DbInputData[]::new));


        AppExecutors.getInstance().mainThread().execute(() -> {
            Toast.makeText(getApplicationContext(), "데이터가 저장되었습니다. \n뒤로가셔서 공유하기를 눌러주세요! ", Toast.LENGTH_LONG)
                .show();
        });
    }

    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{
        private List<Image> images;
        private OnItemClickListener listener;

        Adapter(List<Image> images, OnItemClickListener listener){
            this.images = images;
            this.listener = listener;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;


            ViewHolder(View itemView) {
                super(itemView) ;
                imageView = itemView.findViewById(R.id.photoGroupItem_ImageView);
            }

            void bind(final Image image, final OnItemClickListener listener) {
                imageView.setOnClickListener((v) -> listener.OnItemClick(image));
            }
        }

        @NonNull
        @Override
        public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.photogroup_item, parent, false);

            return new Adapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
            Glide.with(getApplicationContext())
                    .load(images.get(position).getFile())
                    .into(holder.imageView);

            holder.bind(images.get(position), listener);
        }

        @Override
        public int getItemCount() {
            return images.size();
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(Image image);
    }
}
