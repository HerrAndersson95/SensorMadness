package com.anderssonlegitapp.joakim.sensormadness;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import static android.hardware.SensorManager.getRotationMatrix;

public class CompassActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sm;
    private Sensor magn;
    private Sensor accel;

    private float[] accelValues;
    private float[] magnValues;

    private TextView azimuthData;
    private ImageView arrow;
    private int counter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);   //skapa manager för sensorer
        magn = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD); //Magnet fält
        accel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); //accelerometer

        azimuthData = (TextView)findViewById(R.id.AzimuthData);
        arrow = (ImageView)findViewById(R.id.Arrow);
    }

    //Tab in
    protected void onResume() {
        super.onResume();
        sm.registerListener(this, magn, SensorManager.SENSOR_DELAY_GAME);   //registrerar sensorerna
        sm.registerListener(this, accel, SensorManager.SENSOR_DELAY_GAME);
    }

    //Tab ut
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }

    /*
     Called when the accuracy of the registered sensor has changed.
     Unlike onSensorChanged(), this is only called when this accuracy value changes.
     */
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /*
    Called when there is a new sensor event.
    Note that "on changed" is somewhat of a misnomer,
    as this will also be called if we have a new reading from a sensor with the exact same sensor values (but a newer timestamp).
     */
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){    //Triggered by event so we can se what sensor did it to store its value
            accelValues = event.values;
        }
        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){    //Triggered by event so we can se what sensor did it to store its value
            magnValues = event.values;
        }

        if(accelValues != null && magnValues != null) {
            float Rotation[] = new float[9]; //Needed for getRotationMatrix
            float Inclination[] = new float[9];
            boolean completed = getRotationMatrix(Rotation, Inclination, accelValues, magnValues);  //Will be true if values are ok

            if(completed){
                counter++;
                float[] orientation = new float[3];
                sm.getOrientation(Rotation, orientation); //Pass calculations into float orientation

                float azimuthInRadians = orientation[0];
                //System.out.println(azimuthInRadians);
                float azimuthInDegress = radToDegree(azimuthInRadians);
                if(counter >= 10){
                    doDraw(azimuthInDegress);
                    counter = 0;
                }
            }
            /*
                VÄRDEN FÖR ORIENTATION
                values[0]: Azimuth, When facing north, this angle is 0,
                    south, this angle is π.
                    east, this angle is π/2
                    west, this angle is -π/2.
                    The range of values is -π to π.
                values[1]: Pitch, angle of rotation about the x axis.
                    This value represents the angle between a plane parallel to the device's screen and a plane parallel to the ground.
                    Assuming that the bottom edge of the device faces the user and that the screen is face-up,
                    tilting the top edge of the device toward the ground creates a positive pitch angle.
                    The range of values is -π to π.
                values[2]: Roll, angle of rotation about the y axis.
                    This value represents the angle between a plane perpendicular to the device's screen and a plane perpendicular to the ground.
                    Assuming that the bottom edge of the device faces the user and that the screen is face-up,
                    tilting the left edge of the device toward the ground creates a positive roll angle.
                    The range of values is -π/2 to π/2.
             */
        }
    }

    private void doDraw(float degree){
        System.out.println(Float.toString(degree));
        azimuthData.setText(Integer.toString((int) degree)); //We take the azimuth value from the orientation results.
        arrow.setRotation((float)(((int) degree) * -1));
    }

    private float radToDegree(float rad){
        float degrees = (float)Math.toDegrees(rad);
        if (degrees < 0.0f) {
            degrees += 360.0f;
        }
        return degrees;
    }

}
