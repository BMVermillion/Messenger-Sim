package com.example.iems.testapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import static android.widget.LinearLayout.*;


public class MainActivity extends ActionBarActivity{

    ScrollView slider;
    LinearLayout scroll;
    EditText text;
    Signal sig;

    Switch signalVis;

    private final String delim = "_";
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(Intent.ACTION_SEND));

        scroll = (LinearLayout)findViewById(R.id.scrollLayout);
        text = (EditText)findViewById(R.id.editText);
        sig = (Signal)findViewById(R.id.signalView);
        slider = (ScrollView)findViewById(R.id.messages);


       Intent serviceIntent = new Intent(this, NetworkService.class);
       startService(serviceIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        CacheLog.writeLog("Activity Destroyed");

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(Intent.ACTION_SEND));

    }

    @Override
    protected void onStop() {
        super.onPause();

        if (CacheLog.isInstantiated())
            CacheLog.writeLog("Stopping MainActivity");
    }

    @Override
    protected void onStart() {
        super.onResume();

        //Add messages that were sent while the app was not in view
        if (CacheLog.isInstantiated())
            CacheLog.writeLog("Starting MainActivity");

        signalVis = (Switch)findViewById(R.id.sigToggle);
        if (signalVis != null)
            sig.setVisibility(signalVis.isActivated()?VISIBLE:INVISIBLE);
       // LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(Intent.ACTION_SEND));
        scrollToBottom();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intent;
        switch (id) {
            case R.id.action_settings:
                intent = new Intent(this, Settings.class);
                startActivity(intent);
                break;
            case R.id.action_log:
                intent = new Intent(this, LogDisplay.class);
                startActivity(intent);
                break;
            case R.id.action_stop_service:
                intent = new Intent(this, NetworkService.class);
                stopService(intent);
                break;
            case R.id.action_clear:
                CacheLog.clearData("cleared_data");
                scroll.removeAllViews();
        }


        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String message = intent.getStringExtra("message");
            messageSelector(message);
        }
    };

    private void messageSelector(String message) {

        String[] tokens = message.split(delim);

        if (tokens == null)
            return;

        switch (tokens[0]) {
            case "left":
                sig.setLeft();
                counter = 500;
                CacheLog.writeData("Received",tokens[0]);
                break;
            case "center":
                sig.setCenter();
                counter = 500;
                CacheLog.writeData("Received",tokens[0]);
                break;
            case "right":
                sig.setRight();
                counter = 500;
                CacheLog.writeData("Received",tokens[0]);
                break;
            case "start":
                CacheLog.writeData("Received",tokens[0]);
                return;
            case "stop":
                CacheLog.writeData("Received",tokens[0]);
                CacheLog.clearData("");
                return;
            default:

        }
        if (tokens.length > 1)
            setMessage(tokens[1]);
    }

    public void setMessage(String s) {
        if (s.equals(""))
            return;

        post(new MessageBox(this, true, Editable.Factory.getInstance().newEditable(s)));
    }

    public void setMessage(View view) {
        Editable userInput = text.getText();
        if (userInput.toString().equals(""))
            return;

        CacheLog.writeData("post", userInput.toString(), System.currentTimeMillis());
        CacheLog.writeLog("Posted \"" + userInput.toString() + "\"");

        post(new MessageBox(this, false, userInput));

        text.setText("");
    }

    private void post(MessageBox m) {
        scroll.addView(m);
        scrollToBottom();

        Space filler = new Space(this);
        filler.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,25,1f));
        scroll.addView(filler);
    }

    private void scrollToBottom() {
        slider.post(new Runnable() {
            public void run() {
                slider.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void signalCooldown() {
        runOnUiThread(new Thread() {
            @Override
            public void run() {
                int time = 100;
                while (true) {
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (counter == 0) {
                        sig.invalidate();
                        Log.wtf("Sig", "Resetting");
                    }
                    else if (counter < 0)
                        continue;

                    counter -= time;

                }
            }
        });
    }




}
