package com.shubhanshusv.activitymonitor;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Report extends AppCompatActivity {

    TableLayout status_mood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        status_mood = (TableLayout)findViewById(R.id.oola);
        status_mood.setStretchAllColumns(true);
        status_mood.bringToFront();

        Bundle act2_dis = getIntent().getExtras();

        int status[] = act2_dis.getIntArray("1");
        int shake_status[] = act2_dis.getIntArray("2");
        int move_status[] = act2_dis.getIntArray("3");
        int or_status[] = act2_dis.getIntArray("4");
        int minutes = act2_dis.getInt("5");

        TextView title = (TextView)findViewById(R.id.title_report);
        Typeface fonttype = Typeface.createFromAsset(getAssets(),"fonts/galax___.ttf");

        title.setTypeface(fonttype);

        for(int i=0;i<minutes-1;i++){

            TableRow tr1 = new TableRow(this);
            TableRow tr2 = new TableRow(this);
            TableRow tr3 = new TableRow(this);
            TableRow tr4 = new TableRow(this);
            TableRow tr5 = new TableRow(this);
            TextView c1 = new TextView(this);
            TextView c2 = new TextView(this);
            TextView c3 = new TextView(this);
            TextView c4 = new TextView(this);
            TextView c5 = new TextView(this);
            TextView c6 = new TextView(this);
            TextView c7 = new TextView(this);
            TextView c8 = new TextView(this);
            TextView c9 = new TextView(this);

            c1.setText("Minute " + String.valueOf(i+1) + ":");

            if(status[i+1] == 0){

                c2.setText("Focussed");

            }else if(status[i+1] == 1){

                c2.setText("Slightly Fuzzy");

            }else{

                c2.setText("Not at all Focussed");

            }

            c3.setText("Movement :");

            if(move_status[i+1] == 0){

                c4.setText("Low Movement");

            }else if(move_status[i+1] == 1){

                c4.setText("Mild Movement");

            }else{

                c4.setText("High Movement");

            }


            c5.setText("Shakes :");

            if(shake_status[i+1] == 0){

                c6.setText("No shakes observed");

            }else if(shake_status[i+1] == 1){

                c6.setText("Mild Shaking");

            }else{

                c6.setText("Wow! So much jerks!");

            }

            c7.setText("Orientation :");

            if(or_status[i+1] == 0){

                c8.setText("Perfectly Oriented");

            }else if(or_status[i+1] == 1){

                c8.setText("Not perfect sometimes");

            }else if(or_status[i+1] == 2){

                c8.setText("Disturbed several times");

            }else{

                c8.setText("Smartphone idle!");

            }

            c9.setText(" ");


            c1.setTextColor(Color.parseColor("#FFFFFF"));
            c2.setTextColor(Color.parseColor("#FFFFFF"));
            c3.setTextColor(Color.parseColor("#FFFFFF"));
            c4.setTextColor(Color.parseColor("#FFFFFF"));
            c5.setTextColor(Color.parseColor("#FFFFFF"));
            c6.setTextColor(Color.parseColor("#FFFFFF"));
            c7.setTextColor(Color.parseColor("#FFFFFF"));
            c8.setTextColor(Color.parseColor("#FFFFFF"));


            c1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            c2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            c3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            c4.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            c5.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            c6.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            c7.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            c8.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            c1.setTextSize(22);
            c2.setTextSize(22);
            c3.setTextSize(22);
            c4.setTextSize(22);
            c5.setTextSize(22);
            c6.setTextSize(22);
            c7.setTextSize(22);
            c8.setTextSize(22);

            tr1.addView(c1);
            tr1.addView(c2);
            tr2.addView(c3);
            tr2.addView(c4);
            tr3.addView(c5);
            tr3.addView(c6);
            tr4.addView(c7);
            tr4.addView(c8);
            tr5.addView(c9);

            status_mood.addView(tr1);
            status_mood.addView(tr2);
            status_mood.addView(tr3);
            status_mood.addView(tr4);
            status_mood.addView(tr5);

        }

    }
}
