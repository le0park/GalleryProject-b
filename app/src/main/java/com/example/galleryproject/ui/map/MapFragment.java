package com.example.galleryproject.ui.map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.galleryproject.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import org.w3c.dom.Text;

import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class MapFragment extends Fragment implements OnMapReadyCallback, ClusterManager.OnClusterClickListener<MarkerItem>, ClusterManager.OnClusterInfoWindowClickListener<MarkerItem>, ClusterManager.OnClusterItemClickListener<MarkerItem>, ClusterManager.OnClusterItemInfoWindowClickListener<MarkerItem> {
    private MapViewModel mapViewModel;

    private GoogleMap mMap;
    private ClusterManager<MarkerItem> mClusterManager;
    private Random mRandom = new Random(1984);

//    private View marker_root_view;
//    private TextView mapMarker_textView;
//    private ImageView mapMarker_imageView;

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

        setUpMap();

        return root;
    }

    private class MarkerItemRenderer extends DefaultClusterRenderer<MarkerItem> {
        private final IconGenerator mIconGenerator = new IconGenerator(getActivity().getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getActivity().getApplicationContext());
        private final ImageView mImageView;
        private final TextView mTextView;
        private final ImageView mClusterImageView;
        private final TextView mClusterTextView;
        private final int mDimension;
        Bitmap icon;
        MarkerItemRenderer() {
            super(getActivity().getApplicationContext(), getMap(), mClusterManager);

            // Group marker layout
            View clusterMarker_rootView = getLayoutInflater().inflate(R.layout.map_marker, null);
            Drawable clusterDraw = getResources().getDrawable(R.drawable.map_bubble, null);
            mClusterIconGenerator.setBackground(clusterDraw);
            mClusterIconGenerator.setContentView(clusterMarker_rootView);

            mClusterImageView = (ImageView) clusterMarker_rootView.findViewById(R.id.mapMarker_imageView);
            mClusterTextView = (TextView) clusterMarker_rootView.findViewById(R.id.mapMarker_textView);

            // Single marker layout
            View singleMarker_rootView = getLayoutInflater().inflate(R.layout.map_marker, null);
            mIconGenerator.setBackground(clusterDraw);
            mIconGenerator.setContentView(singleMarker_rootView);

            mImageView = (ImageView) singleMarker_rootView.findViewById(R.id.mapMarker_imageView);
            mTextView = (TextView) singleMarker_rootView.findViewById(R.id.mapMarker_textView);
//            mImageView = new ImageView(getActivity().getApplicationContext());
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
//            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
//            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
//            mImageView.setPadding(padding, padding, padding, padding);
        }

        @Override
        protected void onBeforeClusterItemRendered(MarkerItem marker, MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their name.

//            mImageView.setImageResource(person.profilePhoto);
//            Glide.with(getActivity().getApplicationContext()).
//                    load(new File(marker.getFilePath())).
//                    into(mImageView);
//            Log.e("SINGLE_MAREKER", marker.getFilePath());
            mTextView.setText("1");

            icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(marker.getTitle());
        }

        @Override
        protected void onClusterItemRendered(MarkerItem clusterItem, Marker marker) {
            Glide.with(getActivity())
                    .load(new File(clusterItem.getFilePath()))
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .thumbnail(0.1f)
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {
                            mImageView.setImageDrawable(drawable);
                            icon = mIconGenerator.makeIcon();
                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
                        }
                    });        }

        @Override
        protected void onBeforeClusterRendered(Cluster<MarkerItem> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).

//            List<Drawable> markerPhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
//
//            int width = mDimension;
//            int height = mDimension;
//
//            for (MarkerItem item : cluster.getItems()) {
//                // Draw 4 at most.
//                if (markerPhotos.size() == 4) break;
////                Drawable drawable = getResources().getDrawable(item.profilePhoto);
//                Drawable drawable = Drawable.createFromPath(item.getFilePath());
//                Log.e("MARKER_PHOto", "add" + drawable.toString());
//
//                drawable.setBounds(0, 0, width, height);
//                markerPhotos.add(drawable);
//            }
//            MultiDrawable multiDrawable = new MultiDrawable(markerPhotos);
//            multiDrawable.setBounds(0, 0, width, height);
//
//            mClusterImageView.setImageDrawable(multiDrawable);
//            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
//            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected void onClusterRendered(Cluster<MarkerItem> cluster, Marker marker) {
            final List<Drawable> markerPhotos = new ArrayList<>(Math.min(4, cluster.getSize()));
            final int width = mDimension;
            final int height = mDimension;
            Bitmap dummyBitmap = null;
            Drawable drawable;
            final int clusterSize = cluster.getSize();
            final int[] count = {0};

            for (MarkerItem item : cluster.getItems()) {
                // Draw 4 at most.
                if (markerPhotos.size() == 4) break;
                try {
                    Glide.with(getActivity().getApplicationContext())
                            .load(new File(item.getFilePath()))
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                            .into(new SimpleTarget<Drawable>(){
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    resource.setBounds(0, 0, width, height);
                                    markerPhotos.add(resource);
                                    MultiDrawable multiDrawable = new MultiDrawable(markerPhotos);
                                    multiDrawable.setBounds(0, 0, width, height);
                                    mClusterImageView.setImageDrawable(multiDrawable);
                                    Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
                                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }

    @Override
    public boolean onClusterClick(Cluster<MarkerItem> cluster) {
        // Show a toast with some info when the cluster is clicked.
        String firstName = cluster.getItems().iterator().next().getTitle();
        Toast.makeText(getActivity().getApplicationContext(), cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();

        // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
        // inside of bounds, then animate to center of the bounds.

        // Create the builder to collect all essential cluster items for the bounds.
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        // Get the LatLngBounds
        final LatLngBounds bounds = builder.build();

        // Animate camera to the bounds
        try {
            getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<MarkerItem> cluster) {
        // Does nothing, but you could go to a list of the users.
    }

    @Override
    public boolean onClusterItemClick(MarkerItem item) {
        // Does nothing, but you could go into the user's profile page, for example.
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(MarkerItem item) {
        // Does nothing, but you could go into the user's profile page, for example.
    }

    protected void startDemo(boolean isRestore) {
        if (!isRestore) {
            getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 9.5f));
        }

        mClusterManager = new ClusterManager<MarkerItem>(getActivity().getApplicationContext(), getMap());
        mClusterManager.setRenderer(new MarkerItemRenderer());
        getMap().setOnCameraIdleListener(mClusterManager);
        getMap().setOnMarkerClickListener(mClusterManager);
        getMap().setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);

        addItems();
        mClusterManager.cluster();
    }

    private void addItems() {
//        // http://www.flickr.com/photos/sdasmarchives/5036248203/
//        mClusterManager.addItem(new Person(position(), "Walter", R.drawable.walter));
//
//        // http://www.flickr.com/photos/usnationalarchives/4726917149/
//        mClusterManager.addItem(new Person(position(), "Gran", R.drawable.gran));
//
//        // http://www.flickr.com/photos/nypl/3111525394/
//        mClusterManager.addItem(new Person(position(), "Ruth", R.drawable.ruth));
//
//        // http://www.flickr.com/photos/smithsonian/2887433330/
//        mClusterManager.addItem(new Person(position(), "Stefan", R.drawable.stefan));
//
//        // http://www.flickr.com/photos/library_of_congress/2179915182/
//        mClusterManager.addItem(new Person(position(), "Mechanic", R.drawable.mechanic));
//
//        // http://www.flickr.com/photos/nationalmediamuseum/7893552556/
//        mClusterManager.addItem(new Person(position(), "Yeats", R.drawable.yeats));
//
//        // http://www.flickr.com/photos/sdasmarchives/5036231225/
//        mClusterManager.addItem(new Person(position(), "John", R.drawable.john));
//
//        // http://www.flickr.com/photos/anmm_thecommons/7694202096/
//        mClusterManager.addItem(new Person(position(), "Trevor the Turtle", R.drawable.turtle));
//
//        // http://www.flickr.com/photos/usnationalarchives/4726892651/
//        mClusterManager.addItem(new Person(position(), "Teach", R.drawable.teacher));

        mClusterManager.addItem(new MarkerItem(position(), filePath_example));
        mClusterManager.addItem(new MarkerItem(position(), filePath_example));
        mClusterManager.addItem(new MarkerItem(position(), filePath_example1));
        mClusterManager.addItem(new MarkerItem(position(), filePath_example1));
        mClusterManager.addItem(new MarkerItem(position(), filePath_example2));
        mClusterManager.addItem(new MarkerItem(position(), filePath_example2));
    }

    private LatLng position() {
        return new LatLng(random(51.6723432, 51.38494009999999), random(0.148271, -0.3514683));
    }

    private double random(double min, double max) {
        return mRandom.nextDouble() * (max - min) + min;
    }


    public void setUpMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    public GoogleMap getMap() {
        return mMap;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (mMap != null) {
            return;
        }
        mMap = map;
        startDemo(false);
    }

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        LatLng SEOUL = new LatLng(37.56, 126.97);
//
//        marker_root_view = LayoutInflater.from(getContext()).inflate(R.layout.map_marker, null);
//        mapMarker_textView = (TextView) marker_root_view.findViewById(R.id.mapMarker_textView);
//        mapMarker_imageView = (ImageView) marker_root_view.findViewById(R.id.mapMarker_imageView);
//
//        addCustomMarker(new MarkerItem(37.56, 126.97, 2, filePath_example));
//        addCustomMarker(new MarkerItem(37.4, 126.9, 4, filePath_example1));
//        addCustomMarker(new MarkerItem(37.60, 127.0, 10, filePath_example2));
//
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
//    }

    private void addCustomMarker(MarkerItem item) {
//        if (mMap == null) {
//            return;
//        }
//
//        final double latitude = item.getLatitude();
//        final double longitude = item.getLongitude();
//        final String photoNum = String.valueOf(item.getPhotoNum());
//
//        Glide.with(getContext()).
//                load(new File(item.getFilePath()))
//                .fitCenter()
//                .transform()
//                .into(new CustomTarget<Drawable>() {
//                    @Override
//                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                        LatLng position = new LatLng(latitude, longitude);
//                        mapMarker_textView.setText(photoNum);
//                        mMap.addMarker(new MarkerOptions()
//                                .position(position)
//                                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(marker_root_view, ((BitmapDrawable) resource).getBitmap()))));
//                    }
//
//                    @Override
//                    public void onLoadCleared(@Nullable Drawable placeholder) {
//
//                    }
//                });
    }

//    private Bitmap getMarkerBitmapFromView(View view, Bitmap bitmap) {
//        mapMarker_imageView.setImageBitmap(bitmap);
//        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
//        view.buildDrawingCache();
//        Bitmap returnedBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
//                Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(returnedBitmap);
//        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
//        Drawable drawable = view.getBackground();
//        if (drawable != null)
//            drawable.draw(canvas);
//        view.draw(canvas);
//        return returnedBitmap;
//    }
}