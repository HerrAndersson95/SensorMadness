package com.anderssonlegitapp.joakim.sensormadness;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void clickCompass(View v){
        Intent intent = new Intent(this, CompassActivity.class);
        startActivity(intent);
    }
    public void clickReadData(View v){
        Intent intent = new Intent(this, ReadDataActivity.class);
        startActivity(intent);
        //finish();
    }
}
