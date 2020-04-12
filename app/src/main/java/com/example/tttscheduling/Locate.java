package com.example.tttscheduling;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;


public class Locate extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker currentLocationmMarker;
    public static final int REQUEST_LOCATION_CODE = 99;
    double PROXIMITY_RADIUS = 5000;
    double latitude,longitude;
    private static final String TAG = "MainActivity";
    PlacesClient placesClient;

    double lat_search = 0, log_search = 0;
    String name_search= "";


    //for spinner
    Spinner sp_distance;
    ArrayList<String> arrayList_distance;
    ArrayAdapter<String> arrayAdapter_distance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();

        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String apiKey = "AIzaSyDsON3P8HNESpXKvcWBToywJlu7F5g2eAk";

        if (!Places.isInitialized()) {
            Places.initialize(Locate.this, apiKey);
        }
        placesClient = Places.createClient(this);

        // for spinner
        sp_distance = (Spinner) findViewById(R.id.sp_distance);
        arrayList_distance = new ArrayList<>();
        arrayList_distance.add("5 miles");
        arrayList_distance.add("10 miles");
        arrayList_distance.add("20 miles");

        arrayAdapter_distance = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_item, arrayList_distance);

        sp_distance.setAdapter(arrayAdapter_distance);

        sp_distance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                {
                    PROXIMITY_RADIUS = 1.6 * 5 * 1000; // 5 miles
                }

                if (position == 1)
                {
                    PROXIMITY_RADIUS = 1.6 * 10 * 1000; // 10 miles
                }

                if (position == 2)
                {
                    PROXIMITY_RADIUS = 1.6 * 20  * 1000; // 20 miles
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // end spinner


        Button next_map = (Button) this.findViewById(R.id.next_map);

        next_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(view);
            }
        });
    }

    public void sendMessage(View view) {
        final Intent retrieve = getIntent();

        int checkPP = retrieve.getIntExtra("ParentPage", 0);
        if (checkPP == 0) {// Go back to Patient Home
            Intent sendAddress = new Intent(this, PatientHome.class);
            sendAddress.putExtra("date", name_search);
            sendAddress.putExtra("address", name_search);
            startActivity(sendAddress);
        }
        else if (checkPP == 1) {// Go to the Admin Home
            Intent sendAddress = new Intent(this, SignupAdmin.class);
            sendAddress.putExtra("date", name_search);
            sendAddress.putExtra("address", name_search);
            startActivity(sendAddress);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case REQUEST_LOCATION_CODE:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED)
                    {
                        if(client == null)
                        {
                            buildGoogleAdiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    Toast.makeText(this,"Permission Denied" , Toast.LENGTH_LONG).show();
                }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleAdiClient();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);



        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                String markerTitle = marker.getTitle();
                name_search = markerTitle;
                Log.i("TEST MARKER", markerTitle);
                return false;
            }
        });
    }


    protected synchronized void buildGoogleAdiClient() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();

    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        lastlocation = location;

        if(currentLocationmMarker != null)
        {
            currentLocationmMarker.remove();

        }


        LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        currentLocationmMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        if(client != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);

        }

    }

    public void onClick(View v)
    {
        Object dataTransfer[] = new Object[2];
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();

        switch(v.getId())
        {

            case R.id.B_hopistals:
                mMap.clear();
                String hospital = "hospital";
                String url = getUrl(latitude, longitude, hospital);
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);


                Toast.makeText(Locate.this, "Showing Nearby Hospitals!", Toast.LENGTH_SHORT).show();

                break;

        }


    }


    private String getUrl(double latitude , double longitude , String nearbyPlace)
    {

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");

        googlePlaceUrl.append("&key="+"AIzaSyDsON3P8HNESpXKvcWBToywJlu7F5g2eAk");


        Log.d("Locate", "url = "+googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }

    public boolean checkLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED )
        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            return false;

        }
        else
            return true;
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

}
