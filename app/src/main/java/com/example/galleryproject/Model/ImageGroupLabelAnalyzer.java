package com.example.galleryproject.Model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ImageGroupLabelAnalyzer {

    List<ImageGroup> all;
    List<List<LabelGroup>> allLabelGroups;

    List<double[]> X;

    private List<int[]> labelCounters;
    private int allImageCount;
    private static int[] labelPriority;

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
        labelPriority = priority;
    }

    public static boolean hasLabelPriority() {
        return labelPriority != null;
    }


    private void initialize() {
        this.labelCounters = new ArrayList<>();

        // 라벨 데이터 장수 분석
        for (List<LabelGroup> labelGroups: allLabelGroups) {
            this.labelCounters.add(
                    LabelCounter.getCount(labelGroups));
        }

        // 전체 이미지 장수
        this.allImageCount = 0;
        for (ImageGroup group: this.all) {
            this.allImageCount += group.getImages()
                    .size();
        }

        return;
    }

    public void analyze() {
        initialize();

        X = new ArrayList<>();

        for (int groupIdx = 0; groupIdx < this.all.size(); groupIdx++) {
            ImageGroup group = this.all.get(groupIdx);

            // 편차 계산
            double bias = getBias(group);

            // 가장 많은 레이블의 갯수 (대표 레이블)
            int[] labelCounter = this.labelCounters.get(groupIdx);
            int maxLabelCount = getMaxLabelCount(labelCounter);

            /**
             * 타겟 유사 그룹 내 대표 레이블 사진 장수 / 전체 유사 그룹 사진 장수
             */
            double averageInSimGroup = (double) maxLabelCount / (double) group.getImages().size();


            /**
             * 타겟 유사 그룹 내 레이블 사진 장수 / 시간 그룹 내 전체 레이블 사진 장수
             */
            double averageInTimeGroup = (double) maxLabelCount / (double) this.allImageCount;

            /**
             * Label 우선순위에 따른 cost function 값
             */
            double labelScore = (double) this.getLabelScore(labelCounter);

            X.add(new double[]{ bias, averageInSimGroup, averageInTimeGroup, labelScore });
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

    private double getBias(ImageGroup target) {
        /**
         * | 유사그룹 평균 사진 장수 - 타겟 유사 그룹 사진 장수 |
         */

        // 유사 그룹 평균 사진 장수
        int allImageCount = 0;
        for (ImageGroup group: this.all) {
             allImageCount += group.getImages().size();
        }

        double imageCountAverage = (double) allImageCount / (double) this.all.size();

        // 타겟 유사 그룹 사진 장수
        double targetImageCount = target.getImages().size();

        // 결과 (양수)
        double result = imageCountAverage - targetImageCount;
        result = result > 0 ? result : result * (-1);

        return result;
    }

    private void analyzeLabel(int groupIdx) {
        int[] targetCounter = this.labelCounters.get(groupIdx);

        this.getMaxLabelCount(targetCounter);
        this.getLabelScore(targetCounter);

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

    private int getLabelScore(int[] counter) {
        int result = 0;
        for (int lIdx = 0; lIdx < counter.length; lIdx++) {
            int label = labelPriority[lIdx];

            // 점수: (5 - label Index)
            result += counter[label] * (5 - lIdx);
        }

        return result;
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
