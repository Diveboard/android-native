package com.diveboard.mobile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.diveboard.dataaccess.datamodel.Spot;
import com.diveboard.model.SpotService;
import com.diveboard.util.ImageUtils;
import com.diveboard.util.ResponseCallback;
import com.diveboard.util.Utils;
import com.diveboard.util.binding.AutoSuggestAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SelectSpotPage extends Fragment implements OnMapReadyCallback, GoogleMap.OnCameraMoveListener, GoogleMap.OnMarkerClickListener {
    private static final int MESSAGE_TEXT_CHANGED = 1;
    private static final long DELAY_MS = 300;
    private static final float MIN_ZOOM_WITH_MARKERS = 10;
    private static final int MESSAGE_MAP_MOVED = 2;
    //to not spam server
    private static final long MAP_MOVE_DELAY_MS = 1000;
    private static final int REQUEST_PERMISSION_CODE = 1;
    final List<Marker> markers = Collections.synchronizedList(new ArrayList());
    private Handler handler;
    private GoogleMap map;
    private SpotService service;
    private View progress;
    private AutoSuggestAdapter<Spot> adapter;
    private MapMoveHandler mapMoveHandler;
    private AppCompatAutoCompleteTextView suggest;
    private View submitButton;
    private Marker selectedMarker;
    private ApplicationController ac;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ac = (ApplicationController) getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.select_spot, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setupSubmit(view);
        setupBack(view);
        setupAutocompleteList(view);
        return view;
    }

    private void fillInSpot() {
        if (ac.currentDive == null || ac.currentDive.getSpot() == null) {
            toggleSubmitButton(false);
            return;
        }
        setMapPosition(ac.currentDive.getSpot());
    }

    private void setupBack(View view) {
        View backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(view1 -> Navigation.findNavController(view1).popBackStack());
    }

    private void setupSubmit(View view) {
        submitButton = view.findViewById(R.id.submit);
        submitButton.setOnClickListener(view1 -> {
            if (selectedMarker == null) {
                return;
            }
            ac.currentDive.setSpot((Spot) selectedMarker.getTag());
            Navigation.findNavController(view1).popBackStack();
        });
    }

    private void setupAutocompleteList(View view) {
        service = ac.getSpotService();
        suggest = view.findViewById(R.id.appCompatAutoCompleteTextView);
        progress = view.findViewById(R.id.progress_bar);
        adapter = new AutoSuggestAdapter<>(ac, R.layout.spot_item);
        suggest.setAdapter(adapter);
        suggest.setOnItemClickListener((adapterView, view1, position, id) -> {
            Utils.hideKeyboard(ac, view1);
            //navigate to position
            Spot spot = (Spot) adapterView.getItemAtPosition(position);
            setMapPosition(spot);
        });
        suggest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeMessages(MESSAGE_TEXT_CHANGED);
                //user either can type in spot name or select in on map and name is autopopulated in search box
                if (selectedMarker != null
                        && selectedMarker.getTag() != null
                        && ((Spot) selectedMarker.getTag()).name != null
                        && ((Spot) selectedMarker.getTag()).name.equals(s.toString())) {
                    return;
                } else {
                    resetSelectedSpot();
                }
                if (s == null || s.length() < 3) {
                    adapter.setData(null);
                    return;
                }
                handler.sendMessageDelayed(handler.obtainMessage(MESSAGE_TEXT_CHANGED, s.toString()), DELAY_MS);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        handler = new AutoSuggestHandler(this);
        mapMoveHandler = new MapMoveHandler(this);
    }

    private void setMapPosition(Spot spot) {
        if (map == null || spot.getLatLng() == null) {
            return;
        }
        map.clear();
        removeMarkers();
        Marker marker = addMarker(spot, true);
        selectSpot(marker);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(spot.getLatLng())
                .zoom(12).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void selectSpot(Marker marker) {
        selectedMarker = marker;
        toggleSubmitButton(true);
        suggest.setText(((Spot) marker.getTag()).name);
        suggest.dismissDropDown();
    }

    private void resetSelectedSpot() {
        if (selectedMarker != null) {
            selectedMarker.hideInfoWindow();
            selectedMarker = null;
            toggleSubmitButton(false);
        }
    }

    private void removeMarkers() {
        synchronized (markers) {
            Iterator itr = markers.iterator();
            while (itr.hasNext()) {
                Marker marker = (Marker) itr.next();
                marker.remove();
                itr.remove();
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnCameraMoveListener(this);

        if (ActivityCompat.checkSelfPermission(ac, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(ac, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_CODE);
        }
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setOnMarkerClickListener(this);
        fillInSpot();
    }

    @Override
    @SuppressLint("MissingPermission")
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    map.setMyLocationEnabled(true);
                }
            }
        }
    }

    @Override
    public void onCameraMove() {
        mapMoveHandler.removeMessages(MESSAGE_MAP_MOVED);
        mapMoveHandler.sendEmptyMessageDelayed(MESSAGE_MAP_MOVED, MAP_MOVE_DELAY_MS);
    }

    private Marker addMarker(Spot spot, boolean primary) {
        synchronized (markers) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(spot.getLatLng())
                    .title(spot.name)
                    .icon(ImageUtils.bitmapDescriptorFromVector(ac, R.drawable.ic_noun_scuba_diving_flag_713532));
            Marker marker = map.addMarker(markerOptions);
            if (primary) {
                marker.showInfoWindow();
            }
            marker.setTag(spot);
            markers.add(marker);
            return marker;
        }
    }

    private void removeMarkersOutOfBounds(LatLngBounds bounds) {
        synchronized (markers) {
            Iterator itr = markers.iterator();
            while (itr.hasNext()) {
                Marker marker = (Marker) itr.next();
                if (!bounds.contains(marker.getPosition())) {
                    marker.remove();
                    itr.remove();
                }
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        selectSpot(marker);
        return false;
    }

    private void toggleSubmitButton(boolean enable) {
        submitButton.setClickable(enable);
        submitButton.setAlpha(enable ? 1.0f : 0.5f);
    }

    private void showError(String text) {
        Toast toast = Toast.makeText(ac, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    private static class MapMoveHandler extends Handler {
        private WeakReference<SelectSpotPage> outerRef;

        MapMoveHandler(SelectSpotPage outer) {
            this.outerRef = new WeakReference<>(outer);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final SelectSpotPage activity = outerRef.get();
            if (activity == null) {
                return;
            }
            if (activity.map == null || activity.map.getCameraPosition().zoom < MIN_ZOOM_WITH_MARKERS) {
                return;
            }
            Log.d(SelectSpotPage.class.getName(), "Bound Spots requested");
            CameraPosition position = activity.map.getCameraPosition();
            final LatLngBounds latLngBounds = activity.map.getProjection().getVisibleRegion().latLngBounds;
            activity.removeMarkersOutOfBounds(latLngBounds);
            activity.service.searchSpot(null, position.target, latLngBounds,
                    new ResponseCallback<List<Spot>>() {
                        @Override
                        public void success(List<Spot> data) {
                            if (data == null) {
                                return;
                            }
                            Log.d(SelectSpotPage.class.getName(), "Bound Spots arrived: " + data.size());
                            SelectSpotPage outer = outerRef.get();
                            if (outer == null) {
                                return;
                            }
                            addMarkers(data);
                        }

                        @Override
                        public void error(Exception error) {
                            SelectSpotPage outer = outerRef.get();
                            if (outer == null) {
                                return;
                            }
                            outer.showError(error.getMessage());
                            Utils.logError(SelectSpotPage.class, "Cannot find spot", error);
                        }
                    });
        }

        private synchronized void addMarkers(List<Spot> data) {
            final SelectSpotPage activity = outerRef.get();
            if (activity == null) {
                return;
            }
            for (Spot spot : data) {
                activity.addMarker(spot, false);
            }
        }
    }

    private static class AutoSuggestHandler extends Handler {

        private WeakReference<SelectSpotPage> outerRef;

        AutoSuggestHandler(SelectSpotPage outer) {
            this.outerRef = new WeakReference<>(outer);
        }

        @Override
        public void handleMessage(Message msg) {
            SelectSpotPage outer = outerRef.get();
            if (outer == null) {
                return;
            }
            outer.progress.setVisibility(View.VISIBLE);
            String term = msg.obj.toString();
            outer.service.searchSpot(term, null, null,
                    new ResponseCallback<List<Spot>>() {
                        @Override
                        public void success(List<Spot> data) {
                            SelectSpotPage outer12 = outerRef.get();
                            if (outer12 == null) {
                                return;
                            }
                            outer12.progress.setVisibility(View.GONE);
                            if (!term.equals(outer12.suggest.getText().toString())) {
                                return;
                            }
                            outer12.adapter.setData(data);
                        }

                        @Override
                        public void error(Exception error) {
                            SelectSpotPage outer1 = outerRef.get();
                            if (outer1 == null) {
                                return;
                            }
                            outer1.progress.setVisibility(View.GONE);
                            outer1.showError(error.getMessage());
                            Utils.logError(SelectSpotPage.class, "Cannot find spot", error);
                        }
                    });
        }
    }
}
