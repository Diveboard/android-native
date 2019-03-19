package com.diveboard.mobile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.diveboard.model.Spot2;
import com.diveboard.model.SpotService;
import com.diveboard.util.ImageUtils;
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

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class SelectSpotFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnCameraMoveListener, GoogleMap.OnMarkerClickListener {
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
    private AutoSuggestAdapter<Spot2> adapter;
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
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
    }

    private void setupSubmit(View view) {
        submitButton = view.findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedMarker == null) {
                    return;
                }
                ac.currentDive.setSpot((Spot2) selectedMarker.getTag());
                Navigation.findNavController(view).popBackStack();
            }
        });
    }

    private void setupAutocompleteList(View view) {
        service = new SpotService(ac);
        suggest = view.findViewById(R.id.appCompatAutoCompleteTextView);
        progress = view.findViewById(R.id.progress_bar);
        adapter = new AutoSuggestAdapter<>(ac, android.R.layout.simple_dropdown_item_1line);
        suggest.setAdapter(adapter);
        suggest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //hide keyboard
                InputMethodManager imm = (InputMethodManager) ac.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                //navigate to position
                Spot2 spot = (Spot2) adapterView.getItemAtPosition(position);
                setMapPosition(spot);
            }
        });
        suggest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (selectedMarker != null && ((Spot2) selectedMarker.getTag()).name != null && ((Spot2) selectedMarker.getTag()).name.equals(s.toString())) {
                    return;
                } else {
                    resetSelectedSpot();
                }
                if (s == null || s.length() < 2) {
                    adapter.setData(null);
                    return;
                }
                handler.removeMessages(MESSAGE_TEXT_CHANGED);
                handler.sendMessageDelayed(handler.obtainMessage(MESSAGE_TEXT_CHANGED, s.toString()), DELAY_MS);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        handler = new AutoSuggestHandler(this);
        mapMoveHandler = new MapMoveHandler(this);
    }

    private void setMapPosition(Spot2 spot) {
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
        suggest.setText(((Spot2) marker.getTag()).name);
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

    private Marker addMarker(Spot2 spot, boolean primary) {
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
        private WeakReference<SelectSpotFragment> outerRef;

        MapMoveHandler(SelectSpotFragment outer) {
            this.outerRef = new WeakReference<>(outer);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final SelectSpotFragment activity = outerRef.get();
            if (activity == null) {
                return;
            }
            if (activity.map == null || activity.map.getCameraPosition().zoom < MIN_ZOOM_WITH_MARKERS) {
                return;
            }
            Log.d(SelectSpotFragment.class.getName(), "Bound Spots requested");
            CameraPosition position = activity.map.getCameraPosition();
            final LatLngBounds latLngBounds = activity.map.getProjection().getVisibleRegion().latLngBounds;
            activity.removeMarkersOutOfBounds(latLngBounds);
            activity.service.searchSpot(null, position.target, latLngBounds, new com.diveboard.util.Callback<List<Spot2>>() {
                @Override
                public void execute(List<Spot2> data) {
                    if (data == null) {
                        return;
                    }
                    Log.d(SelectSpotFragment.class.getName(), "Bound Spots arrived: " + data.size());
                    SelectSpotFragment outer = outerRef.get();
                    if (outer == null) {
                        return;
                    }
                    addMarkers(data);
                }
            }, new com.diveboard.util.Callback<String>() {
                @Override
                public void execute(String data) {
                    SelectSpotFragment outer = outerRef.get();
                    if (outer == null) {
                        return;
                    }
                    outer.showError(data);
                }
            });
        }

        private synchronized void addMarkers(List<Spot2> data) {
            final SelectSpotFragment activity = outerRef.get();
            if (activity == null) {
                return;
            }
            for (Spot2 spot : data) {
                activity.addMarker(spot, false);
            }
        }
    }

    private static class AutoSuggestHandler extends Handler {

        private WeakReference<SelectSpotFragment> outerRef;

        AutoSuggestHandler(SelectSpotFragment outer) {
            this.outerRef = new WeakReference<>(outer);
        }

        @Override
        public void handleMessage(Message msg) {
            SelectSpotFragment outer = outerRef.get();
            if (outer == null) {
                return;
            }
            outer.progress.setVisibility(View.VISIBLE);
            outer.service.searchSpot(msg.obj.toString(), null, null, new com.diveboard.util.Callback<List<Spot2>>() {
                @Override
                public void execute(List<Spot2> data) {
                    //TODO: there is an issue. if user types next letter and results from prev term arrived then autosuggest will shown inconsistent results
                    SelectSpotFragment outer = outerRef.get();
                    if (outer == null) {
                        return;
                    }
                    outer.adapter.setData(data);
                    outer.progress.setVisibility(View.GONE);
                }
            }, new com.diveboard.util.Callback<String>() {
                @Override
                public void execute(String data) {
                    SelectSpotFragment outer = outerRef.get();
                    if (outer == null) {
                        return;
                    }
                    outer.showError(data);
                }
            });
        }
    }
}
