package com.shubhanshusv.movement;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

    //declaring textview
    TextView movement_count;

    //Sensor manager
    SensorManager mySensorManager;
    Sensor accelerometer;

    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    float moveCount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        movement_count = (TextView)findViewById(R.id.movement_count);

        mySensorManager = (SensorManager)getSystemService(
                Context.SENSOR_SERVICE);
        accelerometer = mySensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER);

    }

    SensorEventListener shakeEventListener
            = new SensorEventListener(){

        @Override
        public void onSensorChanged(SensorEvent event) {

            if (accelerometer.getType() == Sensor.TYPE_ACCELEROMETER) {

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                long curTime = System.currentTimeMillis();

                if ((curTime - lastUpdate) > 200) {

                    lastUpdate = curTime;

                    float x_diff = x - last_x;
                    float y_diff = y - last_y;
                    float z_diff = z - last_z;

                    if((x_diff+y_diff)>=1.5 || (y_diff+z_diff)>=1.5 || (z_diff+x_diff)>=2){

                        moveCount+= 1;

                    }

                    last_x = x;
                    last_y = y;
                    last_z = z;
                }
            }
            movement_count.setText(String.valueOf(moveCount));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Intentionally blank
        }

    };

    //Register the Listener when the Activity is resumed
    protected void onResume() {

        super.onResume();
        mySensorManager.registerListener(shakeEventListener, accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);

    }

    //Unregister the Listener when the Activity is paused
    protected void onPause() {

        super.onPause();
        mySensorManager.unregisterListener(shakeEventListener);

    }

}
