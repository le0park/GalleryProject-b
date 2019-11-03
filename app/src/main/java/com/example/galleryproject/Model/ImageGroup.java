package com.example.galleryproject.Model;

import android.os.Parcelable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class ImageGroup implements Parcelable {

    protected List<Image> images;
    protected String memo;

    public ImageGroup() {
        this(new ArrayList<>(), "");
    }

    public ImageGroup(List<Image> images) {
        this(images, "");
    }

    public ImageGroup(List<Image> images, String memo){
        this.images = images;
        this.memo = memo;
    }

    public void addImage(Image image) {
        images.add(image);
    }

    public List<String> getFilePaths() {
        List<String> paths = new ArrayList<>();
        for (Image image: images) {
            paths.add(image.getFile()
                           .toPath()
                           .toString());
        }

        return paths;
    }

    public List<Image> getImages() {
        return this.images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public String getMemo() {
        return memo;
    }
    public void setMemo(String memo) {
        this.memo = memo;
    }

    public LocalDateTime getDate() {
        if (this.images.size() > 0) {
            return this.images.get(0).getCreationTime();
        } else {
            return LocalDateTime.now();
        }
    }


    /**
     * Geocode Location은 모델로 가지고 있지 않고, 필요할 때 UI Thread에서 호출해서 이용할 수 있게.
     * (latitude, longitude) 가 있기 때문에 호출할 때 어렵지 않다
     */

//    public String getLocation() { return location; }
//    public void setLocation(Context context){
//        //TODO make getLocation from photos longitude, langitude
//        double latitude;
//        double longitude;
//
//        latitude = images.get(0).getLatitude();
//        longitude = images.get(0).getLongitude();
//        for(UnitImage image: images){
//            if(latitude != 0.0 && longitude != 0.0)
//                break;
//            else{
//                latitude = image.getLatitude();
//                longitude = image.getLongitude();
//            }
//        }
//
//        if(latitude != 0.0 && longitude != 0.0) {
//            Geocoder geocoder;
//            geocoder = new Geocoder(context, Locale.getDefault());
//
//            List<Address> addresses = null;
//            try {
//                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//            } catch (IOException e) {
//                Log.e("SetLocation : ", "IO 오류");
//                e.printStackTrace();
//
//                addresses = new ArrayList<>();
//            }
//
//            if (addresses.size() > 0) {
//                String state = addresses.get(0).getAdminArea();
//                this.location = state;
//            }
//
//        } else {
//            Log.e("SetLocation : ", "Location is Null");
//            this.location = null;
//        }
//    }
}

