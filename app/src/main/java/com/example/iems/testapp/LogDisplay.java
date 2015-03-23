package com.example.iems.testapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;


public class LogDisplay extends ActionBarActivity {

    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_display);

        text = (TextView) findViewById(R.id.logView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String logText = "";

        if (CacheLog.isInstantiated())
            for (String s : CacheLog.getLog())
                logText += s;

        text.setText(logText);
    }

    public void refreshData(View view) {
        String logText = "";

        if (CacheLog.isInstantiated())
            for (String s : CacheLog.getLog())
                logText += s;

        text.setText(logText);
    }


}
