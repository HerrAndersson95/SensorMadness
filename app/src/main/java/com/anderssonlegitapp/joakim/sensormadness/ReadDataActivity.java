package com.anderssonlegitapp.joakim.sensormadness;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import static android.hardware.SensorManager.getRotationMatrix;

public class ReadDataActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sm;
    private Sensor magn;
    private Sensor accel;
    private Sensor temp;

    private float[] accelValues;
    private float[] magnValues;
    private float[] tempValues;

    private TextView azimuthData;
    private int counter = 0;
    private ImageView arrow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_data);
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);   //skapa manager för sensorer
        magn = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD); //Magnet fält
        accel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); //accelerometer
        temp = sm.getDefaultSensor(Sensor.TYPE_TEMPERATURE);

        azimuthData = (TextView)findViewById(R.id.AzimuthData);
        arrow = (ImageView)findViewById(R.id.Arrow);
    }
    protected void onResume() {
        super.onResume();
        sm.registerListener(this, magn, SensorManager.SENSOR_DELAY_GAME);   //registrerar sensorerna
        sm.registerListener(this, accel, SensorManager.SENSOR_DELAY_GAME);
        sm.registerListener(this, temp, SensorManager.SENSOR_DELAY_GAME);
    }
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){    //Triggered by event so we can se what sensor did it to store its value
            accelValues = event.values;
        }
        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){    //Triggered by event so we can se what sensor did it to store its value
            magnValues = event.values;
        }
        if(event.sensor.getType() == Sensor.TYPE_TEMPERATURE){
            tempValues = event.values;
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
                    doDrawCompass(azimuthInDegress);
                    counter = 0;
                }
            }
        }
        if(tempValues != null){
            System.out.println("HIT TEMP");
            doDrawTemp(tempValues[0]);
        }
    }

    private void doDrawCompass(float degree){
        azimuthData.setText(Integer.toString((int) degree)); //We take the azimuth value from the orientation results.
        arrow.setRotation((float)(((int) degree) * -1));
    }

    private void doDrawTemp(float tmp){
        System.out.println(Float.toString(tmp));
    }

    private float radToDegree(float rad){
        float degrees = (float)Math.toDegrees(rad);
        if (degrees < 0.0f) {
            degrees += 360.0f;
        }
        return degrees;
    }
}
