package com.prad.cs160.represent;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.prad.cs160.apilibrary.LookupElectionInformation;
import com.prad.cs160.apilibrary.ElectionInformation;

import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText zipcode;
    private Geocoder gcoder;
    private LookupElectionInformation lei;

    int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

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
        String admin_level_1, admin_level_2;

        Log.d("T", "Received Zip Code: " + zip);
        lei = new LookupElectionInformation(zip, getBaseContext());

        ElectionInformation info = lei.getInfo();

        Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
        sendIntent.putExtra(PhoneToWatchService.INFO, info);
        startService(sendIntent);

        Intent intent = new Intent(this, CongressionalActivity.class);
        intent.putExtra(CongressionalActivity.INFO, info);
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
        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }

        // Default to current GPS position.
        // Get away with not checking this.
        Location bestLocation = null;
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, android.os.Process.myPid(), android.os.Process.myUid()) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            bestLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));
        }
        // Direct to berkeley by default.
        LatLng latLng = new LatLng(37.87, -122.27);
        if (bestLocation != null) {
            Log.d("T", "Using GPS location");
            latLng = new LatLng(bestLocation.getLatitude(), bestLocation.getLongitude());
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
        setZipcode(latLng);

        /*LatLng berkeley = new LatLng(37.87, -122.27);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(berkeley, 14.0f));
        setZipcode(berkeley);*/

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
            Log.d("T", e.toString());
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }
}
