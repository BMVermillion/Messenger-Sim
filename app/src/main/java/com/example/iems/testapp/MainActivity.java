package com.example.iems.testapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;

import static android.widget.LinearLayout.GONE;
import static android.widget.LinearLayout.VISIBLE;


public class MainActivity extends ActionBarActivity {

    public final int SETTINGS_REQUEST_CODE = 1;
    private final String delim = "_";
    private final int DEFAULT_PORT = 50001;
    private final int COOLDOWN_TIME = 1000;

    private ScrollView slider;
    private LinearLayout scroll;
    private EditText text;
    private Signal sig;
    private String startMessage;
    private String stopMessage;

    public static int width;
    public static int height;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String message = intent.getStringExtra("message");
            messageSelector(message);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(Intent.ACTION_SEND));

        scroll = (LinearLayout) findViewById(R.id.scrollLayout);
        text = (EditText) findViewById(R.id.editText);
        sig = (Signal) findViewById(R.id.signalView);
        slider = (ScrollView) findViewById(R.id.messages);


        Intent serviceIntent = new Intent(this, NetworkService.class);
        serviceIntent.putExtra("port", DEFAULT_PORT);
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

        //Sets the visibility of the signal
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean toggleVisibility = prefs.getBoolean("toggle", true);
        startMessage = prefs.getString("start", "");
        stopMessage = prefs.getString("stop", "");

        if (toggleVisibility)
            sig.setVisibility(VISIBLE);
        else
            sig.setVisibility(GONE);

        scrollToBottom();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SETTINGS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                sig.setVisibility(data.getBooleanExtra("signal", true) ? VISIBLE : GONE);

                Intent servIntent = new Intent(this, NetworkService.class);
                servIntent.putExtra("port", data.getIntExtra("port", DEFAULT_PORT));
                startService(servIntent);

                String dir = data.getStringExtra("folder");
                if (dir != null)
                    CacheLog.setFileDir(dir);

                startMessage = data.getStringExtra("start");
                stopMessage = data.getStringExtra("stop");
            }
        }
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
                startActivityForResult(intent, SETTINGS_REQUEST_CODE);
                break;
            case R.id.action_log:
                intent = new Intent(this, LogDisplay.class);
                startActivity(intent);
                break;
            case R.id.action_file_out:
                CacheLog.clearData("cleared_data");
            case R.id.action_clear:
                scroll.removeAllViews();
                break;


        }


        return super.onOptionsItemSelected(item);
    }

    private void messageSelector(String message) {

        String[] tokens = message.split(delim);

        if (tokens == null)
            return;

        switch (tokens[0]) {
            case "left":
                sig.setLeft();
                CacheLog.writeData("left", "");
                signalCooldown();
                break;
            case "center":
                sig.setCenter();
                CacheLog.writeData("center", "");
                signalCooldown();
                break;
            case "right":
                sig.setRight();
                CacheLog.writeData("right", "");
                signalCooldown();
                break;

            case "start":

                if (tokens.length > 1)
                    CacheLog.setFileName("Subject-" + tokens[1]);
                else
                    CacheLog.setFileName("Subject-noname");

                if (startMessage != null && startMessage.length() >= 1) {
                    CacheLog.writeData("start", startMessage);
                    setMessage(startMessage);
                    return;
                }
                else {
                    if (tokens.length > 1)
                        CacheLog.writeData("start", tokens[1]);
                    else
                        CacheLog.writeData("start", "");
                }
                break;

            case "stop":
                //CacheLog.writeData("Received", tokens[0]);
                if (stopMessage != null && stopMessage.length() >= 1) {
                    setMessage(stopMessage);
                    CacheLog.writeData("stop",stopMessage);
                    return;
                }
                else {
                    if (tokens.length > 1)
                        CacheLog.writeData("stop", tokens[1]);
                    else
                        CacheLog.writeData("stop", "");
                }

                CacheLog.clearData(null);
                break;
            default:
                if (tokens.length > 1)
                    setMessage(tokens[1]);

                break;

        }

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
        filler.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 25, 1f));
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
        Handler handler = new Handler();
        handler.postDelayed(
                new Thread() {
                    @Override
                    public void run() {
                        sig.reset();
                    }
                }, COOLDOWN_TIME);
    }


}
