package com.example.zanewearableswatch;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    String origin = ""; //find a way to get the user's current location
    String destination = "";
    String key = "AIzaSyAUkJ-ObhbadMLikRQZ3i2_L79WB7Fug3Q"; //add api key
    String mode = "";
    ArrayList<Step> steps = new ArrayList<Step>();

    /*
    //current lat and long, Work in progress
    LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    double longitude = location.getLongitude();
    double latitude = location.getLatitude();


    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }
    };*/

    //POJO for step data parsed from call to Directions API
    private class Step {
        private String instruction;
        private String maneuver;
        private int distance;
        private int duration;
        private LatLng startLocation;
        private LatLng endLocation;

        public Step() {
            instruction = "";
            maneuver = "";
            distance = 0;
            duration = 0;
            startLocation = null;
            endLocation = null;
        }

        public Step(String ins, String man, int dis, int dur, LatLng start, LatLng end) {
            instruction = ins;
            maneuver = man;
            distance = dis;
            duration = dur;
            startLocation = start;
            endLocation = end;
        }

        public String getInstruction()  {return instruction;}
        public String getManeuver()     {return maneuver;}
        public int getDistance()        {return distance;}
        public int getDuration()        {return duration;}
        public LatLng getStart()        {return startLocation;}
        public LatLng getEnd()          {return endLocation;}

        public void setInstruction(String ins)  {instruction = ins;}
        public void setManeuver(String man)     {maneuver = man;}
        public void setDistance(int dis)        {distance = dis;}
        public void setDuration(int dur)        {duration = dur;}
        public void setStart(LatLng start)      {startLocation = start;}
        public void setEnd(LatLng end)          {endLocation = end;}

        public String toString() {
            String[] arr = {instruction, maneuver, String.valueOf(distance), String.valueOf(distance),
                            startLocation.toString(), endLocation.toString()};
            return Arrays.stream(arr).collect(Collectors.joining(", "));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton bikeButton = findViewById(R.id.bikeButton);
        ImageButton walkButton = findViewById(R.id.walkButton);
        ImageButton carButton = findViewById(R.id.carButton);

        String testOrigin = "\"ChIJyYB_SZVU2YARR-I1Jjf08F0 2920 Zoo Dr, San Diego, CA 92101, USA\"";

        bikeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mode = "b";
            }
        });
        walkButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mode = "w";
            }
        });
        carButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mode = "d";
            }
        });

        Button navigateButton = findViewById(R.id.navigate);
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                String url = "https://maps.googleapis.com/maps/api/directions/json?"+ "origin="
                        + testOrigin + "&destination=" + destination
                        + "&mode=" + mode + "&key=" + key;

                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null, new ResponseListener(), new ErrorListener());
                queue.add(jsonRequest);
            }
        });
    }

    /*
    public String printJSONArray (JSONArray jArr) {
        for(int i = 0; i < jArr.length();i++) {
            JSONObject innerObj = jArr.getJSONObject(i);
            for(Iterator it = innerObj.keys(); it.hasNext(); ) {
                String key = (String)it.next();
                System.out.println(key + ":" + innerObj.get(key));
            }
        }
    }*/



    private class ResponseListener implements Response.Listener<JSONObject>{
        @Override
        public void onResponse(JSONObject json) {
            Log.d("Response is: ", "successful");
            String responseStr = json.toString();
            /*
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            map = gson.fromJson(json.toString(), new TypeToken<HashMap<String, Object>>() {}.getType());
            //System.out.println(gson.toJson(json));
            System.out.println("Starting gson");

            JSONArray jsonArray = json.getJSONArray("response");

            int[] myArray = new Gson().fromJson(jsonArray, int[].class);

            System.out.println(Arrays.toString(myArray));*/

            /*
            Gson gson = new Gson();
            MapCall mapCall = gson.fromJson(responseStr, MapCall.class);
            System.out.println(mapCall.status);
            */

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
                for (int i = 0; i < steps.size(); i++) { //debug
                    Log.d(String.valueOf(i), steps.get(i).toString());
                }
            }
            catch (JSONException e) {
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