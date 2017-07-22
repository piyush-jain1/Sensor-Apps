package com.shubhanshusv.activitymonitor;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class Activity_observer extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;

    //TextView pitch_, roll_, mild_or, severe_or, path_;
    //TextView movement, shake, ans, minute, counter;


    float azimut;

    float linear_acceleration[] = new float[3];
    float gravity[] = new float[3];
    int status[] = new int[120];
    int shake_status[] = new int[120];
    int or_status[] = new int[120];
    int move_status[] = new int[120];
    int min = 0;

    long start_time, end_time, diff, app_start_time, app_present_time;
    //int screen_orientation;
    int mild_count, strong_count;
    int flag_m, flag_s, flag_c;
    int flag;
    long diff1;

    int min_counter = 1;

    private long lastUpdate = 0;                                // stores last time update was made in checking values of shake
    private float last_x, last_y, last_z;                       // stores previous values of acceleration in x,y,z directions
    private static final int SHAKE_THRESHOLD = 250;             // constant value of threshold


    //For flaw count (to keep track of flaws)

    int mild_flaw = 0;
    int severe_flaw = 0;
    int total = 0;

    float pitch, roll;

    private final float[] mAccelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];
    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];

    float moveCount = 0;                                        // keeps count of movement data present minute
    float prev_movecount = 0;                                   // movement data in previous minute
    float shakeCount = 0;                                         // shake data in present minute
    float prev_shakecount = 0;                                  // shake data in previous minute
    int prev_or_mild = 0;                                       // orientation mild flaw data in previous minute
    int prev_or_severe = 0;                                     // orientation severe flaw data in previous minute
    //Vibrator vibe ;

    ValueAnimator gty = new ValueAnimator();
    ValueAnimator gtr = new ValueAnimator();
    ValueAnimator ytg = new ValueAnimator();
    ValueAnimator ytr = new ValueAnimator();
    ValueAnimator rty = new ValueAnimator();
    ValueAnimator rtg = new ValueAnimator();

    int path;
    View view;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observer);    // Register the sensor listeners
        //ans = (TextView) findViewById(R.id.ans);
        //movement = (TextView) findViewById(R.id.movement);
        //shake = (TextView) findViewById(R.id.shake);
        //pitch_ = (TextView) findViewById(R.id.pitch);
        //roll_ = (TextView) findViewById(R.id.roll);
        //path_ = (TextView) findViewById(R.id.with);
        //mild_or = (TextView) findViewById(R.id.mild_or);
        //severe_or = (TextView) findViewById(R.id.severe_or);
        //counter = (TextView) findViewById(R.id.counter1);
        //minute = (TextView) findViewById(R.id.counter2);

        int i;

        for(i=0;i<120;i++){

            or_status[i] = 0;

        }

        status[0] = 0;
        move_status[0] = 0;
        shake_status[0] = 0;

        // green to yellow

        gty.setIntValues(R.color.green,R.color.yellow);
        gty.setEvaluator(new ArgbEvaluator());
        gty.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setBackgroundColor((Integer)animation.getAnimatedValue());
            }
        });

        gty.setDuration(4000);

        // green to red

        gtr.setIntValues(R.color.green,R.color.red);
        gtr.setEvaluator(new ArgbEvaluator());
        gtr.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setBackgroundColor((Integer)animation.getAnimatedValue());
            }
        });

        gtr.setDuration(4000);

        // yellow to green

        ytg.setIntValues(R.color.yellow,R.color.green);
        ytg.setEvaluator(new ArgbEvaluator());
        ytg.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setBackgroundColor((Integer)animation.getAnimatedValue());
            }
        });

        ytg.setDuration(4000);

        // yellow to red

        ytr.setIntValues(R.color.yellow,R.color.red);
        ytr.setEvaluator(new ArgbEvaluator());
        ytr.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setBackgroundColor((Integer)animation.getAnimatedValue());
            }
        });

        ytr.setDuration(4000);

        // red to green

        rtg.setIntValues(R.color.red,R.color.green);
        rtg.setEvaluator(new ArgbEvaluator());
        rtg.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setBackgroundColor((Integer)animation.getAnimatedValue());
            }
        });

        rtg.setDuration(4000);

        // red to yellow

        rty.setIntValues(R.color.red,R.color.yellow);
        rty.setEvaluator(new ArgbEvaluator());
        rty.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setBackgroundColor((Integer)animation.getAnimatedValue());
            }
        });

        rty.setDuration(4000);

        gravity[0] = 0.0f;
        gravity[1] = 0.0f;
        gravity[2] = 0.0f;
        diff = 0;
        diff1 = 0;
        mild_count = 0;
        strong_count = 0;
        flag_m = 0;
        flag_s = 0;
        flag_c = 0;
        start_time = System.currentTimeMillis();
        start_time /= 1000;
        app_start_time = System.currentTimeMillis();
        app_start_time /= 1000;
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        view = findViewById(R.id.color);
        view.setBackgroundColor(Color.parseColor("#7EF724"));
        //bgcolor.start();

        //vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        if (magnetometer == null) {
            path = 1;
            //path_.setText("without");
        } else {
            path = 2;
            //path_.setText("with");
        }
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    public void onSensorChanged(SensorEvent event) {

        app_present_time = System.currentTimeMillis() / 1000;
        diff1 = app_present_time - app_start_time;

        //minute.setText(String.valueOf(diff1 % 60));

        if (diff1 % 60 == 0 && flag == 0 && diff1 != 0) {

            prev_movecount = moveCount;
            prev_shakecount = shakeCount;

            status[min_counter] = 0;
            move_status[min_counter] = 0;
            shake_status[min_counter] = 0;

            if (prev_shakecount < 4 && prev_shakecount >= 2) {

                mild_flaw++;
                shake_status[min_counter] = 1;

            } else if (prev_shakecount >= 4) {

                severe_flaw++;
                shake_status[min_counter] = 2;

            }

            if (prev_movecount <= 15 && prev_movecount >= 10) {

                mild_flaw++;
                move_status[min_counter] = 0;

            } else if (prev_movecount > 15) {

                severe_flaw++;
                move_status[min_counter] = 0;

            }

            mild_flaw += mild_count;
            severe_flaw += strong_count;
            total = mild_flaw + 2 * severe_flaw;

            if (total < 4) {

                // This was for the smooth transition of colours

//                if(status[min_counter-1] == 1){
//
//                    ytg.start();
//                    view.setBackgroundColor(Color.parseColor("#7EF724"));
//
//                }else if(status[min_counter-1] == 2){
//
//                    rtg.start();
//                    view.setBackgroundColor(Color.parseColor("#7EF724"));
//
//                }else{
//
//                    view.setBackgroundColor(Color.parseColor("#7EF724"));
//
//                }

                //ans.setText("Good job, Keep Concentrating");
                view.setBackgroundColor(Color.parseColor("#7EF724"));
                status[min_counter] = 0;

            }else if (total >= 4 && total <= 6){

                // This was for the smooth transition of colours

//                if(status[min_counter-1] == 0){
//
//                    gty.start();
//                    view.setBackgroundColor(Color.parseColor("#EEF724"));
//
//                }else if(status[min_counter-1] == 1){
//
//                    rty.start();
//                    view.setBackgroundColor(Color.parseColor("#EEF724"));
//
//                }else{
//
//                    view.setBackgroundColor(Color.parseColor("#EEF724"));
//
//                }

                //ans.setText("Hey maintain focus");
                view.setBackgroundColor(Color.parseColor("#EEF724"));
                status[min_counter] = 1;

            }else{

                // This was for the smooth transition of colours

                //ans.setText("Warning! Not at all focused");
//                if(status[min_counter-1] == 0){
//
//                    gtr.start();
//                    view.setBackgroundColor(Color.parseColor("#F73124"));
//
//                }else if(status[min_counter-1] == 1){
//
//                    ytr.start();
//                    view.setBackgroundColor(Color.parseColor("#F73124"));
//
//                }else{
//
//                    view.setBackgroundColor(Color.parseColor("#F73124"));
//
//                }

                view.setBackgroundColor(Color.parseColor("#F73124"));
                status[min_counter] = 2;

            }

            mild_flaw = 0;
            severe_flaw = 0;
            total = 0;
            min_counter++;
            mild_count = 0;
            strong_count = 0;
            moveCount = 0;
            shakeCount = 0;
            flag = 1;

        } else if (diff1 % 60 == 1) {

            flag = 0;

        }

        if (path == 1) {

            //path_.setText("Without");

            final float alpha = 0.3f;

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){

                mAccelerometerReading[0] = event.values[0];
                mAccelerometerReading[1] = event.values[1];
                mAccelerometerReading[2] = event.values[2];

                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

                double gravityNorm = Math.sqrt(gravity[0] * gravity[0] + gravity[1] * gravity[1] + gravity[2] * gravity[2]);

                pitch = (float) Math.asin(-gravity[1] / gravityNorm);
                roll = (float) Math.atan2(-gravity[0] / gravityNorm, gravity[2] / gravityNorm);

                pitch = pitch * 180 * 7 / 22;
                roll = roll * 180 * 7 / 22;

                //pitch_.setText(String.valueOf(pitch));
                //roll_.setText(String.valueOf(roll));

            }

        } else {

            // path_.setText("With");
            // Obtaining readings from accelerometer and magnetometer

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                mAccelerometerReading[0] = event.values[0];
                mAccelerometerReading[1] = event.values[1];
                mAccelerometerReading[2] = event.values[2];

            } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {

                mMagnetometerReading[0] = event.values[0];
                mMagnetometerReading[1] = event.values[1];
                mMagnetometerReading[2] = event.values[2];

            }

            way_one_orientation();

        }

        shake_n_movement_data(mAccelerometerReading[0], mAccelerometerReading[1], mAccelerometerReading[2], lastUpdate, shakeCount, moveCount);
        //movement.setText(String.valueOf(moveCount));
        //shake.setText(String.valueOf(shakeCount));
        //pitch_.setText(String.valueOf(pitch));
        //roll_.setText(String.valueOf(roll));
        int screen_orientation = this.getResources().getConfiguration().orientation;

        if (screen_orientation == 2) {

            if ((Math.abs(roll) <= 60 && Math.abs(roll) >= 30) && (pitch <= 12 && pitch >= -12)) {
                start_time = System.currentTimeMillis();
                start_time /= 1000;
                diff = 0;
                flag_c = 0;
            } else {
                end_time = System.currentTimeMillis();
                end_time /= 1000;
                diff = end_time - start_time;

                if ((diff >= 5 && diff < 10) && flag_c == 0) {

                    mild_count++;
                    or_status[min_counter] = 1;
                    //mild_or.setText(String.valueOf(mild_count));
                    flag_c = 1;

                }

                if ((diff >= 10 && diff < 15) && flag_c == 1) {

                    strong_count++;
                    or_status[min_counter] = 2;
                    //severe_or.setText(String.valueOf(strong_count));
                    flag_c = 2;

                }

                if (diff >= 15 && flag_c == 2) {

                    strong_count += 3;
                    or_status[min_counter] = 3;
                    //severe_or.setText(String.valueOf(strong_count));
                    flag_c = 3;

                }

                if (diff == 30) {

                    start_time = System.currentTimeMillis();
                    start_time /= 1000;
                    diff = 0;
                    flag_c = 0;

                }

            }
            //counter.setText(String.valueOf(diff));


        } else {

            if ((Math.abs(pitch) <= 60 && Math.abs(pitch) >= 30) && (roll <= 12 && roll >= -12)) {
                start_time = System.currentTimeMillis();
                start_time /= 1000;
                diff = 0;
                flag_c = 0;
            } else {
                end_time = System.currentTimeMillis();
                end_time /= 1000;
                diff = end_time - start_time;

                if ((diff >= 5 && diff < 10) && flag_c == 0) {

                    mild_count++;
                    or_status[min_counter] = 1;
                    //mild_or.setText(String.valueOf(mild_count));
                    flag_c = 1;

                }

                if ((diff >= 10 && diff < 15) && flag_c == 1) {

                    strong_count++;
                    or_status[min_counter] = 2;
                    //severe_or.setText(String.valueOf(strong_count));
                    flag_c = 2;

                }

                if (diff >= 15 && flag_c == 2) {

                    strong_count += 3;
                    or_status[min_counter] = 3;
                    //severe_or.setText(String.valueOf(strong_count));
                    flag_c = 3;

                }

                if (diff == 30) {

                    start_time = System.currentTimeMillis();
                    start_time /= 1000;
                    diff = 0;
                    flag_c = 0;

                }

            }
            //counter.setText(String.valueOf(diff));
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////Movement and Shaking Frequency Module ////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////////////


    private void shake_n_movement_data(float x, float y, float z, long last_update, float shake_Count, float move_Count) {


        // Declaring variables
        long curTime = System.currentTimeMillis();                     // current system time
        long diffTime;                                                // difference from app start time
        float jerk;                                                    // variable to store jerk measure

        // To note changes in accelerations in x, y , z values from last updated values
        float x_diff = x - last_x;
        float y_diff = y - last_y;
        float z_diff = z - last_z;


        // checks when the data was last updated
        // if more than 0.2 seconds have passed, update

        if ((curTime - last_update) > 200) {

            // difference is noted
            diffTime = (curTime - last_update);
            // last update time is updated
            last_update = curTime;


            // shake is calculated by measuring jerk, i.e, rate of change of acceleration
            jerk = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;


            // if jerk is greater than set threshold, shake count is increased by 0.25
            // increment by 0.25 is to control sensitivity
            // Shake count helps us to keep track of sudden jerks experienced by smart phone
            if (jerk > SHAKE_THRESHOLD) {
                shake_Count += 0.25;
            }


            // Movement is measured by noting changes in accelerations in two directions
            // This helps to keep track of minor smart phone movements by the user
            if ((x_diff + y_diff) >= 1.5 || (y_diff + z_diff) >= 1.5 || (z_diff + x_diff) >= 2) {
                move_Count += 1;
            }


            // Previous values of accelerations are updated
            last_x = x;
            last_y = y;
            last_z = z;

        }


        // updated values are transferred
        shakeCount = shake_Count;
        moveCount = move_Count;
        lastUpdate = last_update;

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////Orientation Module //////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////////////


    void way_one_orientation() {


        float[] rotationMatrix = new float[9];
        float[] orientationAngles = new float[3];

        // Update rotation matrix, which is needed to update orientation angles.

        mSensorManager.getRotationMatrix(rotationMatrix, null,
                mAccelerometerReading, mMagnetometerReading);

        // "rotationMatrix" now has up-to-date information.

        mSensorManager.getOrientation(rotationMatrix, orientationAngles);

        // "orientationAngles" now has up-to-date information.

        pitch = (float) Math.toDegrees(orientationAngles[1]);
        roll = (float) Math.toDegrees(orientationAngles[2]);


        // pitch and roll have been assigned corresponding values

    }


    public void get_report(View view){

        Intent i = new Intent(this,Report.class);

        i.putExtra("1",status);

        i.putExtra("2",shake_status);

        i.putExtra("3",move_status);

        i.putExtra("4",or_status);

        i.putExtra("5",min_counter);

        finish();

        startActivity(i);

    }

}