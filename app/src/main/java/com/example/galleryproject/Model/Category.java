package com.example.galleryproject.Model;

public class Category {
    public static final int
            PERSON  = 0,
            FOOD    = 1,
            PET     = 2,
            SCENERY = 3,
            ETC     = 4;

    public static String getName(int VALUE) {
        switch (VALUE) {
            case PERSON:
                return "사람";
            case FOOD:
                return "음식";
            case PET:
                return "반려동물";
            case SCENERY:
                return "풍경";
            case ETC:
                return "기타";
            default:
                return "잘못된 입력";
        }
    }

    public static int getValue(String name) {
        if(name.equals("사람")) {
            return PERSON;
        }
        if(name.equals("음식")) {
            return FOOD;
        }
        if(name.equals("반려동물")) {
            return PET;
        }
        if(name.equals("풍경")) {
            return SCENERY;
        }
        if(name.equals("기타")) {
            return ETC;
        }

        return -1;
    }
}
