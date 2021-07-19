package com.example.fragmenttravel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.button.MaterialButton;

public class Home extends AppCompatActivity {
    private MaterialButton open_drawer, add_fav;
    private DrawerLayout mDrawerLayout;
    private TextView name, phone, email;

    public static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        SharedPreferences sh = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String user_phone = sh.getString("phone", "");
        String user_name =  sh.getString("name", "");
        String user_email =  sh.getString("email", "");

        open_drawer = findViewById(R.id.open_drawer);
        mDrawerLayout = findViewById(R.id.nav_drawer_layout);
        add_fav = findViewById(R.id.add_fav);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);

        name.setText(user_name);
        phone.setText(user_phone);
        email.setText(user_email);

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
                save_favourite.show(getSupportFragmentManager(),"dialogFragment");
            }
        });


        if(ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Home.this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                    },REQUEST_CODE_LOCATION_PERMISSION
            );
        }
        else {
            startLocationService();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length >0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startLocationService();
            }
            else{
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isLocationServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager != null){
            for(ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)){
                if(LocationService.class.getName().equals(service.service.getClassName())){
                    if(service.foreground){
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    private void startLocationService(){
        if(!isLocationServiceRunning()){
            Intent intent = new Intent(this, LocationService.class);
            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
            this.startService(intent);
            Toast.makeText(this, "Location Service Started", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationService(){
        if(isLocationServiceRunning()){
            Intent intent = new Intent(this, LocationService.class);
            intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
            this.startService(intent);
            Toast.makeText(this, "Location Service Stopped", Toast.LENGTH_SHORT).show();
        }
    }

    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        //fAuth.signOut();
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}