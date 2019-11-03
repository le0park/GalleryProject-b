package com.example.galleryproject.Model;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationUtility {
    public static String getLocation(Context context, double latitude, double longtitude) {
        Geocoder geocoder;
        geocoder = new Geocoder(context, Locale.getDefault());

        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latitude, longtitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            Log.e("getLocation: ", "IO 오류");
            e.printStackTrace();

            addresses = new ArrayList<>();
        }

        if (addresses.size() > 0) {
            String state = addresses.get(0).getAdminArea();
            return state;
        } else {
            return null;
        }
    }
}
