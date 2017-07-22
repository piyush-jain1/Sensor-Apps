package com.shubhanshusv.activitymonitor;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Help extends AppCompatActivity {

    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        title = (TextView)findViewById(R.id.title_help);
        Typeface fonttype = Typeface.createFromAsset(getAssets(),"fonts/galax___.ttf");

        title.setTypeface(fonttype);
    }

    public void go_back(View view){

        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);

    }

}
