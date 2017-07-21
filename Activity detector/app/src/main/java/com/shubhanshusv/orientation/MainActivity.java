package com.shubhanshusv.orientation;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Vibrator;
import android.content.Context;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;
import android.util.Log;
import android.graphics.Color;
import android.view.View;

public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;

    TextView pitch_,roll_,mild_or,severe_or,path_;
    TextView movement,shake,ans,minute,counter;

    float azimut;

    float linear_acceleration[] = new float[3];
    float gravity[] = new float[3];

    long start_time,end_time,diff,app_start_time,app_present_time;
    //int screen_orientation;
    int mild_count,strong_count;
    int flag_m,flag_s,flag_c;
    int flag;
    long diff1;

    long min_counter = 0;

    private long lastUpdate = 0;                                // stores last time update was made in checking values of shake
    private float last_x, last_y, last_z;                       // stores previous values of acceleration in x,y,z directions
    private static final int SHAKE_THRESHOLD = 250;             // constant value of threshold



    //For flaw count (to keep track of flaws)

    int mild_flaw=0;
    int severe_flaw=0;
    int total = 0;

    float pitch,roll;

    private final float[] mAccelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];
    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];

    float moveCount = 0;                                        // keeps count of movement data present minute
    float prev_movecount = 0;                                   // movement data in previous minute
    float shakeCount=0;                                         // shake data in present minute
    float prev_shakecount = 0;                                  // shake data in previous minute
    int prev_or_mild = 0;                                       // orientation mild flaw data in previous minute
    int prev_or_severe = 0;                                     // orientation severe flaw data in previous minute
    Vibrator vibe ;

    int path;
    View view;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);    // Register the sensor listeners
        ans = (TextView)findViewById(R.id.ans);
        movement = (TextView)findViewById(R.id.movement);
        shake = (TextView)findViewById(R.id.shake);
        pitch_ = (TextView)findViewById(R.id.pitch);
        roll_ = (TextView)findViewById(R.id.roll);
        path_ = (TextView)findViewById(R.id.with);
        mild_or = (TextView)findViewById(R.id.mild_or);
        severe_or = (TextView)findViewById(R.id.severe_or);
        counter = (TextView)findViewById(R.id.counter1);
        minute = (TextView)findViewById(R.id.counter2);

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
        start_time/=1000;
        app_start_time = System.currentTimeMillis();
        app_start_time /= 1000;
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        view = findViewById(R.id.color);
        view.setBackgroundColor(Color.GREEN);

        vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        if (magnetometer == null){
            path = 1;
            path_.setText("without");
        }else{
            path = 2;
            path_.setText("with");
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

    public void onAccuracyChanged(Sensor sensor, int accuracy) {  }


    public void onSensorChanged(SensorEvent event) {

        app_present_time = System.currentTimeMillis()/1000;
        diff1 = app_present_time - app_start_time;

        minute.setText(String.valueOf(diff1%60));

        if(diff1%60 == 0 && flag==0){

            prev_movecount = moveCount;
            prev_shakecount = shakeCount;

            if(prev_shakecount < 4 && prev_shakecount >= 2){

                mild_flaw++;

            }else if(prev_shakecount >= 4){

                severe_flaw++;

            }

            if(prev_movecount <= 15 && prev_movecount >= 10){

                mild_flaw++;

            }else if(prev_movecount > 15){

                severe_flaw++;

            }

            mild_flaw+= mild_count;
            severe_flaw += strong_count;

            total = mild_flaw + 2*severe_flaw;

            if(total <4){

                ans.setText("Focus");
                view.setBackgroundColor(Color.GREEN);


            }else if(total >=4 && total <=6){


                ans.setText("Mild");
                view.setBackgroundColor(Color.YELLOW);

            }else{

                ans.setText("Severe");
                view.setBackgroundColor(Color.RED);

            }

            mild_flaw = 0;
            severe_flaw = 0;
            total = 0;
            min_counter++;
            mild_count = 0;
            strong_count =0;
            moveCount = 0;
            shakeCount = 0;
            flag = 1;

        }else if(diff1%60 == 1){

            flag = 0;

        }

        if(path == 1){

            path_.setText("Without");

            final float alpha = 0.3f;

            if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){


                mAccelerometerReading[0] = event.values[0];
                mAccelerometerReading[1] = event.values[1];
                mAccelerometerReading[2] = event.values[2];

                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

                double gravityNorm = Math.sqrt(gravity[0] * gravity[0] + gravity[1] * gravity[1] + gravity[2] * gravity[2]);

                pitch = (float) Math.asin(-gravity[1] / gravityNorm);
                roll = (float) Math.atan2(-gravity[0] / gravityNorm, gravity[2] / gravityNorm);

                pitch =pitch*180*7/22;
                roll = roll*180*7/22;

                pitch_.setText(String.valueOf(pitch));
                roll_.setText(String.valueOf(roll));

            }

        }else{

            path_.setText("With");
            // Obtaining readings from accelerometer and magnetometer

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                mAccelerometerReading[0] = event.values[0];
                mAccelerometerReading[1] = event.values[1];
                mAccelerometerReading[2] = event.values[2];

            }else if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){

                mMagnetometerReading[0] = event.values[0];
                mMagnetometerReading[1] = event.values[1];
                mMagnetometerReading[2] = event.values[2];

            }

            way_one_orientation();

        }

        shake_n_movement_data(mAccelerometerReading[0],mAccelerometerReading[1],mAccelerometerReading[2],lastUpdate,shakeCount,moveCount);
            movement.setText(String.valueOf(moveCount));
            shake.setText(String.valueOf(shakeCount));
            pitch_.setText(String.valueOf(pitch));
            roll_.setText(String.valueOf(roll));
        int screen_orientation = this.getResources().getConfiguration().orientation;

        if(screen_orientation == 2){

            if((Math.abs(roll)<=60 && Math.abs(roll) >= 30) && (pitch <=12 && pitch >=-12)){
                start_time = System.currentTimeMillis();
                start_time/=1000;
                diff = 0;
                flag_c = 0;
            }else{
                end_time = System.currentTimeMillis();
                end_time /= 1000;
                diff = end_time - start_time;

                if((diff>= 5 && diff < 10) && flag_c == 0){

                    mild_count++;
                    mild_or.setText(String.valueOf(mild_count));
                    flag_c = 1;

                }

                if((diff >= 10 && diff < 15) && flag_c == 1){

                    strong_count++;
                    severe_or.setText(String.valueOf(strong_count));
                    flag_c = 2;

                }

                if(diff >=15 && flag_c == 2){

                    strong_count+=3;
                    severe_or.setText(String.valueOf(strong_count));
                    flag_c = 3;

                }

                if(diff == 30){

                    start_time = System.currentTimeMillis();
                    start_time/=1000;
                    diff = 0;
                    flag_c = 0;

                }

            }
            counter.setText(String.valueOf(diff));


        }else{

            if((Math.abs(pitch)<=60 && Math.abs(pitch) >= 30) && (roll <=12 && roll >=-12)){
                start_time = System.currentTimeMillis();
                start_time/=1000;
                diff = 0;
                flag_c = 0;
            }else{
                end_time = System.currentTimeMillis();
                end_time /= 1000;
                diff = end_time - start_time;

                if((diff>= 5 && diff < 10) && flag_c == 0){

                    mild_count++;
                    mild_or.setText(String.valueOf(mild_count));
                    flag_c = 1;

                }

                if((diff >= 10 && diff < 15) && flag_c == 1){

                    strong_count++;
                    severe_or.setText(String.valueOf(strong_count));
                    flag_c = 2;

                }

                if(diff >=15 && flag_c == 2){

                    strong_count+=3;
                    severe_or.setText(String.valueOf(strong_count));
                    flag_c = 3;

                }

                if(diff == 30){

                    start_time = System.currentTimeMillis();
                    start_time/=1000;
                    diff = 0;
                    flag_c = 0;

                }

            }
            counter.setText(String.valueOf(diff));
        }
    }



























    //////////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////Movement and Shaking Frequency Module ////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////////////







    //////////////////////////////////////////////////////////////////////////////////////////////////////

    ///Counter reset function

    /// Input parameters
    /// laid in the input of function
    /// described above



    /// Function
    /// Resets various counters used to measure counts with respect to various conditions
    /// Records previous counts

    /// Note instead of making similar function for orientation, its counters are also
    /// handled here


    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    private void counter_reset(long minute_counter, float movement_count ,int fla, long difference,float shake_count,int or_mild,int or_severe){

        if(difference%60 == 0 && fla==0){

            // All present counts will be reset, so these counts are transferred as previous minute count

            prev_movecount = movement_count;
            prev_shakecount = shake_count;
            prev_or_mild = or_mild;
            prev_or_severe = or_severe;


            // All the counters containing flaw counts with respect to various conditions are reseted to 0

            movement_count = 0;
            shake_count = 0;
            or_mild = 0;
            or_severe = 0;
            mild_flaw = 0;
            severe_flaw = 0;

            // Minute is increased by 1 as one minute has passed from the starting time

            minute_counter+=1;

            // Flag is made 1 , This flag is just helping us to cover the case that when a minute has passed
            // then from that duration until 1 second is passed, no resetting should take place.

            fla = 1;


        }else if(difference%60 == 1){

            // flag again reset to 0 when 1 second after a minute has passed
            fla = 0;

        }


        // changes made locally are transferred to related variables
        flag = fla;
        moveCount = movement_count;
        min_counter = minute_counter;
        shakeCount = shake_count;
        //orientation_mild_count = or_mild;
        //orientation_severe_count = or_severe;

    }






    //////////////////////////////////////////////////////////////////////////////////////////////////////

    ///Shake and movement calculator

    /// Input parameters
    /// float values of accelerations
    /// long last update time
    /// floatof shake and move count




    /// Function
    /// calculates shake and movement flaw


    ///////////////////////////////////////////////////////////////////////////////////////////////////////


    private void shake_n_movement_data(float x, float y, float z,long last_update,float shake_Count, float move_Count){


        // Declaring variables
        long curTime = System.currentTimeMillis();                     // current system time
        long diffTime ;                                                // difference from app start time
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
            jerk = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;


            // if jerk is greater than set threshold, shake count is increased by 0.25
            // increment by 0.25 is to control sensitivity
            // Shake count helps us to keep track of sudden jerks experienced by smart phone
            if (jerk > SHAKE_THRESHOLD) {
                shake_Count+= 0.25;
            }


            // Movement is measured by noting changes in accelerations in two directions
            // This helps to keep track of minor smart phone movements by the user
            if((x_diff+y_diff)>=1.5 || (y_diff+z_diff)>=1.5 || (z_diff+x_diff)>=2){
                move_Count+= 1;
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

    ///flaw counter updator

    /// Input parameters
    /// float shake and movement



    /// Function
    /// updates mild and severe count based on previous flaw data


    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    void flaw_count_updater(float prev_movecount,float prev_shakecount){


        // implements specified conditions os specified in design doc

        if(prev_shakecount <= 4 && prev_shakecount >= 2){

            mild_flaw++;

        }else if(prev_shakecount > 2){

            severe_flaw++;

        }

        if(prev_movecount <= 15 && prev_movecount >= 10){

            mild_flaw++;

        }else if(prev_movecount > 15){

            severe_flaw++;

        }



    }








    //////////////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////Orientation Module //////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////////////







    //////////////////////////////////////////////////////////////////////////////////////////////////////

    ///Way decider function

    /// Input parameters
    /// sensor



    /// Returnsa Int


    /// Function
    /// there are two choices available
    /// 1. Magnetic field sensor is present in smartphone
    /// 2. magnetic field sensor is not present

    /// If magnetic field sensor is not present, way 0 is used to calculate orientation
    /// If magnetic field sensor is present , way 1 is used

    /// This function decides which way to take


    ///////////////////////////////////////////////////////////////////////////////////////////////////////






    int way_decider(Sensor magnetic_field){

        // If magnetic field sensor is not present, choose path 0
        // else choose path 1

        int path = 0;

        if(magnetic_field == null){

            path = 1;

        }else{

            path = 0;
        }

        return path;

    }





    //////////////////////////////////////////////////////////////////////////////////////////////////////

    ///Main orientation function

    /// Input parameters
    /// int way (which way to follow)






    /// Function
    /// calculates and updates orientation count


    ///////////////////////////////////////////////////////////////////////////////////////////////////////


//    void Orientation_calculation(int path){
//
//        // if path = 0, the way 0 else way 1
//        if(path == 0){
//
//            //way_zero_orientation(accelerometerReading[0], accelerometerReading[1],accelerometerReading[2]);
//
//
//            //if screen orientation is portrait, strong condition on roll, else on pitch
//            if(screen_orientation == 2){
//
//                flaw_checker(pitch ,roll,start_time_orientation,flag_c,error_duration,orientation_severe_count,orientation_mild_count);
//
//            }else{
//
//                flaw_checker(roll,pitch,start_time_orientation,flag_c,error_duration,orientation_severe_count,orientation_mild_count);
//
//            }
//
//
//
//        }else if(path == 1){
//
//            way_one_orientation();
//
//            if(screen_orientation == 2){
//
//                flaw_checker(pitch ,roll,start_time_orientation,flag_c,error_duration,orientation_severe_count,orientation_mild_count);
//
//
//            }else{
//
//                flaw_checker(roll,pitch,start_time_orientation,flag_c,error_duration,orientation_severe_count,orientation_mild_count);
//
//
//            }
//
//
//
//
//        }
//
//
//    }
//
//





    //////////////////////////////////////////////////////////////////////////////////////////////////////

    ///way 0 orientation calculation

    /// Input parameters
    /// float accelerometer readings






    /// Function
    /// Calculates orientation value from just accelerometer readings
    /// gravity values are filtered from accelerometer values
    /// which in turn are used to calculate orientation values

    /// Standard way laid down in android documentation


    ///////////////////////////////////////////////////////////////////////////////////////////////////////


    void way_zero_orientation(float acc_x, float acc_y,float acc_z){


        //Variable declaration

        float gravity[] = new float[3];                                     // to store gravity values in x, y, z directions
        float alpha = 0.3f;                                                 // alpha is a filtering constant
        double gravityNorm;                                                 // normalized gravity


        // gravity in x , y, z directions are filtered from accelerometer readings
        gravity[0] = alpha * gravity[0] + (1 - alpha) * acc_x;
        gravity[1] = alpha * gravity[1] + (1 - alpha) * acc_y;
        gravity[2] = alpha * gravity[2] + (1 - alpha) * acc_z;


        // normalized gravity is calculated
        gravityNorm = Math.sqrt(gravity[0] * gravity[0] + gravity[1] * gravity[1] + gravity[2] * gravity[2]);


        // Pitch and roll are calculated by applying trigonometry
        pitch = (float) Math.asin(-gravity[1] / gravityNorm);
        roll = (float) Math.atan2(-gravity[0] / gravityNorm, gravity[2] / gravityNorm);


        // Values are converted into degrees from radians
        pitch =pitch*180*7/22;
        roll = roll*180*7/22;


    }











    //////////////////////////////////////////////////////////////////////////////////////////////////////

    ///way 1 orientation calculation

    /// Input parameters
    /// none






    /// Function
    /// Standard android built in functions have been used included in sensor manager library
    /// Inbuilt functions use accelerometer reading and magnetometer reading to calculate orientation


    ///////////////////////////////////////////////////////////////////////////////////////////////////////


    void way_one_orientation(){


        float[] rotationMatrix = new float[9];
        float[] orientationAngles = new float[3];

        // Update rotation matrix, which is needed to update orientation angles.

        mSensorManager.getRotationMatrix(rotationMatrix, null,
                mAccelerometerReading, mMagnetometerReading);

        // "rotationMatrix" now has up-to-date information.

        mSensorManager.getOrientation(rotationMatrix, orientationAngles);

        // "orientationAngles" now has up-to-date information.

        pitch = (float)Math.toDegrees(orientationAngles[1]);
        roll = (float)Math.toDegrees(orientationAngles[2]);


        // pitch and roll have been assigned corresponding values

    }








    //////////////////////////////////////////////////////////////////////////////////////////////////////

    ///flaw condition checker

    /// Input parameters
    /// angles orientation float
    /// duration of flaw






    /// Function
    /// flaw conditions are imposed


    ///////////////////////////////////////////////////////////////////////////////////////////////////////




    private void flaw_checker(float angle_low ,float angle_high,long s_time,int check_bit, long duration,int s_count,int m_count){

        // This condition implies that orientation standards are followed

        if((Math.abs(angle_high)<=60 && Math.abs(angle_high) >= 30) && (angle_low <=12 && angle_low >=-12)){

            // s_time is used to mark the beginning of event when condition is not followed
            s_time = System.currentTimeMillis()/1000;

            // since condition is followed, duration of flaw in condition is 0.
            duration = 0;

            // check_bit is 0, used to indicate various duration types
            check_bit = 0;

        }else{

            // If condition is not followed, then flaw is measured by condition implementor
            condition_implementer(s_time,check_bit,duration,s_count,m_count);

        }


        // Changes made are transferred

        //flag_c = check_bit;
        //error_duration = duration;
        //start_time_orientation = s_time;
        //orientation_severe_count = s_count;
        //orientation_mild_count = m_count;


    }







    //////////////////////////////////////////////////////////////////////////////////////////////////////

    ///Condition implementor

    /// Input parameters
    /// check bits






    /// Function
    /// imposes condition


    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    void condition_implementer(long s_time,int check_bit, long duration,int s_count,int m_count){

        // used to calculate duration of flaw
        long e_time = System.currentTimeMillis()/1000;


        // Duration is difference between flae start and flaw end time
        duration = e_time - s_time;

        // counters are increased according to duration and check bit
        //check bits ensure that only one of these if statements are followed at a time



        // between 5 to 10 sec, increase mild count by 1
        if((duration >= 5 && duration < 10) && check_bit == 0){

            m_count++;
            check_bit = 1;

        }

        // between 10 to 15 sec, increase severe count by 1
        if((duration >= 10 && duration <15) && check_bit == 1){

            s_count++;
            check_bit = 2;

        }

        // greater than 15s, increase severe count by 3
        // not implemented but can be used to detect extreme flaws
        if(duration >=15 && check_bit == 2){

            s_count+=3;
            check_bit = 3;

        }


        // if duration is 30s, reset timer
        if(duration == 30){

            s_time = System.currentTimeMillis()/1000;
            duration = 0;
            check_bit = 0;

        }



        // Transfer changes to corresponding variables
        //error_duration = duration;
        //flag_c = check_bit;
        //orientation_mild_count = m_count;
        //orientation_severe_count = s_count;

    }


}
