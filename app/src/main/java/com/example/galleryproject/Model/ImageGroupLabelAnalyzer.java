package com.example.galleryproject.Model;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageGroupLabelAnalyzer {

    List<ImageGroup> all;
    List<List<LabelGroup>> allLabelGroups;

    private static int[] labelPriority;
    private List<int[]> labelCounters;
    private int allImageCount;
    private List<double[]> X;

    private List<Map<Integer, double[]>> inputsByCategory;

    private boolean analyzed = false;

    public ImageGroupLabelAnalyzer() { }

    public void setGroups(List<ImageGroup> all) {
        this.all = all;

        this.analyzed = false;
    }

    public void setLabelGroups(List<List<LabelGroup>> allLabelGroups) {
        this.allLabelGroups = allLabelGroups;
        this.analyzed = false;
    }

    public static void setLabelPriority(int[] priority) {
        Log.d("ImageGroupLabelAnalyzer", "priority: " + priority.toString());
        labelPriority = new int[5];

        for (int idx = 0; idx < 5; idx++) {
            labelPriority[priority[idx]] = idx;
        }
    }

    public static boolean hasLabelPriority() {
        return labelPriority != null;
    }


    private void initialize() {
        this.labelCounters = new ArrayList<>();

        // 그룹 내 전체 이미지 라벨 그룹당 데이터 장수 카운팅
        for (List<LabelGroup> labelGroups: allLabelGroups) {
            this.labelCounters.add(
                    LabelCounter.getCount(labelGroups));
        }

        // 전체 이미지 장수
        this.allImageCount = 0;
        for (ImageGroup group: this.all) {
            this.allImageCount += group
                    .getImages()
                    .size();
        }


        this.inputsByCategory = new ArrayList<>();
        for (List<LabelGroup> group: allLabelGroups) {
            inputsByCategory.add(new HashMap());
        }

        return;
    }

    public void analyze() {
        initialize();

        X = new ArrayList<>();

        int[] totalCountByLabel = new int[5];
        for (int[] labelCounter: labelCounters) {
            totalCountByLabel[Category.PERSON] += labelCounter[Category.PERSON];
            totalCountByLabel[Category.FOOD] += labelCounter[Category.FOOD];
            totalCountByLabel[Category.PET] += labelCounter[Category.PET];
            totalCountByLabel[Category.SCENERY] += labelCounter[Category.SCENERY];
            totalCountByLabel[Category.ETC] += labelCounter[Category.ETC];
        }


        for (int groupIdx = 0; groupIdx < this.all.size(); groupIdx++) {
            ImageGroup group = this.all.get(groupIdx);

            // 편차 계산
            double bias = getBias(group);

            // 카테고리별 사진 장수 (대표 레이블)
            int[] labelCounter = this.labelCounters.get(groupIdx);
            for (int cIdx = 0; cIdx < totalCountByLabel.length; cIdx++) {
                // label이 존재하지 않으면 생략
                if (labelCounter[cIdx] == 0) {
                    continue;
                }

                /**
                 * 유사 그룹 내 대표 레이블 사진 장수 / 전체 그룹 사진 장수
                 */
                double averageInCategory = (double) labelCounter[cIdx] / (double) totalCountByLabel[cIdx];


                /**
                 * 유사 그룹 내 대표 레이블 사진 장수 / 유사 그룹 사진 장수
                 */
                double averageInGroup = (double) labelCounter[cIdx] / (double) group.getImages().size();


                /**
                 * Label 우선순위에 따른 cost function 값
                 */
                double labelScore = (double) 5 - labelPriority[cIdx];
                inputsByCategory.get(groupIdx);

                double[] x = new double[]{ bias, averageInCategory, averageInGroup, labelScore };
                X.add(x);

//                Log.e("ANALYZER", String.format("X [%d]: %f, %f, %f, %f", cIdx, x[0], x[1], x[2], x[3]));
                inputsByCategory.get(groupIdx)
                                .put(cIdx, x);
            }
        }

        this.analyzed = true;
    }

    public List<double[]> getX() {
        if (this.analyzed) {
             return this.X;
        }

        Log.e("IMAGE_GROUP_LABEL_ANALYZER", "not analyzed");
        return null;
    }

    public List<Map<Integer, double[]>> getInputs() {
        return this.inputsByCategory;
    }

    private double getBias(ImageGroup target) {
        /**
         * | 유사그룹 평균 사진 장수 - 타겟 유사 그룹 사진 장수 |
         */

        double imageCountAverage = (double) this.allImageCount / (double) this.all.size();

        // 타겟 유사 그룹 사진 장수
        double targetImageCount = target.getImages().size();

        // 결과
        double result = imageCountAverage - targetImageCount;

        return result;
    }

    private void analyzeLabel(int groupIdx) {
        int[] targetCounter = this.labelCounters.get(groupIdx);

        this.getMaxLabelCount(targetCounter);

    }

    private int getMaxLabelCount(int[] counter) {
        // 타겟 유사 그룹 내 대표 레이블 사진 장수
        int maxIdx = 0;
        for (int i = 0; i < counter.length; i++) {
            if (counter[maxIdx] < counter[i]) {
                maxIdx = i;
            }
        }

        return counter[maxIdx];
    }

    @Override
    public String toString() {
        if (X == null) {
            return "";
        }


        StringBuffer sb = new StringBuffer();
        sb.append("{ \n");
        for (double[] element: X) {
            sb.append("  { ");
            for (double ex: element) {
                sb.append(ex);
                sb.append(", ");
            }
            sb.append("}, \n");
        }
        sb.append("}");

        return sb.toString();
    }

}
