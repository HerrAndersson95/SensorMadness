package com.anderssonlegitapp.joakim.sensormadness;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class AccelerometerActivity extends AppCompatActivity implements SensorEventListener{


    private SensorManager sm;
    private Sensor accel;
    private Sensor linaccel;
    private float[] accelValues;
    private float[] accelValuesSmooth = new float[3];
    private float[] linaccelValuesSmooth = new float[3];
    private TextView xaxis;
    private TextView yaxis;
    private TextView zaxis;
    private TextView xaxisS;
    private TextView yaxisS;
    private TextView zaxisS;
    private TextView linxaxis;
    private TextView linyaxis;
    private TextView linzaxis;
    private TextView linxaxisS;
    private TextView linyaxisS;
    private TextView linzaxisS;
    private TextView num_dec;
    private TextView num_alpha;
    private SeekBar sb;
    float ALPHA = 0.1f;
    int BETA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);

        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        accel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        linaccel = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        xaxis = (TextView)findViewById(R.id.accel_x);
        yaxis = (TextView)findViewById(R.id.accel_y);
        zaxis = (TextView)findViewById(R.id.accel_z);

        xaxisS = (TextView)findViewById(R.id.accel_xS);
        yaxisS = (TextView)findViewById(R.id.accel_yS);
        zaxisS = (TextView)findViewById(R.id.accel_zS);

        linxaxis = (TextView)findViewById(R.id.lin_accel_x);
        linyaxis = (TextView)findViewById(R.id.lin_accel_y);
        linzaxis = (TextView)findViewById(R.id.lin_accel_z);

        linxaxisS = (TextView)findViewById(R.id.lin_accel_amooth_x);
        linyaxisS = (TextView)findViewById(R.id.lin_accel_amooth_y);
        linzaxisS = (TextView)findViewById(R.id.lin_accel_amooth_z);

        num_dec = (TextView)findViewById(R.id.num_dec);
        num_dec.setText("Number of decimals: " + BETA);
        num_alpha = (TextView)findViewById(R.id.num_alpha);
        num_alpha.setText("Low pass alpha: " + ALPHA);

        sb = (SeekBar)findViewById(R.id.sk_decimals);
        //SEEKBAR
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double dec = (double)progress / 20;     //SETS THE NUMBER OF DECIMALS TO MAX 5
                int ans = (int) Math.round(dec);
                BETA = ans;
                doType("Number of decimals: " + ans, num_dec);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //END SEEKBAR
    }
    protected void onResume() {
        super.onResume();
        sm.registerListener(this, accel, SensorManager.SENSOR_DELAY_UI);
        sm.registerListener(this, linaccel, SensorManager.SENSOR_DELAY_UI);
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

        if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            accelValues = event.values;
        }

        if(accelValues != null && event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            accelValuesSmooth = lowPassFilter(accelValues, accelValuesSmooth);
            accelValuesSmooth[0] = roundToDecimal(accelValuesSmooth[0], BETA);
            accelValuesSmooth[1] = roundToDecimal(accelValuesSmooth[1], BETA);
            accelValuesSmooth[2] = roundToDecimal(accelValuesSmooth[2], BETA);

            doDraw(accelValues[0], xaxis);
            doDraw(accelValues[1], yaxis);
            doDraw(accelValues[2], zaxis);

            doDraw(accelValuesSmooth[0], xaxisS);
            doDraw(accelValuesSmooth[1], yaxisS);
            doDraw(accelValuesSmooth[2], zaxisS);
        }

        if(accelValues != null && event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            doDraw(accelValues[0], linxaxis);
            doDraw(accelValues[1], linyaxis);
            doDraw(accelValues[2], linzaxis);
            linaccelValuesSmooth = lowPassFilter(accelValues, linaccelValuesSmooth);
            linaccelValuesSmooth[0] = roundToDecimal(linaccelValuesSmooth[0], BETA);
            linaccelValuesSmooth[1] = roundToDecimal(linaccelValuesSmooth[1], BETA);
            linaccelValuesSmooth[2] = roundToDecimal(linaccelValuesSmooth[2], BETA);
            doDraw(linaccelValuesSmooth[0], linxaxisS);
            doDraw(linaccelValuesSmooth[1], linyaxisS);
            doDraw(linaccelValuesSmooth[2], linzaxisS);
        }
    }

    private void doDraw(float val, TextView id){
        id.setText(Float.toString(val));
    }
    private void doType(String txt, TextView id){
        id.setText(txt);
    }

    private float[] lowPassFilter( float[] input, float[] output ) {
        if (output == null) return input;

        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
            //output[i] = output[i] * ALPHA + (1 - ALPHA) * input[i];
        }
        return output;
    }

    public static float roundToDecimal(float number, int scale) {
        int pow = 10;
        for (int i = 1; i < scale; i++)
            pow *= 10;
        float tmp = number * pow;
        return (float) (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) / pow;
    }

    public void onClickPlus(View v){
        ALPHA += 0.05f;
        doType("Low pass alpha: " + ALPHA, num_alpha);
    }
    public void onClickMinus(View v){
        if(ALPHA > 0){
            ALPHA -= 0.05f;
            doType("Low pass alpha: " + ALPHA, num_alpha);
        } else {
            ALPHA = 0.0f;
            doType("Low pass alpha: " + ALPHA, num_alpha);
        }
    }
}
