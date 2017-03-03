package com.example.s1350924.es_assignment_1;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    //get access to sensor
    private Sensor mMagneticField;

    private Sensor aAccelerometer;

    // Set TextView for sensor value
    private TextView mag_x;
    private TextView mag_y;


    private float x,y,z;
    private double h;

    private float magneticFieldValues[] = new float[3];
    private float accelerometerValues[] = new float[3];

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE=20;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION=30;


    @Override
    public void onSensorChanged(SensorEvent event){

        // Calculate the total magnetic field
        // h = Math.sqrt(event.values[0]*event.values[0]+event.values[1]*
        // event.values[1]+event.values[2]*event.values[2]);

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            // read sensor called from SensorEvent and save them to accelerometer values
            accelerometerValues[0] = event.values[0];
            accelerometerValues[1] = event.values[1];
            accelerometerValues[2] = event.values[2];
        }

        // Set the string value from sensor to text
        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            // read sensor called from SensorEvent and save them to magnetic field values
            magneticFieldValues[0] = event.values[0];
            magneticFieldValues[1] = event.values[1];
            magneticFieldValues[2] = event.values[2];
        }

        calculateOrientation();
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    // When activity is resumed, listen to sensor
    protected void onResume(){
        super.onResume();
        mSensorManager.registerListener(this, mMagneticField, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, aAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    // When activity is paused, stop listening to the sensor
    protected void onPause(){
        super.onPause();
        // Disable the sensor
        mSensorManager.unregisterListener(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissionsBox();

        initialisation();
        calculateOrientation();

        //Get an instance of SensorManager for accessing sensors
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        // Determine a default sensor type, in this case it is magnetometer
        mMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        aAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


    }

    private void initialisation() {
        /* need to do initialization for text view (hint: call findViewById()) and call the sensors
        (aSensor and mSensor) when the activity starts. */

        // Find view from layout file
        mag_x = (TextView)findViewById(R.id.Xaxis);
        mag_y = (TextView)findViewById(R.id.Yaxis);


        // private Sensor mMagneticField;
        // private Sensor aAccelerometer;

    }


    private void calculateOrientation() {
        /* Under the method of calculateOrientation(), just as its name, data from both sensors will be
        calculated and converted to the degree by which your phone is rotated.
        The float variable named “degree” ranges within (-180° – 180°). This means when degree is
        0°, you are facing north. Finish the rest of the code to complete calculateOrientation() method. */

        float[] values = new float[3];
        float[] R = new float[9];
        SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);
        SensorManager.getOrientation(R, values);

        float degree = (float) Math.toDegrees(values[0]);
        // values[1] = (float) Math.toDegrees(values[1]);
        // values[2] = (float) Math.toDegrees(values[2]);

        double ddm = 22.5/2;
        if(degree > -1*ddm && degree <= 1*ddm){
            mag_x.setText("At " + (int)degree + " degrees (North).");
        }else if(degree > 1*ddm && degree <= 3*ddm){
            mag_x.setText("At " + (int)degree + " degrees (North Northeast).");
        }else if(degree > 3*ddm && degree <= 5*ddm){
            mag_x.setText("At " + (int)degree + " degrees (Northeast).");
        }else if(degree > 5*ddm && degree <= 7*ddm){
            mag_x.setText("At " + (int)degree + " degrees (East Northeast).");
        }else if(degree > 7*ddm && degree <= 9*ddm){
            mag_x.setText("At " + (int)degree + " degrees (East).");
        }else if(degree > 9*ddm && degree <= 11*ddm){
            mag_x.setText("At " + (int)degree + " degrees (East Southeast).");
        }else if(degree > 11*ddm && degree <= 13*ddm){
            mag_x.setText("At " + (int)degree + " degrees (Southeast).");
        }else if(degree > 13*ddm && degree <= 15*ddm){
            mag_x.setText("At " + (int)degree + " degrees (South Southeast).");
        }else if(degree > 15*ddm && degree <= 17*ddm){
            mag_x.setText("At " + (int)degree + " degrees (South).");
        }else if(degree < -13*ddm && degree >= -15*ddm){
            mag_x.setText("At " + (int)degree + " degrees (South Southwest).");
        }else if(degree < -11*ddm && degree >= -13*ddm){
            mag_x.setText("At " + (int)degree + " degrees (Southwest).");
        }else if(degree < -9*ddm && degree >= -11*ddm){
            mag_x.setText("At " + (int)degree + " degrees (West Southwest).");
        }else if(degree < -7*ddm && degree >= -9*ddm){
            mag_x.setText("At " + (int)degree + " degrees (West).");
        }else if(degree < -5*ddm && degree >= -7*ddm){
            mag_x.setText("At " + (int)degree + " degrees (West Northwest).");
        }else if(degree < -3*ddm && degree >= -5*ddm){
            mag_x.setText("At " + (int)degree + " degrees (Northwest).");
        }else if(degree < -1*ddm && degree >= -5*ddm){
            mag_x.setText("At " + (int)degree + " degrees (North Northwest).");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


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

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    @Override
    // Is called automatically by ActivityCompat.requestPermissions() in the requestPermissionsBox()
    // function above
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the task you need to do.


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.



                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the task you need to do.


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.


                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
