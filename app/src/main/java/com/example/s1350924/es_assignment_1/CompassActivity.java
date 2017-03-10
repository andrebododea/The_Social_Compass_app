package com.example.s1350924.es_assignment_1;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class CompassActivity extends Activity implements SensorEventListener {

    private Location currentLocation;
    private GeomagneticField geomagneticField;

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

    // record the compass picture angle turned
    private float currentDegree = 0f;

    // Compass image
    private ImageView compass_image;




    @Override
    public void onSensorChanged(SensorEvent event){

        // Calculate the total magnetic field
        // h = Math.sqrt(event.values[0]*event.values[0]+event.values[1]*
        // event.values[1]+event.values[2]*event.values[2]);

        final float alpha = 0.9f;

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // read sensor called from SensorEvent and save them to accelerometer values
            accelerometerValues[0] = alpha * accelerometerValues[0] + (1 - alpha) * event.values[0];
            accelerometerValues[1] = alpha * accelerometerValues[1] + (1 - alpha) * event.values[1];
            accelerometerValues[2] = alpha * accelerometerValues[2] + (1 - alpha) * event.values[2];
        }

        // Set the string value from sensor to text
        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            // read sensor called from SensorEvent and save them to magnetic field values
            magneticFieldValues[0] = alpha * magneticFieldValues[0] + (1 - alpha) * event.values[0];
            magneticFieldValues[1] = alpha * magneticFieldValues[1] + (1 - alpha) * event.values[1];
            magneticFieldValues[2] = alpha * magneticFieldValues[2] + (1 - alpha) * event.values[2];
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
        // Disable the sensor and save battery
        mSensorManager.unregisterListener(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comp);

        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        // Check if have the necessary permissions (Fine location for GPS)
        if (ContextCompat.checkSelfPermission(CompassActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Instantiate a CurrentLocationListener
            CurrentLocationListener locationListener = new CurrentLocationListener();

            // Passes the current location to the CurrentLocationListener function
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            double mLat = locationListener.getCurrentLatitude();
            double mLong = locationListener.getCurrentLatitude();

            // try with network provider
            Location networkLocation = locationManager
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (networkLocation != null) {
                currentLocation = networkLocation;
            }

            geomagneticField = new GeomagneticField(
                    (float) currentLocation.getLatitude(),
                    (float) currentLocation.getLongitude(),
                    (float) currentLocation.getAltitude(),
                    System.currentTimeMillis());
        }

        initialisation();
        calculateOrientation();
    }

    private void initialisation() {
        /* need to do initialization for text view (hint: call findViewById()) and call the sensors
        (aSensor and mSensor) when the activity starts. */

        // Find view from content_comp.xml, and set it equal to the int mag_x
        mag_x = (TextView)findViewById(R.id.Xaxis);

        // Set compass_image to the ImageView element imageViewCompass in content_comp.xml
        compass_image = (ImageView) findViewById(R.id.imageViewCompass);

        //Get an instance of SensorManager for accessing sensors
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        // Determine a default sensor type, in this case it is magnetometer
        mMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        aAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }


    private void calculateOrientation() {
        /* Under the method of calculateOrientation(), just as its name, data from both sensors will be
        calculated and converted to the degree by which your phone is rotated.
        The float variable named “degree” ranges within (-180° – 180°). This means when degree is
        0°, you are facing north. Finish the rest of the code to complete calculateOrientation() method. */


        float[] values = new float[3];
        float[] R = new float[9];
        boolean success = SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);


        // Prevents updating the values if the device is in free fall.
        // Free fall is defined as condition when the magnitude of the gravity
        // is less than 1/10 of the nominal value. On failure the output matrices are not modified
        if(success) {
            // Places degrees from -180 to +180 into the array values
            SensorManager.getOrientation(R, values);

            // values[0] holds azimuth, rotation about the z-axis.
            float degree = (float) (Math.toDegrees(values[0]));

            // fix difference between true North and magnetical North
            if (geomagneticField != null) {
                degree += geomagneticField.getDeclination();
            }

            if (degree < 0) {
                degree += 360;
            }

            // create a rotation animation (reverse turn degree degrees)
            RotateAnimation ra = new RotateAnimation(
                    currentDegree, -degree,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
            );

            // I want each animation to take more time if it's a long distance, and less time
            // for a short distance.
            // Very short aimations should take little time, thus setting .3 ms per degree seems reasonable
            // degrees * .3 ms = time of animation
            float distance_in_degrees = Math.round(Math.abs(Math.abs(currentDegree) - Math.abs(degree)));
            int timeOfAnimation = (int)(distance_in_degrees*.3);

            ra.setDuration(30);   // how long the animation will take place

            ra.setRepeatCount(0);

            // set the animation after the end of the reservation status
            ra.setFillAfter(true);

            // Start the animation
            compass_image.startAnimation(ra);
            currentDegree = -degree;


            mag_x.setText("At " + Math.round(degree) + " degrees.");
            /*
            double ddm = 22.5 / 2;

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
            }else if(degree < -1*ddm && degree >= -3*ddm){
                mag_x.setText("At " + (int)degree + " degrees (North Northwest).");
            }
            */

        }
    }
}
