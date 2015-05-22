package com.example.iems.testapp.network;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.example.iems.testapp.message.CacheLog;
import com.example.iems.testapp.message.MessageFilter;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class NetworkOut extends IntentService {

    private static final String ACTION_MESSAGE_OUT = "com.example.iems.testapp.network.action.MESSAGE_OUT";
    private static final String ACTION_SETTINGS = "com.example.iems.testapp.network.action.SETTINGS";

    private static final String MESSAGE = "com.example.iems.testapp.network.extra.MESSAGE";
    private static final String IP = "com.example.iems.testapp.network.extra.IP";
    private static final String PORT = "com.example.iems.testapp.network.extra.PORT";

    public static void startActionMessageOut(Context context, String message) {
        Intent intent = new Intent(context, NetworkOut.class);
        intent.setAction(ACTION_MESSAGE_OUT);
        intent.putExtra(MESSAGE, message);
        context.startService(intent);
    }

    public static void startActionSettings(Context context, String ip, int port) {
        Intent intent = new Intent(context, NetworkOut.class);
        intent.setAction(ACTION_SETTINGS);
        intent.putExtra(IP, ip);
        intent.putExtra(PORT, port);
        context.startService(intent);
    }


    private String IPAddress;
    private int port;

    public NetworkOut() {
        super("NetworkOut");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.wtf("NetworkOut", intent.getAction());
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_MESSAGE_OUT.equals(action)) {
                final String param1 = intent.getStringExtra(MESSAGE);
                handleActionMessageOut(param1);
            }
            else if (ACTION_SETTINGS.equals(action)) {
                final String ip = intent.getStringExtra(IP);
                final int port = intent.getIntExtra(PORT,50003);
                Log.wtf("NetOut", "ip:"+ip+" port:"+port);
                setNetworkParams(ip, port);
            }
        }
    }


    private void handleActionMessageOut(String message) {

        if (IPAddress == null || port == 0)
            onMessageFail("No IP", message);


        Socket clientSocket = null;
        try {
            clientSocket = new Socket(IPAddress, port);
        } catch (IOException e) {
            onMessageFail("Failed to create socket", message);
            return;
        }

        DataOutputStream outToControler = null;
        if (clientSocket != null) {
            try {
                outToControler = new DataOutputStream(clientSocket.getOutputStream());
            } catch (IOException e) {
                onMessageFail("Data stream init error", message);
                return;
            }
        }
        else {
            onMessageFail("Null socket", message);
            return;
        }

        try {
            outToControler.writeBytes(message);
            outToControler.flush();
        } catch (IOException e) {
            onMessageFail("Failed to send", message);
        }
    }

   private void onMessageFail(String how, String message) {
       MessageFilter.startActionFail(this, message, how);
   }

   private void setNetworkParams(String ip, int port) {
       IPAddress = ip;
       this.port = port;
   }
}
