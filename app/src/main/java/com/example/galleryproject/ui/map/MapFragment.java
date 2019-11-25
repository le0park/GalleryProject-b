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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import com.example.galleryproject.Database.AppDatabase;
import com.example.galleryproject.Database.AppExecutors;
import com.example.galleryproject.Model.Image;
import com.example.galleryproject.Model.ImageCollection;
import com.example.galleryproject.R;
import com.example.galleryproject.Util.DatabaseUtils;

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

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback,
                                                     ClusterManager.OnClusterClickListener<MarkerItem>,
                                                     ClusterManager.OnClusterInfoWindowClickListener<MarkerItem>,
                                                     ClusterManager.OnClusterItemClickListener<MarkerItem>,
                                                     ClusterManager.OnClusterItemInfoWindowClickListener<MarkerItem> {
    private GoogleMap mMap;
    private ClusterManager<MarkerItem> mClusterManager;
    private MapViewModel mapViewModel;

    AppDatabase mDb;
    List<ImageCollection> collections;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        setUpMap();

        mapViewModel = ViewModelProviders.of(this).get(MapViewModel.class);
        mDb = AppDatabase.getInstance(getContext());
        collections = new ArrayList<>();
        return root;
    }

    private class MarkerItemRenderer extends DefaultClusterRenderer<MarkerItem> {
        private final IconGenerator mIconGenerator = new IconGenerator(getActivity().getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getActivity().getApplicationContext());
        private final ImageView mImageView;
//        private final TextView mTextView;
        private final ImageView mClusterImageView;
//        private final TextView mClusterTextView;
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

            // Single marker layout
            View singleMarker_rootView = getLayoutInflater().inflate(R.layout.map_marker, null);
            mIconGenerator.setBackground(clusterDraw);
            mIconGenerator.setContentView(singleMarker_rootView);

            mImageView = (ImageView) singleMarker_rootView.findViewById(R.id.mapMarker_imageView);
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
        }

        @Override
        protected void onBeforeClusterItemRendered(MarkerItem marker, MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their name.

            icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(marker.getTitle());
        }

        @Override
        protected void onClusterItemRendered(final MarkerItem clusterItem, final Marker marker) {
            Glide.with(getActivity())
                 .load(clusterItem.getRepImage().getFile())
                 .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                 .thumbnail(0.1f)
                 .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {
                        Marker marker = getMarker(clusterItem);
                        if (marker == null) {
                            return;
                        }

                        mImageView.setImageDrawable(drawable);
                        icon = mIconGenerator.makeIcon();

                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
                    }
                 });
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<MarkerItem> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).


        }

        @Override
        protected void onClusterRendered(final Cluster<MarkerItem> cluster, final Marker marker) {
            final List<Drawable> markerPhotos = new ArrayList<>(Math.min(4, cluster.getSize()));
            final int width = mDimension;
            final int height = mDimension;
            for (MarkerItem item : cluster.getItems()) {
                // Draw 4 at most.
                if (markerPhotos.size() == 4) break;
                try {
                    Glide.with(getActivity().getApplicationContext())
                            .load(item.getRepImage().getFile())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .thumbnail(0.1f)
                            .into(new SimpleTarget<Drawable>(){
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    Marker marker = getMarker(item);
                                    if (marker == null) {
                                        return;
                                    }

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

    protected void initClusterManager(boolean isRestore) {
        if (!isRestore) {
            getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.715133, 126.734086), 10f));
        }

        mClusterManager = new ClusterManager<>(getActivity().getApplicationContext(), getMap());
        mClusterManager.setRenderer(new MarkerItemRenderer());
        getMap().setOnCameraIdleListener(mClusterManager);
        getMap().setOnMarkerClickListener(mClusterManager);
        getMap().setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);

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
        initClusterManager(false);
        AppExecutors.getInstance()
                .diskIO()
                .execute(() -> {
                    int offset = 0;
                    int size = -1;
                    while ((offset == 0 && size == -1) ||
                            (size != 0)) {
                        List<ImageCollection> clist = DatabaseUtils.getCollectionsFromDbByRange(mDb, 400, offset);

                        size = clist.size();
                        offset += size;
                        if (size > 0) {
                            AppExecutors.getInstance().mainThread().execute(() -> mapViewModel.insertAll(clist));
                        }
                    }
                });

        mapViewModel.getImageCollections().observe(this, (collections) -> {
            getMap().clear();
            mClusterManager.clearItems();

            List<MarkerItem> items = new ArrayList<>();
            for (ImageCollection c: collections) {
                List<Image> images = c.getRepImages();
                Image repImage = images.get(0);
                LatLng location = null;
                if (images.size() > 0) {
                    double lat = repImage.getLatitude();
                    double lng = repImage.getLongitude();

                    if (lat == 0.0 || lng == 0.0) {
                        continue;
                    } else {
                        location = new LatLng(lat, lng);
                    }
                }
                if (location == null) {
                    continue;
                }

                items.add(new MarkerItem(location, c));
            }

            mClusterManager.addItems(items);
            Log.e("MARKER_CLUSTER", "DO CLUSTER!");
            Log.e("MARKER_CLUSTER", collections.size() + "");
            Log.e("MARKER_CLUSTER", collections.size() + "");
            mClusterManager.cluster();
        });
    }
}