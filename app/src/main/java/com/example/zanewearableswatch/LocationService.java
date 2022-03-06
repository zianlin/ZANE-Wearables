package com.example.zanewearableswatch;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.model.LatLng;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LocationService extends Service {
    public static double latitude = 0.0;
    public static double longitude = 0.0;
    Location location = new Location("location");
    ArrayList<Step> steps = new ArrayList<Step>();
    FusedLocationProviderClient flpc;
    LocationCallback locationCallback;

    @Override
    public void onCreate() {
        super.onCreate();
        //get current location every 5 seconds
        flpc = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                latitude = locationResult.getLastLocation().getLatitude();
                longitude = locationResult.getLastLocation().getLongitude();
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                Log.d("Latitude, Longitude", String.valueOf(latitude) + String.valueOf(longitude));

                if (steps.size() > 0) { //if steps is populated
                    Location target = new Location("target");
                    target.setLatitude((steps.get(0).getStart()).lat);
                    target.setLongitude((steps.get(0).getStart()).lng);
                    if(location.distanceTo(target) < 25) { //25 meters
                        Log.d(String.valueOf(getDirection(steps.get(0).getManeuver())), String.valueOf(latitude) + String.valueOf(longitude));
                        steps.remove(0);
                    }
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //process steps json and turn it back to arraylist
        Type type = new TypeToken<List<Step>>() {}.getType();
        steps = new Gson().fromJson(intent.getStringExtra("steps"), type);
        for (int i = 0; i < steps.size(); i++) { //debug
            Log.d("service", steps.get(i).toString());
        }

        requestLocation(); //start requesting location every 5 seconds
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private char getDirection(String maneuver) {
        if (maneuver.endsWith("LEFT")) return 'l';
        if (maneuver.endsWith("RIGHT")) return 'r';
        else return 'f';
    }

    private void requestLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        flpc.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }
}
