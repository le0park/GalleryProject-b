package com.example.galleryproject.ui.map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;

import com.bumptech.glide.request.target.CustomTarget;

import com.bumptech.glide.request.transition.Transition;
import com.example.galleryproject.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.security.MessageDigest;
import java.util.concurrent.ExecutionException;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private MapViewModel mapViewModel;

    private GoogleMap mMap;

    private View marker_root_view;
    private TextView mapMarker_textView;
    private ImageView mapMarker_imageView;

    String filePath_example = "/storage/emulated/0/DCIM/Camera/20180401_163414.jpg";
    String filePath_example1 = "/storage/emulated/0/DCIM/Camera/20180401_162319.jpg";
    String filePath_example2 = "/storage/emulated/0/DCIM/Camera/20180401_162309.jpg";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mapViewModel = ViewModelProviders.of(this).get(MapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_map, container, false);
//        mapViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng SEOUL = new LatLng(37.56, 126.97);

//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(SEOUL);
//        markerOptions.title("서울");
//        markerOptions.snippet("한국의 수도");
//        mMap.addMarker(markerOptions);

//        addCustomMarker();

        marker_root_view = LayoutInflater.from(getContext()).inflate(R.layout.map_marker, null);
        mapMarker_textView = (TextView)marker_root_view.findViewById(R.id.mapMarker_textView);
        mapMarker_imageView = (ImageView)marker_root_view.findViewById(R.id.mapMarker_imageView);

        addCustomMarker(new MarkerItem(37.56, 126.97, 2, filePath_example));
        addCustomMarker(new MarkerItem(37.4, 126.9, 4, filePath_example1));
        addCustomMarker(new MarkerItem(37.60, 127.0, 10, filePath_example2));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
    }

    private void addCustomMarker(MarkerItem item) {
        if (mMap == null) {
            return;
        }

        final double latitude = item.getLatitude();
        final double longitude = item.getLongitude();
        final String photoNum = String.valueOf(item.getPhotoNum());

        Glide.with(getContext()).
                load(new File(item.getFilePath()))
                .fitCenter()
                .transform()
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        LatLng position = new LatLng(latitude, longitude);
                        mapMarker_textView.setText(photoNum);
                        mMap.addMarker(new MarkerOptions()
                                .position(position)
                                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(marker_root_view, ((BitmapDrawable)resource).getBitmap()))));
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    private void addCustomMarker() {
        if (mMap == null) {
            return;
        }

        Glide.with(getContext()).
                load(new File(filePath_example))
                .fitCenter()
                .transform()
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        LatLng SEOUL = new LatLng(37.56, 126.97);
                        mMap.addMarker(new MarkerOptions()
                        .position(SEOUL)
                        .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(marker_root_view, ((BitmapDrawable)resource).getBitmap()))));
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    private Bitmap getMarkerBitmapFromView(View view, Bitmap bitmap) {
        mapMarker_imageView.setImageBitmap(bitmap);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = view.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        view.draw(canvas);
        return returnedBitmap;
    }
}