package com.prad.cs160.represent;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText zipcode;
    private Geocoder gcoder;

    public final static String ZIP_CODE = "com.prad.cs160.represent.ZIP_CODE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mapFragment.getActivity().setActionBar(toolbar);
        mapFragment.getActivity().getActionBar().setDisplayShowTitleEnabled(true);

        gcoder = new Geocoder(this, Locale.getDefault());

        zipcode = (EditText) findViewById(R.id.zip_code);
    }

    public void lookupReps(View view) {
        int zip = Integer.parseInt(zipcode.getText().toString());

        Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
        sendIntent.putExtra(ZIP_CODE, zip);
        startService(sendIntent);

        Intent intent = new Intent(this, Representatives.class);
        intent.putExtra(ZIP_CODE, zip);
        startActivity(intent);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng berkeley = new LatLng(37.87, -122.27);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(berkeley, 14.0f));
        setZipcode(berkeley);
        mMap.setOnCameraChangeListener(new OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                LatLng newCenter = mMap.getCameraPosition().target;
                setZipcode(newCenter);
            }
        });
    }

    private void setZipcode(LatLng location) {
        List<Address> addresses;
        try {
            addresses = gcoder.getFromLocation(location.latitude, location.longitude, 1);
            zipcode.setText(addresses.get(0).getPostalCode());
        } catch (Exception e) {
            // Do nothing.
        }
    }
}
