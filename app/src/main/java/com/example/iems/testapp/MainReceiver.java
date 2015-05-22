package com.example.iems.testapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by IEMS on 5/22/2015.
 */
public class MainReceiver extends BroadcastReceiver{
    private MainActivity main;
    public MainReceiver( MainActivity main ) {
        super();
        this.main = main;

    }

     public void onReceive(Context context, Intent intent) {
         Log.wtf("Intent", "Recieved intent for main activity: " + intent.getAction());
         switch (intent.getStringExtra("TAG")) {
             case "Left":
                 main.signalLeft();
                 break;
             case "Center":
                 main.signalCenter();
                 break;
             case "Right":
                 main.signalRight();
                 break;
             case "Message":
                 String message = intent.getStringExtra("MESSAGE");
                 if (message != null)
                    main.setMessage(message);
         }


     }



}
