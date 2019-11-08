package com.example.galleryproject.Model;


import java.util.Collections;
import java.util.List;

public class LabelCounter {
    private static final int LABEL_CATEGORY_SIZE = 5;

    /**
     * 이미지 그룹 내의 count
     * @param labelGroups
     * @return
     */
    public static int[] getCount(List<LabelGroup> labelGroups) {
        int[] counter = new int[LABEL_CATEGORY_SIZE];

        // 각 사진의 category 마다 카운팅
        for (LabelGroup group: labelGroups) {
            int category = getCategory(group.getLabels());
            counter[category] += 1;
        }

        return counter;
    }


    /**
     * @param labels
     * @return objects to int
     * 0 : person
     * 1 : food
     * 2 : pet(dog or cat)
     * 3 : scenery
     * 4 : etc
     */
    public static int getCategory(List<Label> labels) {
        // Smile, Fun, Eyelash, Bangs, Skin, Selfie, Hair
        // Food, Cuisine, Vegetable, Meal
        // Pet, Dog, Cat, Fur
        // Sky, Building, River, Rock, Lake, Mountain

        // Descending Order
        Collections.sort(labels, (label1, label2) -> {
            Float c1 = label1.getConfidence();
            Float c2 = label2.getConfidence();

            return c2.compareTo(c1);
        });

        float minConfidence = (float) 0.3;

        // label 갯수 세기
        int[] counters = new int[5];
        for (Label label: labels) {
            if (label.getConfidence() < minConfidence)
                continue;

            String labelText = label.getText();
            int categoryIdx = categorize(labelText);

            counters[categoryIdx] += 1;
        }

        // label 갯수 가장 많은 것 출력
        int maxIdx = 0;
        for (int cIdx = 0; cIdx < 5; cIdx++) {
            if (counters[cIdx] >= counters[maxIdx]) {
                maxIdx = cIdx;
            }
        }

        return maxIdx;
    }

    private static int categorize(String str) {
        if (str.contains("Smile") ||
            str.contains("Eyelash") ||
            str.contains("Bangs") ||
            str.contains("Skin") ||
            str.contains("Fun") ||
            str.contains("Selfie")) {
            return 0;

        } else if (str.contains("Food") ||
                   str.contains("Cuisine") ||
                   str.contains("Vegetable") ||
                   str.contains("Meal")) {
            return 1;

        } else if (str.contains("Pet") ||
                   str.contains("Dog") ||
                   str.contains("Cat") ||
                   str.contains("Fur")) {
            return 2;

        } else if (str.contains("Sky") ||
                   str.contains("Building") ||
                   str.contains("River") ||
                   str.contains("Mountain")) {
            return 3;

        } else {
            return 4;
        }
    }
}
