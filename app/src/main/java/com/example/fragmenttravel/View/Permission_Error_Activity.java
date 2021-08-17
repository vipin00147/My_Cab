package com.example.fragmenttravel.View;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.fragmenttravel.Model.Constants;
import com.example.fragmenttravel.Model.LocationService;
import com.example.fragmenttravel.R;
import com.google.android.material.button.MaterialButton;

public class Permission_Error_Activity extends AppCompatActivity {

    private MaterialButton allow;

    private static final int REQUEST_CODE = 123;

    private Bundle b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission__error_);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        b1 = new Bundle();

        allow = findViewById(R.id.materialButton);

        b1 = getIntent().getExtras();

        //Getting Permissions from user...
        allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckSelfPermissions();
            }
        });
    }

    private void CheckSelfPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) +
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_PHONE_STATE
                    }, REQUEST_CODE
            );

        } else {
            startLocationService();
        }
    }

    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            Intent intent = new Intent(this, LocationService.class);
            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
            this.startService(intent);
            Log.d("LocationServiceRunning","Location Started");

        }
        else if(isLocationServiceRunning()){
            Log.d("LocationServiceRunning","Already Running");
        }
        Intent intent1 = new Intent(this, Home.class);
        intent1.putExtras(b1);
        startActivity(intent1);
    }

    private boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE && grantResults.length > 0) {
            if (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d("LocationServiceRunning","Permission Granted");
                startLocationService();
            }
            else {
                Intent intent = new Intent(this,Permission_Error_Activity.class);
                intent.putExtras(b1);
                startActivity(intent);
            }
        }
    }
}