package com.shubhanshusv.activitymonitor;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

// An example of background running service
// just displays msg in log

public class Intent_services extends IntentService {

    private static final String tag = "activitymonitor";

    public Intent_services() {
        super("intent_services");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Log.i(tag,"Service started");

    }
}
