package com.example.galleryproject;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.util.Log;

import com.example.galleryproject.Model.Image;

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

public class DeepLearningModel {
    private Activity activity;

    public DeepLearningModel(Activity activity) {
        this.activity = activity;
    }

    public Interpreter getTfliteInterpreter(String modelPath) {
        try {
            return new Interpreter(loadModelFile(activity, modelPath));
        } catch (Exception e) {
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

    public float[][][] parseModelInput(List<double[]> x) {
        float[][][] input = new float[x.size()][4][1];
        for (int i = 0; i < x.size(); i++) {
            for (int j = 0; j < 4; j++) {
                input[i][j][0] = (float) x.get(i)[j];
            }
        }
        return input;
    }


    public List<Image> getRepImages(List<Image> images, float[][] priority) {
        // priority[i][0];
        List<Image> repImages = new ArrayList<>();
        Map<Float, Image> hash = new HashMap<>();
        Map<Float, Integer> countHash = new HashMap<>();

        int priorityInx = 0;

        for (Image image : images) {
            hash.put(priority[priorityInx][0], image);

            if (countHash.containsKey(priority[priorityInx][0])) {
                countHash.put(priority[priorityInx][0], countHash.get(priority[priorityInx][0]) + 1);
            } else {
                countHash.put(priority[priorityInx][0], 0);
            }

            priorityInx++;
        }

        List<Float> priorities = new ArrayList<>(hash.keySet());
        Collections.sort(priorities, Collections.reverseOrder());


        if (hash.size() == 1) {
            int repCount = 0;
            for (Image image : images) {
                repImages.add(image);
                repCount++;
                if (repCount == 3)
                    break;
            }
            return repImages;

        } else if (hash.size() == 2) {
            for (float p : priorities) {
                repImages.add(hash.get(p));
            }
            repImages.add(images.get(0));
            return repImages;
//            if (images.size() <= 2) {
//                return repImages;
//            } else {
//                if (countHash.get(priorities.get(0)) >= 2) {
//                    for (int inx = 0; inx < images.size(); inx++) {
//                        if (priority[inx][0] == priorities.get(0)) {
//                            repImages.add(images.get(inx));
//                            return repImages;
//                        }
//                    }
//                } else {
//                    for (int inx = 0; inx < images.size(); inx++) {
//                        if (priority[inx][0] == priorities.get(1)) {
//                            repImages.add(images.get(inx));
//                            return repImages;
//                        }
//                    }
//                }
//            }
        } else {
            int repCount = 0;
            for (float p : priorities) {
                repImages.add(hash.get(p));
                repCount++;
                if (repCount == 3)
                    break;
            }
        }

//        for (float p : priorities) {
//            repImages.add(hash.get(p));
//        }

        return repImages;
    }
}
