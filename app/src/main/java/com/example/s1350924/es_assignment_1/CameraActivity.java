package com.example.s1350924.es_assignment_1;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

/**
 * Created by andrebododea on 3/8/17.
 */

public class CameraActivity extends Activity {


    static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    public Uri fileUri;

    // Camera button
    FloatingActionButton fab;


    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type){
        final File mediaStorageDir;

        //File storage_for_pic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File storage_for_pic = new File("/storage/sdcard0/DCIM/Camera/");
        System.out.println(storage_for_pic.toString());

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());


        File mediaFile = null;
        if(type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(storage_for_pic,"IMG_"+timeStamp+".jpg");
        }else{
            return null;
        }
        return mediaFile;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                // Image captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Image successfully saved", Toast.LENGTH_LONG).show();

                // Overlay photo with GPS and direction data

                try {
                    overlayPhoto();
                }catch(IOException ex){
                    Toast.makeText(this, "Could not overlay photo data: "+ex.toString(), Toast.LENGTH_LONG).show();
                }

                // Offer to share with social media
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);

                File myf = getNewestFileInDirectory();
                Uri picUri = Uri.fromFile(myf);
                shareIntent.putExtra(Intent.EXTRA_STREAM, picUri);
                shareIntent.setType("image/*");

                // Grant permissions to all apps that can handle this intent.
                List<ResolveInfo> resInfoList = (CameraActivity.this).getPackageManager().queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    System.out.println("Package name: " + packageName);
                    (CameraActivity.this).grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                // Share the intent with all available apps
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


    // Overlay GPS coordinates and compass direction on photo
    private void overlayPhoto()throws IOException {
        // Convert photo to Bitmap
        File myf = getNewestFileInDirectory();
        Uri picUri = Uri.fromFile(myf);
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);


        Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(55);

        Rect textRect = new Rect();

        // Get the GPS coordinates
        String myCoords = GPS_coords();
        myCoords = myCoords+", Direction: North";

        paint.getTextBounds(myCoords, 0, myCoords.length(), textRect);

        // set default bitmap config if none
        android.graphics.Bitmap.Config bitmapConfig =
                bitmap.getConfig();
        if(bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable, so need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);

        //Calculate the positions
        int x = (canvas.getWidth() / 2) - 2;     //-2 is for regulating the x position offset

        //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
       // int y = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) ;
        int y = (int) ((canvas.getHeight())-(canvas.getHeight()/10)) ;


        canvas.drawText(myCoords, x, y, paint);

        // Point to the file with the original photo, which I will overwrite with the bitmap data
        // File f = new File(fileUri.getPath());
        File f = getNewestFileInDirectory();

        //Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bitmapdata = bos.toByteArray();

        //write the bytes in file
        try {
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        }catch(IOException ex){
            System.out.println (ex.toString());
            System.out.println("Could not find file");
        }

    }

    private String GPS_coords() {
            // Instantiate the location manager
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        // Check if have the necessary permissions (Fine location for GPS)
        if (ContextCompat.checkSelfPermission(CameraActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Instantiate a CurrentLocationListener
            CurrentLocationListener locationListener = new CurrentLocationListener();

            // Passes the current location to the CurrentLocationListener function
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,locationListener);

            double mLat= locationListener.getCurrentLatitude();
            double mLong= locationListener.getCurrentLatitude();

            if((mLat != 0 && mLong != 0)) {
                // Round to 3 decimal places
                String latlong = "Latitude: " + String.format("%.3f", mLat) + ", Longitude: " + String.format("%.3f", mLong);
                return latlong;
            }else{
                // Get last known location from the network provider if could not get GPS coordinates
                Location mobileLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                // Round coordinates to 3 decimal places
                String latlong = "Latitude: "+ String.format("%.3f", mobileLocation.getLatitude()) + ", Longitude: " + String.format("%.3f", mobileLocation.getLongitude());
                return latlong;
            }
        }
        return "";
    }

    // Loop through all files in DCIM/Camera to find the latest file
    public File getNewestFileInDirectory() {
        File latestPhoto = null;

        // start loop trough files in directory
        File file = new File("/storage/sdcard0/DCIM/Camera/");

            // Loops through all files in the Camera directory
        for(final File child : file.listFiles()) {
            if(latestPhoto == null){
                latestPhoto = child;
            } else if ((file.lastModified() > latestPhoto.lastModified()) || latestPhoto == null ) {
                latestPhoto = child;
            }
        }

        return latestPhoto;
    }
}
