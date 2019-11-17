package com.example.galleryproject;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.util.Log;

import com.example.galleryproject.Model.Image;
import com.example.galleryproject.Model.ImageCollection;
import com.example.galleryproject.Model.ImageGroup;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DeepLearningModel {
    private Activity activity;

    public DeepLearningModel(Activity activity) {
        this.activity = activity;
    }

    public Interpreter getTfliteInterpreter(String modelPath) {
        try {
            return new Interpreter(loadModelFile(activity, modelPath));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 모델을 읽어오는 함수로, 텐서플로 라이트 홈페이지에 있다.
    // MappedByteBuffer 바이트 버퍼를 Interpreter 객체에 전달하면 모델 해석을 할 수 있다.
    private MappedByteBuffer loadModelFile(Activity activity, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public float[][][] parseModelInput(List<double[]> x){
        float[][][] input = new float[x.size()][4][1];
        for (int i = 0; i < x.size(); i++) {
            for (int j = 0; j < 4; j++) {
                input[i][j][0] = (float)x.get(i)[j];
            }
        }
        return input;
    }


    public List<Image> getRepImages(ImageCollection result, float[][] priority) {
        // priority[i][0];
        List<Image> all_imagesInTimeLine = new ArrayList<>();

        List<Image> repImages = new ArrayList<>();
        Map<Float, Image> hash = new HashMap<>();

        int priorityInx = 0;
        for(int imageGroupInx = 0; imageGroupInx < result.getGroups().size(); imageGroupInx++){
            ImageGroup ig = result.getGroups().get(imageGroupInx);
            for(int imageInx = 0; imageInx < ig.getImages().size(); imageInx++){
                Image im = ig.getImages().get(imageInx);
                hash.put(priority[priorityInx][0], im);
                priorityInx++;
                if(priorityInx == priority.length)
                    break;
            }
            if(priorityInx == priority.length)
                break;
        }

        List<Float> priorities = new ArrayList<>(hash.keySet());
        Collections.sort(priorities, Collections.reverseOrder());
        for(float p : priorities)
            Log.e("SORTED_PRIORITY : ", p + " " + hash.get(p));

        return repImages;
    }
}
