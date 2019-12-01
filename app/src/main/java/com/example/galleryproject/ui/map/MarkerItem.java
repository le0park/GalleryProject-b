package com.example.galleryproject.ui.map;

import com.example.galleryproject.Model.Image;
import com.example.galleryproject.Model.ImageCollection;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.List;

public class MarkerItem implements ClusterItem {
    private LatLng position;
    private ImageCollection collection;

    public MarkerItem(LatLng position, ImageCollection collection){
        this.position = position;
        this.collection = collection;
    }

    public MarkerItem(double latitude, double longitude, ImageCollection collection){
        this(new LatLng(latitude, longitude), collection);
    }

    public double getLatitude() {
        return this.position.latitude;
    }

    public void setLatitude(double latitude) {
        this.position = new LatLng(latitude, getLongitude());
    }

    public double getLongitude() {
        return this.position.longitude;
    }

    public void setLongitude(double longitude) {
        this.position = new LatLng(getLatitude(), longitude);
    }

    public Image getRepImage() {
        if (collection.getRepImages().size() > 0) {
            return this.collection.getRepImages().get(0);
        }

        return null;
    }

    public ImageCollection getCollection() {
        return this.collection;
    }

    public List<Image> getRepImages() {
        return this.collection.getRepImages();
    }

    @Override
    public LatLng getPosition() {
        return this.position;
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
