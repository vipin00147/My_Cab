package com.example.fragmenttravel.View;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.fragmenttravel.Model.Constants;
import com.example.fragmenttravel.Model.LocationService;
import com.example.fragmenttravel.Model.OTPModel;
import com.example.fragmenttravel.R;
import com.google.android.material.button.MaterialButton;

public class Welcome_ extends AppCompatActivity {

    private MaterialButton allow;
    private Bundle b1;

    private static final int REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        b1 = new Bundle();

        SharedPreferences sh = getSharedPreferences("MyData", Context.MODE_PRIVATE);

        String phone = sh.getString("phone", "");
        String name =  sh.getString("name", "");
        String email =  sh.getString("email", "");

        try {
            allow = findViewById(R.id.allowpermission);
            Intent intent = getIntent();
            b1 = intent.getExtras();//we have to  use intent here...
        }
        catch (Exception e){
            e.printStackTrace();
        }

        if(b1 == null){
            b1 = new Bundle();
            b1.putString("phone",phone);
            b1.putString("name",name);
            b1.putString("email",email);
        }

        allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OTPModel.setPermissionAllowed(1);
                CheckSelfPermissions();
            }
        });
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

    private void CheckSelfPermissions() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) +
                ContextCompat.checkSelfPermission(getApplicationContext(),
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

    private void stopLocationService() {
        if (isLocationServiceRunning()) {
            Intent intent = new Intent(this, LocationService.class);
            intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
            this.startService(intent);
            Toast.makeText(this, "Location Service Stopped", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE && grantResults.length > 0) {
            if (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d("LocationServiceRunning","Permission Granted");
                startLocationService();
            }
        }
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
}