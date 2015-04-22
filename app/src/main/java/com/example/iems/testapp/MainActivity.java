package com.example.iems.testapp;

import android.app.Activity;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;

import static android.widget.LinearLayout.GONE;
import static android.widget.LinearLayout.VISIBLE;


public class MainActivity extends ActionBarActivity {

    public final int SETTINGS_REQUEST_CODE = 1;
    private final int DEFAULT_PORT = 50001;
    private final int COOLDOWN_TIME = 1000;

    private ScrollView slider;
    private LinearLayout scroll;
    private EditText text;
    private Signal sig;
    private String startMessage;
    private String stopMessage;
    private Button send;
    private MessageBox lastMessage;

    public static int width;
    public static int height;

    public boolean started = false;
    private boolean hiddenBox = false;
    private int settings = 0;
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
        send = (Button) findViewById(R.id.button);

        Intent serviceIntent = new Intent(this, NetworkService.class);
        serviceIntent.putExtra("port", DEFAULT_PORT);
        startService(serviceIntent);

        registerForContextMenu(send);

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

        if (CacheLog.isInstantiated())
            CacheLog.writeLog("Starting MainActivity");

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean toggleVisibility = prefs.getBoolean("toggle", true);
        hiddenBox = prefs.getBoolean("single", false);
        startMessage = prefs.getString("start", "");
        stopMessage = prefs.getString("stop", "");

        if (toggleVisibility)
            sig.setVisibility(VISIBLE);
        else
            sig.setVisibility(GONE);

        if (hiddenBox)
            hideTextBox();
        else
            unhideTextBox();

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

        String[] tokens = message.split("_");

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
                started = true;
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

        if (lastMessage != null)
            lastMessage.changeBackground();

        lastMessage = new MessageBox(this, true, Editable.Factory.getInstance().newEditable(s));
        post(lastMessage);
    }

    public void setMessage(View view) {

        if (hiddenBox) {
            if (lastMessage != null)
                lastMessage.changeBackground();

            CacheLog.writeData("confirm","Button press");
            lastMessage = new MessageBox(this, false, Editable.Factory.getInstance().newEditable("Confirmation Sent"));
            post(lastMessage);
            return;
        }

        Editable userInput = text.getText();
        if (userInput.toString().equals("")) {
            if (++settings == 5) {
                Intent intent = new Intent(this, Settings.class);
                startActivityForResult(intent, SETTINGS_REQUEST_CODE);
                Log.wtf("Menu", "Opening settings");
                settings = 0;
            }
            return;
        }
        settings = 0;

        CacheLog.writeData("post", userInput.toString(), System.currentTimeMillis());
        CacheLog.writeLog("Posted \"" + userInput.toString() + "\"");


        if (lastMessage != null)
            lastMessage.changeBackground();

        lastMessage = new MessageBox(this, false, userInput);
        post(lastMessage);

        text.setText("");

        hideSoftKeyboard(MainActivity.this, view);
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

    public void onTextBoxClick(View view) {
            CacheLog.writeData("click", "");
            CacheLog.writeLog("User clicked text box");
    }

    private static void hideSoftKeyboard (Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    /*
    Functions for changing the app to single button mode. This mode
    sends a confirmation message in place of a user response.
     */
    //////////////////////////////////////
    private void hideTextBox() {
        text.setVisibility(View.GONE);
        send.setText("Confirm");
    }

    private void unhideTextBox() {
        text.setVisibility(View.VISIBLE);
        send.setText("Send");
    }
    //////////////////////////////////////
}
