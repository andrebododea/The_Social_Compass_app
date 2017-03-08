package com.example.s1350924.es_assignment_1;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

/**
 * Created by andrebododea on 3/8/17.
 */

public class CameraActivity extends Activity {


    static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    private Uri fileUri;

    // Camera button
    FloatingActionButton fab;


    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type){
        final File mediaStorageDir;

        // If external media storage (SD Card) is mounted
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"myWeeCameraApp");
        }else{
            mediaStorageDir = new File("/storage/sdcard0/myWeeCameraApp/");
        }

        // If the storage director doesn't exits
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("myWeeCameraApp", "failed to create directory"); // Sends a DEBUG log message
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File mediaFile = null;
        if(type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+timeStamp+".jpg");
        }else{
            return null;
        }
        return mediaFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                // Image captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Image successfully saved", Toast.LENGTH_LONG).show();

                // Offer to share with social media
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                shareIntent.setType("image/*");
                startActivity(Intent.createChooser(shareIntent, "Share images to.."));

            } else if(resultCode == RESULT_CANCELED){
                // User canceled the image capture
                Toast.makeText(this, "Image capture canceled", Toast.LENGTH_LONG).show();
            } else{
                // Image capture failed, advise user
                Toast.makeText(this, "Image capture failed, try again", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam);

        // Set camera button
        fab = (FloatingActionButton) findViewById(R.id.fab);

        // When camera button is clicked, this code is run
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Have the camera application capture an image and return it
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // Start the camera intent
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

                // Create the Uri of a file to save the image
                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

                // Specify the path and file name of the received image
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            }
        });
    }

    private void getGPScoords() {
      /*
    LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

    // Check if we have the necessary permissions
    if (ContextCompat.checkSelfPermission(CameraActivity.this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        LocationListener locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

    }
    */

    }

}
