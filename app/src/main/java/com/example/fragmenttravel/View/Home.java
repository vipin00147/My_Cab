package com.example.fragmenttravel.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.fragmenttravel.Model.Constants;
import com.example.fragmenttravel.Model.LocationService;
import com.example.fragmenttravel.Model.UserCurrentLocation;
import com.example.fragmenttravel.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;

public class Home extends AppCompatActivity implements OnMapReadyCallback {
    private MaterialButton open_drawer, add_fav;
    private DrawerLayout mDrawerLayout;
    private Marker Center_marker;
    private FloatingActionButton gpsButton;


    private GoogleMap mapAPI;
    private SupportMapFragment mapFragment;
    private LatLng address;
    private MarkerOptions markerOptions;


    final static int REQUEST_CHECK_CODE = 100;
    final static int REQUEST_CODE_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapAPI);
        mapFragment.getMapAsync(this::onMapReady);

        open_drawer = findViewById(R.id.open_drawer);
        mDrawerLayout = findViewById(R.id.nav_drawer_layout);
        add_fav = findViewById(R.id.add_fav);
        gpsButton = findViewById(R.id.gps);

        open_drawer.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.START);
            }
        });

        add_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_favourite save_favourite = new save_favourite();
                save_favourite.show(getSupportFragmentManager(), "dialogFragment");
            }
        });

        noLocationFound noLocationFound = new noLocationFound();
        noLocationFound.show(getSupportFragmentManager(), "dialogFragment");

        gpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                address = new LatLng(new UserCurrentLocation().getLat(), new UserCurrentLocation().getLon());
                Center_marker.setPosition(address);
                mapAPI.animateCamera(CameraUpdateFactory.newLatLngZoom(address,19));
            }
        });

        //enable GPS location
        enableGPSFromDevice();
        startLocationService();
    }

    private void enableGPSFromDevice() {
        LocationRequest request = new LocationRequest()
                .setFastestInterval(1500)
                .setInterval(3000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addAllLocationRequests(Collections.singleton(request));

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    task.getResult(ApiException.class);
                    address = new LatLng(new UserCurrentLocation().getLat(), new UserCurrentLocation().getLon());
                    Center_marker.setPosition(address);
                } catch (ApiException e) {
                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(Home.this, REQUEST_CHECK_CODE);

                            } catch (IntentSender.SendIntentException sendIntentException) {
                                sendIntentException.printStackTrace();
                            } catch (ClassCastException classCastException) {
                                classCastException.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==  resultCode){
            //address = new LatLng(new UserCurrentLocation().getLat(), new UserCurrentLocation().getLon());
            //Center_marker.setPosition(address);
        }
    }

    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        //fAuth.signOut();
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        address = new LatLng(new UserCurrentLocation().getLat(), new UserCurrentLocation().getLon());
        Log.d("myCurrentLocation", String.valueOf(address));

        markerOptions = new MarkerOptions();
        mapAPI = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mapAPI.setMyLocationEnabled(true);
        mapAPI.setTrafficEnabled(true);

        markerOptions.position(mapAPI.getCameraPosition().target);
        Center_marker = mapAPI.addMarker(markerOptions);
        Center_marker.setPosition(address);
        Center_marker.setIcon(BitmapFromVector(getApplicationContext(), R.drawable.marker));

        mapAPI.moveCamera(CameraUpdateFactory.newLatLngZoom(address, 17));
        mapAPI.setMapType(mapAPI.MAP_TYPE_SATELLITE);



        mapAPI.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                Center_marker.setPosition(mapAPI.getCameraPosition().target);
            }
        });
    }
    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            Intent intent = new Intent(this, LocationService.class);
            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
            this.startService(intent);
            Toast.makeText(this, "Location Service Started", Toast.LENGTH_SHORT).show();

        }
        else if(isLocationServiceRunning()){
            Log.d("LocationServiceRunning","Running");
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (LocationService.class.getName().equals(service.service.getClassName())) {
                    if (service.foreground) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }
}