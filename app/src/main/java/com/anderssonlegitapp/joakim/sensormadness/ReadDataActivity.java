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
    private Sensor prox;
    private Sensor light;
    private Sensor press;
    private Sensor magz;

    private float[] accelValues;
    private float[] magnValues;
    private float[] proxValues;
    private float[] eventValues;

    private TextView azimuthData;
    private TextView txt_prox;
    private TextView txt_light;
    private TextView txt_press;
    private TextView txt_magz;
    private int counter = 0;
    private ImageView arrow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_data);
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);   //skapa manager för sensorer
        magn = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD); //Magnet fält
        accel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); //accelerometer
        prox = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        light = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
        press = sm.getDefaultSensor(Sensor.TYPE_PRESSURE);
        magz = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);


        azimuthData = (TextView)findViewById(R.id.AzimuthData);
        arrow = (ImageView)findViewById(R.id.Arrow);
        txt_prox = (TextView) findViewById(R.id.txt_prox);
        txt_light = (TextView) findViewById(R.id.txt_light);
        txt_press = (TextView) findViewById(R.id.txt_press);
        txt_magz = (TextView) findViewById(R.id.txt_magz);
    }
    protected void onResume() {
        super.onResume();
        sm.registerListener(this, magn, SensorManager.SENSOR_DELAY_GAME);   //registrerar sensorerna
        sm.registerListener(this, accel, SensorManager.SENSOR_DELAY_GAME);
        sm.registerListener(this, prox, SensorManager.SENSOR_DELAY_GAME);
        sm.registerListener(this, light, SensorManager.SENSOR_DELAY_GAME);
        sm.registerListener(this, press, SensorManager.SENSOR_DELAY_GAME);
        sm.registerListener(this, magz, SensorManager.SENSOR_DELAY_GAME);
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
        if(event.sensor.getType() == Sensor.TYPE_PROXIMITY){
            proxValues = event.values;
            if(proxValues != null){
                doDraw(proxValues[0], txt_prox);
            }
        }
        if(event.sensor.getType() == Sensor.TYPE_LIGHT){
            eventValues = event.values;
            if(eventValues != null){
                doDraw(eventValues[0], txt_light);
            }
        }
        if(event.sensor.getType() == Sensor.TYPE_PRESSURE){
            eventValues = event.values;
            if(eventValues != null){
                doDraw(eventValues[0], txt_press);
            }
        }
        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED){
            eventValues = event.values;
            if(eventValues != null){
                doDraw(eventValues[2], txt_magz);
            }
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
    }

    private void doDrawCompass(float degree){

        /*
        if(degree > 180.0f){
            degree =  degree - 360.0f;
            arrow.setRotation(degree);
        } else {
            arrow.setRotation(degree * -1.0f);
        }
        */
        System.out.println(degree);
        arrow.setRotation(degree * -1.0f);
        if (degree < 0.0f) {
            degree += 360.0f;
        }
        azimuthData.setText(Integer.toString((int) degree)); //We take the azimuth value from the orientation results.
    }

    private void doDraw(float tmp, TextView t){
        t.setText(Float.toString(tmp));
    }

    private float radToDegree(float rad){
        float degrees = (float)Math.toDegrees(rad);
        /*
        if (degrees < 0.0f) {
            degrees += 360.0f;
        }
        */
        return degrees;
    }
}
