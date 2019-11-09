package com.example.galleryproject.Model;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LabelCounter {
    private static final int LABEL_CATEGORY_SIZE = 5;

    private static final List<String> PERSON_LABEL =
        Arrays.asList(new String[]{
                    "Smile", "Fun", "Eyelash", "Bangs", "Skin", "Hair",
                    "Selfie", "Cool", "Nail", "Love", "Flesh", "Ear", "Hand",
                    "Model", "Lipstick", "Mouth", "Singer", "Moustache",
                    "Baby", "Team", "Crowd", "Laugh", "Foot", "Musician"
                });
    private static final List<String> SCENERY_LABEL =
            Arrays.asList(new String[]{
                    "Sky", "Building", "River", "Rock", "Lake", "Mountain",
                    "Forest", "Leisure", "Tower", "Jungle", "Sunset", "Beach",
                    "Field", "Road", "Vehicle", "Park", "Gaden", "Prairie",
                    "Bridge", "Skyscraper", "Infrastructure", "Cliff", "Temple",
                    "Monument", "Sand", "Space", "Ruins", "Pier", "Glacier",
                    "Waterfall", "Neon", "Skyline", "Mosque", "Church", "Lighthouse",
                    "Asphalt", "Cathedral", "Aircraft", "Moon", "Sports", "Palace",
                    "Cave", "Statue", "Canyon", "Aviation", "Airliner", "Star",
                    "Stairs", "Brick", "Aerospace engineering"
                });
    private static final List<String> PET_LABEL =
            Arrays.asList(new String[]{ "Pet", "Dog", "Cat", "Fur", "Bird", "Horse",
                    "Insect", "Wool", "Shetland sheepdog", "Safari",
                    "Butterfly" });

    private static final List<String> FOOD_LABEL =
            Arrays.asList(new String[]{
                    "Food", "Cuisine", "Vegetable", "Meal", "Cake", "Coffee",
                    "Fast food", "Fruit", "Bread", "Juice", "Lunch", "Icing",
                    "Cup", "Cookware and bakeware", "Gelato", "Cookie", "Pizza",
                    "Sushi", "Wine", "Alcohol", "Pho", "Cappuccino"
                });

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
        // Descending Order
        Collections.sort(labels, (label1, label2) -> {
            Float c1 = label1.getConfidence();
            Float c2 = label2.getConfidence();

            return c2.compareTo(c1);
        });


        float minConfidence = (float) 0.5;

        // label 갯수 세기
        double[] counters = new double[5];
        for (Label label: labels) {
            double confidence = label.getConfidence();
            if (confidence < minConfidence)
                continue;

            String labelText = label.getText();
            int categoryIdx = categorize(labelText);

            counters[categoryIdx] += confidence;
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
        if (PERSON_LABEL.contains(str)) {
            return 0;

        } else if (FOOD_LABEL.contains(str)) {
            return 1;

        } else if (PET_LABEL.contains(str)) {
            return 2;

        } else if (SCENERY_LABEL.contains(str)) {
            return 3;

        } else {
            return 4;
        }
    }
}
