package com.shubhanshusv.activitymonitor;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Intent for service (runs in background)

        title = (TextView)findViewById(R.id.title);
        Typeface fonttype = Typeface.createFromAsset(getAssets(),"fonts/galax___.ttf");

        title.setTypeface(fonttype);

        Intent seri = new Intent(this,Intent_services.class);
        startService(seri);

    }

    public void start_monitor(View view){

        Intent i = new Intent(this,Activity_observer.class);
        startActivity(i);

    }

    public void help(View view){

        Intent i = new Intent(this,Help.class);
        startActivity(i);

    }

}
