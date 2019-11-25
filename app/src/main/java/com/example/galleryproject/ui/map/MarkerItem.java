package com.example.galleryproject.ui.map;

import com.example.galleryproject.Model.Image;
import com.example.galleryproject.Model.ImageCollection;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.List;

public class MarkerItem implements ClusterItem {
    private LatLng location;
    private ImageCollection collection;

    public MarkerItem(LatLng location, ImageCollection collection){
        this.location = location;
        this.collection = collection;
    }

    public MarkerItem(double latitude, double longitude, ImageCollection collection){
        this(new LatLng(latitude, longitude), collection);
    }

    public double getLatitude() {
        return this.location.latitude;
    }

    public void setLatitude(double latitude) {
        this.location = new LatLng(latitude, getLongitude());
    }

    public double getLongitude() {
        return this.location.longitude;
    }

    public void setLongitude(double longitude) {
        this.location = new LatLng(getLatitude(), longitude);
    }

    public Image getRepImage() {
        if (collection.getRepImages().size() > 0) {
            return this.collection.getRepImages().get(0);
        }

        return null;
    }

    public List<Image> getRepImages() {
        return this.collection.getRepImages();
    }

    @Override
    public LatLng getPosition() {
        return this.location;
    }

    @Override
    public String getTitle() {
        return getRepImage().getFile()
                            .toPath()
                            .toString()
                            .substring(0, 5);
    }

    @Override
    public String getSnippet() {
        return getRepImages().size() + "개";
    }

}
