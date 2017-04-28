package com.anderssonlegitapp.joakim.sensormadness;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import static android.support.v4.app.ActivityCompat.requestPermissions;

public class GPSActivity2 extends AppCompatActivity {
    private TextView latitude;
    private TextView longitude;
    private TextView speed;
    protected static final int MY_PERMISSIONS_REQUEST_GPS_FINE = 1;
    public static String str_receiver = "GPSserviceTAG";
    private Vibrator vib;

    private Location currentLocation;
    private Location oldCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps2);
        latitude = (TextView) findViewById(R.id.txt_lat);
        longitude = (TextView) findViewById(R.id.txt_long);
        speed = (TextView) findViewById(R.id.txt_spd);
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (hasPermissions()){
            // our app has permissions.
            System.out.println("OK GPS SETUP COMPLETED");
            startService(new Intent(this, GPSservice.class));
        }
        else {
            //our app doesn't have permissions, So i m requesting permissions.
            requestPerms();
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = (Bundle) intent.getBundleExtra("Location");
            if(oldCurrentLocation == null){
                oldCurrentLocation = b.getParcelable("Location");
                currentLocation = b.getParcelable("Location");
            }
            oldCurrentLocation = currentLocation;
            currentLocation = b.getParcelable("Location");

            latitude.setText(Double.toString(currentLocation.getLatitude()));
            longitude.setText(Double.toString(currentLocation.getLongitude()));
            speed.setText(Double.toString(currentLocation.distanceTo(oldCurrentLocation)));
            vib.vibrate(60);
            System.out.println("Recieved GPSDATA to Activity");
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(GPSActivity2.str_receiver));

    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private boolean hasPermissions(){
        int res = 0;
        //string array of permissions,
        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        for (String perms : permissions){
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)){
                System.out.println("PERM NOT GRANTED GRANTED");
                return false;
            }
        }
        System.out.println("PERM ALREADY GRANTED GRANTED");
        return true;
    }
    private void requestPerms(){
        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(permissions,MY_PERMISSIONS_REQUEST_GPS_FINE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_GPS_FINE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    System.out.println("OK PERM GRANTED");

                    System.out.println("OK GPS SETUP COMPLETED");

                } else {
                    System.out.println("No Permission given");
                    finish();
                }
            }
        }
    }
}
