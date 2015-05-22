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

import com.example.iems.testapp.display.MessageBox;
import com.example.iems.testapp.display.Signal;
import com.example.iems.testapp.message.CacheLog;
import com.example.iems.testapp.message.MessageFilter;
import com.example.iems.testapp.network.NetworkReciever;

import static android.widget.LinearLayout.GONE;
import static android.widget.LinearLayout.VISIBLE;


public class MainActivity extends ActionBarActivity {

    public final int SETTINGS_REQUEST_CODE = 1;
    private final int DEFAULT_PORT = 50001;

    //Activity Elements
    private ScrollView slider;
    private LinearLayout scroll;
    private EditText text;
    private Signal sig;
    private Button send;
    private MessageBox lastMessage;

    //Message variables
    //private String startMessage;
    //private String stopMessage;

    //Control variables
    private boolean hiddenBox = false;
    private int settingsHit = 0;
    private long lastHit = 0;

    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        //Register a receiver to get messages from the network service
        IntentFilter filter = new IntentFilter();
        filter.addAction("Left");
        filter.addAction("Center");
        filter.addAction("Right");
        filter.addAction("Message");

        LocalBroadcastManager.getInstance(this).registerReceiver(new MainReceiver(this), new IntentFilter("SEND"));

        //Load elements from layout
        scroll = (LinearLayout) findViewById(R.id.scrollLayout);
        text = (EditText) findViewById(R.id.editText);
        sig = (Signal) findViewById(R.id.signalView);
        slider = (ScrollView) findViewById(R.id.messages);
        send = (Button) findViewById(R.id.button);

        //Start the network service
        Intent serviceIntent = new Intent(this, NetworkReciever.class);
        serviceIntent.putExtra("port", DEFAULT_PORT);
        startService(serviceIntent);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Load the setting variables saved in the phone
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean toggleVisibility = prefs.getBoolean("toggle", true);
        hiddenBox = prefs.getBoolean("single", false);
        //startMessage = prefs.getString("start", "");
        //stopMessage = prefs.getString("stop", "");

        //Toggle signal visibility
        if (toggleVisibility)
            sig.setVisibility(VISIBLE);
        else
            sig.setVisibility(GONE);

        //Switch between single button mode
        if (hiddenBox)
            hideTextBox();
        else
            unhideTextBox();

        //Automatically scrolls to the most recent message
        scrollToBottom();

        //MessageFilter.startActionFocus(this, System.currentTimeMillis());
    }

    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(
                "android.intent.action.MAIN");

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.wtf("Intent", "Recieved intent for main activity: " + intent.getAction());
                switch (intent.getStringExtra("TAG")) {
                    case "Left":
                        signalLeft();
                        break;
                    case "Center":
                        signalCenter();
                        break;
                    case "Right":
                        signalRight();
                        break;
                    case "Message":
                        String message = intent.getStringExtra("MESSAGE");
                        if (message != null)
                            setMessage(message);
                }
            }

        };

        this.registerReceiver(mReceiver, intentFilter);
    }

    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(this.mReceiver);
    }
    //Changes to make when returning from the settings activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SETTINGS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                sig.setVisibility(data.getBooleanExtra("signal", true) ? VISIBLE : GONE);

                Intent servIntent = new Intent(this, NetworkReciever.class);
                servIntent.putExtra("port", data.getIntExtra("port", DEFAULT_PORT));
                startService(servIntent);

                String dir = data.getStringExtra("folder");
                if (dir != null)
                    CacheLog.setFileDir(dir);

                //startMessage = data.getStringExtra("start");
                //stopMessage = data.getStringExtra("stop");
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
            case R.id.action_file_out:
                CacheLog.closeFile();
            case R.id.action_clear:
                scroll.removeAllViews();
                break;


        }


        return super.onOptionsItemSelected(item);
    }






    public void setMessage(String s) {
        if (s.equals(""))
            return;

        if (lastMessage != null)
            lastMessage.changeBackground();

        lastMessage = new MessageBox(this, true, s);
        post(lastMessage);
    }

    public void setMessage(View view) {

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastHit <= 500)
            settingsHit++;
        else
            settingsHit = 0;

        if (settingsHit == 5) {
            Intent intent = new Intent(this, Settings.class);
            startActivityForResult(intent, SETTINGS_REQUEST_CODE);
        }
        lastHit = currentTime;

        if (hiddenBox) {
            if (lastMessage != null)
                lastMessage.changeBackground();

            MessageFilter.startActionSend(this, "Confirmation Sent", System.currentTimeMillis());
            lastMessage = new MessageBox(this, false, "Confirmation Sent");
            post(lastMessage);
            return;
        }

        Editable userInput = text.getText();

        if (userInput.toString().equals(""))
            return;

        MessageFilter.startActionSend(this, userInput.toString(), System.currentTimeMillis());

        if (lastMessage != null)
            lastMessage.changeBackground();

        lastMessage = new MessageBox(this, false, userInput.toString());
        post(lastMessage);

        text.setText("");

        //hideSoftKeyboard(MainActivity.this, view);
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
                }, 1000);
    }

    public void onTextBoxClick(View view) {
        MessageFilter.startActionClick(this, System.currentTimeMillis());
        scrollToBottom();

    }

    private static void hideSoftKeyboard (Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    public void signalLeft() {
        sig.setLeft();
        signalCooldown();
    }
    public void signalCenter() {
        sig.setCenter();
        signalCooldown();
    }
    public void signalRight() {
        sig.setRight();
        signalCooldown();
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
