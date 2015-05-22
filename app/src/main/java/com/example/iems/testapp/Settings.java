package com.example.iems.testapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.example.iems.testapp.message.CacheLog;


public class Settings extends ActionBarActivity {

    private Switch toggle;
    private Switch single;
    private EditText startText;
    private EditText stopText;
    private EditText folder;
    private EditText port;
    private SharedPreferences settingPrefs;
    private TextView ipText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toggle = ((Switch) findViewById(R.id.sigToggle));
        single = ((Switch) findViewById(R.id.single));
        startText = ((EditText) findViewById(R.id.editStartText));
        stopText = ((EditText) findViewById(R.id.editStopText));
        folder = ((EditText) findViewById(R.id.folderEdit));
        port = ((EditText) findViewById(R.id.portEdit));
        settingPrefs = getSharedPreferences("settings", MODE_PRIVATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        toggle.setChecked(settingPrefs.getBoolean("toggle", true));
        single.setChecked(settingPrefs.getBoolean("single", false));
        startText.setText(settingPrefs.getString("start", ""));
        stopText.setText(settingPrefs.getString("stop", ""));
        folder.setText(settingPrefs.getString("folder", "Driving Sim"));
        port.setText(settingPrefs.getString("port", "50001"));

    }

    public void resetSettings(View view) {

        toggle.setChecked(true);
        single.setChecked(false);
        startText.setText("");
        stopText.setText("");
        folder.setText("Driving Sim/");
        port.setText("50001");

    }

    public void setSettings(View view) {


        boolean toggleVal = toggle.isChecked();
        boolean singleVal = single.isChecked();
        String startVal = startText.getText().toString();
        String stopVal = stopText.getText().toString();
        String folderVal = folder.getText().toString();
        int portVal = Integer.valueOf(port.getText().toString());

        SharedPreferences.Editor editor = settingPrefs.edit();
        editor.putBoolean("toggle", toggleVal);
        editor.putBoolean("single", singleVal);
        editor.putString("start", startVal);
        editor.putString("stop", stopVal);
        editor.putString("folder", folderVal);
        editor.putString("port", String.valueOf(portVal));
        editor.commit();

        Intent intent = new Intent();
        intent.putExtra("signal", toggleVal);
        intent.putExtra("start", startVal);
        intent.putExtra("stop", stopVal);
        intent.putExtra("folder", folderVal);
        intent.putExtra("port", portVal);

        setResult(RESULT_OK, intent);
        finish();
    }
}
