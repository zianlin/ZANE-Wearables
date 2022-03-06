package com.example.zanewearableswatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.maps.model.Distance;
import com.google.maps.model.Duration;
import com.google.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    String origin = ""; //find a way to get the user's current location
    String destination = "";
    String key = "AIzaSyAUkJ-ObhbadMLikRQZ3i2_L79WB7Fug3Q"; //add api key
    String mode = "";
    ArrayList<Step> steps = new ArrayList<Step>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //start service that continuously gets lat and long of user
        if ((Build.VERSION.SDK_INT >= 23) && (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else {
            startService();
        }

        ImageButton bikeButton = findViewById(R.id.bikeButton);
        ImageButton walkButton = findViewById(R.id.walkButton);
        ImageButton carButton = findViewById(R.id.carButton);

        bikeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("User selects", "biking");
                mode = "b";
                bikeButton.setImageResource(R.drawable.bikeon);
                walkButton.setImageResource(R.drawable.walkoff);
                carButton.setImageResource(R.drawable.caroff);
            }
        });
        walkButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("User selects", "walking");
                mode = "w";
                bikeButton.setImageResource(R.drawable.bikeoff);
                walkButton.setImageResource(R.drawable.walkon);
                carButton.setImageResource(R.drawable.caroff);
            }
        });
        carButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("User selects", "driving");
                mode = "d";
                bikeButton.setImageResource(R.drawable.bikeoff);
                walkButton.setImageResource(R.drawable.walkoff);
                carButton.setImageResource(R.drawable.caron);
            }
        });

        Button navigateButton = findViewById(R.id.navigate);
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                origin = LocationService.latitude + "," + LocationService.longitude;
                Log.d("Navigation start: origin is ", origin);
                destination = ((EditText)findViewById(R.id.destination)).getText().toString();

                if (destination.equals("") || mode == "") {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Error")
                            .setMessage("Destination or mode has not been set!")
                            .setNegativeButton("OK", null)
                            .show();
                    return;
                }

                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                //String request = "google.navigation:q=" + destinationText + "&mode=" + mode;
                //for now, destination is just the textfield, but it should be coordinates
                String url = "https://maps.googleapis.com/maps/api/directions/json?"
                        + "origin=" + origin + "&destination=" + destination
                        + "&mode=" + mode + "&key=" + key;

                Log.d("url", url);
                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null, new ResponseListener(), new ErrorListener());
                queue.add(jsonRequest);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startService();
                }
                else {
                    Toast.makeText(this, "App requires location permissions", Toast.LENGTH_LONG).show();
                }
        }
    }

    void startService() {
        Intent intent = new Intent(MainActivity.this, LocationService.class);
        intent.putExtra("steps", new Gson().toJson(steps));
        startService(intent);
    }

    private class ResponseListener implements Response.Listener<JSONObject>{
        @Override
        public void onResponse(JSONObject json) {
            Log.d("Response is: ", "successful");

            try {
                JSONArray routesArr = json.getJSONArray("routes");
                JSONObject routesObj = routesArr.getJSONObject(0);
                JSONArray legsArr = routesObj.getJSONArray("legs");
                JSONObject legsObj = legsArr.getJSONObject(0);
                JSONArray stepsArr = legsObj.getJSONArray("steps");

                for (int i = 0; i < stepsArr.length(); i++) {
                    Step step = new Step();
                    JSONObject stepObj = stepsArr.getJSONObject(i);

                    String instruction = stepObj.getString("html_instructions");
                    String maneuver = stepObj.has("maneuver") ? stepObj.getString("maneuver") : "";
                    int distance = stepObj.getJSONObject("distance").getInt("value");
                    int duration = stepObj.getJSONObject("duration").getInt("value");
                    JSONObject start = stepObj.getJSONObject("start_location");
                    JSONObject end = stepObj.getJSONObject("end_location");

                    step.setInstruction(instruction);
                    step.setManeuver(maneuver);
                    step.setDistance(distance);
                    step.setDuration(duration);
                    step.setStart(new LatLng(start.getDouble("lat"),
                            start.getDouble("lng")));
                    step.setEnd(new LatLng(end.getDouble("lat"),
                            end.getDouble("lng")));

                    steps.add(step);
                }
                startService(); //'restart' service, passing in newly filled 'steps' as json
            }
            catch (JSONException e) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Error")
                        .setMessage("Invalid destination!")
                        .setNegativeButton("OK", null)
                        .show();
                e.printStackTrace();
            }
        }
    }

    private class ErrorListener implements Response.ErrorListener{
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("Response is: ", "error");
        }
    }
}