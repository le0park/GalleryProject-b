package com.example.galleryproject.ui.timeLine;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.galleryproject.SimplePhoto;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PhotoGroup implements Parcelable {
    private ArrayList<String> filePaths;
    private ArrayList<SimplePhoto> photos;
    private String memo;
    private String date;
    private String location;

    private Context context;

    public PhotoGroup(ArrayList<String> filePaths, String memo, Context context) {
        this.filePaths = filePaths;

        photos = new ArrayList<SimplePhoto>();
        for(String file : filePaths){
            photos.add(new SimplePhoto(file));
        }

        setDate();

        this.context = context;
        setLocation(context);

        this.memo = memo;
    }

    public PhotoGroup(){}
    protected PhotoGroup(Parcel in) {
        this();
        readFromParcel(in);
    }
    public PhotoGroup(ArrayList<String> _filePaths, ArrayList<SimplePhoto> _photos, String _memo, String _date, String _location){
        this.filePaths = _filePaths;
        this.photos = _photos;
        this.memo = _memo;
        this.date = _date;
        this.location = _location;
        this.context = null;
    }


    public ArrayList<String> getFilePaths() {
        return filePaths;
    }
    public void setFilePaths(ArrayList<String> filePaths) {
        this.filePaths = filePaths;
    }

    public String getMemo() {
        return memo;
    }
    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getDate(){ return date; }
    public void setDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
        SimpleDateFormat convertDate = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date d = simpleDateFormat.parse(photos.get(0).getDate());
            this.date = convertDate.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getLocation(){ return location; }
    public void setLocation(Context context){
        //TODO make getLocation from photos longitude, langitude
        try {
            String latitude;
            String longitude;

            latitude = photos.get(0).getLatitude();
            longitude = photos.get(0).getLongitude();
            for(SimplePhoto photo : photos){
                if(latitude != null && longitude != null)
                    break;
                else{
                    latitude = photo.getLatitude();
                    longitude = photo.getLongitude();
                }
            }

            if(latitude != null && longitude != null) {
                GeoDegree gd = new GeoDegree(photos.get(0));

                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(context, Locale.getDefault());

                addresses = geocoder.getFromLocation(gd.getLatitude(), gd.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

//            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//            String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
//            String country = addresses.get(0).getCountryName();
//            String postalCode = addresses.get(0).getPostalCode();
//            String knownName = addresses.get(0).getFeatureName();

                this.location = state;
            }else{
                Log.e("SetLocation : ", "Location is Null");
                this.location = "위치정보 없음";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringList(filePaths);
        parcel.writeList(photos);
        parcel.writeString(memo);
        parcel.writeString(date);
        parcel.writeString(location);
    }

    public void readFromParcel(Parcel in){
        filePaths = in.createStringArrayList();
        photos = in.readArrayList(null);
        memo = in.readString();
        date = in.readString();
        location = in.readString();
        context = null;
    }

    public static final Creator<PhotoGroup> CREATOR = new Creator<PhotoGroup>() {
        @Override
        public PhotoGroup createFromParcel(Parcel in) {
            return new PhotoGroup(in);
        }

        @Override
        public PhotoGroup[] newArray(int size) {
            return new PhotoGroup[size];
        }
    };
}
