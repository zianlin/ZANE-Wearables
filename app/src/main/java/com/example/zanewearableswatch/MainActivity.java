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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    EditText destination = null;
    String mode = "";

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

                //for now, destination is just the textfield, but it should be coordinates
                String request = "google.navigation:q=" + destinationText + "&mode=" + mode;
                System.out.println(request); //for testing

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
}