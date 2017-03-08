package com.example.s1350924.es_assignment_1;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    // Both variables for requesting permissions
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE=20;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION=30;

    //Defining view
    private Button compButton;
    private Button camButton;


    // Eventually set up a toolbar for the app

    // When activity is resumed, listen to sensor
    protected void onResume(){
        super.onResume();
    }

    // When activity is paused, stop listening to the sensor
    protected void onPause(){
        super.onPause();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check and request permissions from user
        requestPermissionsBox();

        //Find your views
        compButton = (Button)findViewById(R.id.compass);
        camButton = (Button)findViewById(R.id.camera);



        //Assign a listener to compass button
        compButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start your second activity
                Intent intent = new Intent(MainActivity.this, CompassActivity.class);
                startActivity(intent);
            }
        });


        //Assign a listener to camera button
        camButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start your second activity
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });
    }


    // Checks permissions and allows the user to enable permissions
    public void requestPermissionsBox() {
        // Here, MainActivity.this is the current activity
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Request the permission.
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Request the permission.
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an app-defined int constant.
            // The callback method gets the result of the request.
        }
    }

    @Override
    // Is called automatically by ActivityCompat.requestPermissions() in the
    // requestPermissionsBox() function above
    public void onRequestPermissionsResult(int reqCode,
                                           String permissions[], int[] grantResults) {
        switch (reqCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the task you need to do.
                } else {

                    // permission denied
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                } else {
                    // permission denied
                }
                return;
            }
            // other 'case' lines to check for other permissions this app might request
        }
    }

}
