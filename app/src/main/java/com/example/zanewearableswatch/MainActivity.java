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
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText destination = null;
    String mode = "";
    String resp = "";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton bikeButton = findViewById(R.id.bikeButton);
        ImageButton walkButton = findViewById(R.id.walkButton);
        ImageButton carButton = findViewById(R.id.carButton);
        String origin = ""; //find a way to get the user's current location
        String key = "AIzaSyAUkJ-ObhbadMLikRQZ3i2_L79WB7Fug3Q"; //add api key

        String testDestination = "\"ChIJyYB_SZVU2YARR-I1Jjf08F0 2920 Zoo Dr, San Diego, CA 92101, USA\"";
        String testOrigin = "ChIJE9on3F3HwoAR9AhGJW_fL-I\n Los Angeles, CA, USA";

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
                //set destination and mode
                destination  = (EditText)findViewById(R.id.destination);
                String destinationText = destination.getText().toString();

                if (destinationText.equals("") || mode == "") {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Error")
                            .setMessage("Destination or mode has not been set!")

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton("OK", null)
                            .show();
                }

                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                //String request = "google.navigation:q=" + destinationText + "&mode=" + mode;
                //for now, destination is just the textfield, but it should be coordinates
                String url = "https://maps.googleapis.com/maps/api/directions/json?"+ "origin="
                        + testOrigin + "&destination=" + testDestination
                        + "&mode=" + mode + "&key=" + key;

                //probably don't need this
                /*JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("google.destination.q", destinationText);
                    jsonObject.put("&mode", mode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                 */


                //System.out.println(request); //for testing
                //Toast.makeText(MainActivity.this, url, Toast.LENGTH_LONG).show();
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,  new ResponseListener(), new ErrorListener());
                ArrayList<Bundle> bundleList = parse(resp);

                for (Bundle bundle : bundleList)
                {
                    for (String key: bundle.keySet())
                    {
                        Log.d("Bundle Debug", key + " = \"" + bundle.get(key) + "\"");
                    }
                }


                queue.add(request);
                /* this code probably isn't what we're looking for, it just opens a separate map.
                 * including in case its useful
                Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("google.navigation:q=0,0&mode=d"));
                intent.setPackage("com.google.android.apps.maps");

                //google maps downloaded
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                else { //google maps not downloaded
                    new AlertDialog.Builder(this)
                            .setTitle("Error")
                            .setMessage("Application requires Google Maps to be downloaded on the device.")

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }*/
            }
        });
    }
    public ArrayList<Bundle> parse(String response){
        ArrayList<Bundle> list = new ArrayList<Bundle>();
        try {
            JSONObject json = new JSONObject(response);
            JSONArray routes = json.getJSONArray("route");
            JSONArray legs = routes.getJSONArray(0);
            JSONArray steps = legs.getJSONArray(0);
            for(int i=0;i<steps.length();i++) {
                JSONObject singleStep = steps.getJSONObject(i);
                JSONObject duration = singleStep.getJSONObject("duration");
                Bundle dur = new Bundle();
                dur.putString("text", duration.getString("text"));
                dur.putString("value", duration.getString("value"));
                JSONObject distance = singleStep.getJSONObject("distance");
                Bundle dis = new Bundle();
                dis.putString("text", distance.getString("text"));
                dis.putString("value", distance.getString("value"));
                Bundle data = new Bundle();
                data.putBundle("duration", dur);
                data.putBundle("distance", dis);
                list.add(data);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return list;
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
        public void onResponse(JSONObject response) {
            Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG).show();
            destination.setText(response.toString());
            resp = response.toString();
            System.out.println(response.toString());
        }
    }

    private class ErrorListener implements Response.ErrorListener{
        @Override
        public void onErrorResponse(VolleyError error) {

            Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}