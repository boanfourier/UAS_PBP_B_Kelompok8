package com.medicare.prohealthymedicare;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.medicare.prohealthymedicare.databinding.FragmentKetentuanBinding;

public class KetentuanFragment extends Fragment {
    MarkerOptions marker;

        FragmentKetentuanBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Mapbox.getInstance(requireContext(), getString(R.string.mapbox_access_token));

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_ketentuan,container,false);
        binding.getLifecycleOwner();


        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {

                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        CameraPosition position = new CameraPosition.Builder()
                                .target(new LatLng(-7.799337, 110.370038))
                                .zoom(10)
                                .tilt(20)
                                .build();                        // Map is set up and the style has loaded. Now you can add data or make other map adjustments

                        mapboxMap.addMarker(new MarkerOptions()
                                .position(new LatLng(-7.799337, 110.370038))
                                .title("Posisi Medicare"));


                        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position),3000);

                    }
                });

            }
        });
        return binding.getRoot();
     }
    @Override
    public void onResume() {
        super.onResume();
        binding.mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        binding.mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.mapView.onDestroy();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        binding.mapView.onSaveInstanceState(outState);
    }

}