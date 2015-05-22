package com.example.iems.testapp.network;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.iems.testapp.MainActivity;
import com.example.iems.testapp.message.CacheLog;
import com.example.iems.testapp.message.MessageFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkReciever extends Service implements Runnable {
    private NotificationManager noteManager;
    private ServerSocket socket;
    BufferedReader reader;
    private Thread thread;
    private int port;


    @Override
    public void onCreate() {
        super.onCreate();

        noteManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        thread = new Thread(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (thread.getState() == Thread.State.NEW && thread != null) {
            try {
                socket = new ServerSocket(port);
            } catch (IOException e) {
                e.printStackTrace();

            }
            thread.start();
        } else {
            int getPort = intent.getIntExtra("port", port);
            if (getPort != port) {
                try {
                    port = getPort;
                    socket.close();
                    socket = new ServerSocket(port);

                    Thread t = new Thread(this);
                    t.start();
                } catch (IOException e) {

                }


            }
        }

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        try {
            reader.close();
            socket.close();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (thread.getState() == Thread.State.NEW)
            thread.start();

        return null;
    }

    @Override
    public void run() {
        String networkMessage;


        ServerSocket localSocket = socket;

        while (!localSocket.isClosed()) {
            try {

                Socket connectionSocket = localSocket.accept();
                reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

                networkMessage = reader.readLine();
                connectionSocket.close();
                //sendMessage(networkMessage);

                final String message = networkMessage;
                final long time = System.currentTimeMillis();
                new Thread() {
                    @Override
                    public void run() {
                        parse(message, time);
                    }
                }.start();


            } catch (IOException e) {
            }

        }
    }

    /*
    private void sendMessage(String message) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra("message", message);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);


    }

    private void messageWriteBack(String ip, String message) {
        new Thread() {
            public void run() {

            }
        };
    }

    */

    private void parse(String message, long time) {

        Log.wtf("Network", "Message: " + message);
        String[] tokens = message.split("_");
        Intent intent = new Intent("android.intent.action.MAIN");

        if (tokens == null) {
            return;
        }

        switch (tokens[0]) {
            case "subject":
                CacheLog.setFileParams("Subject-"+tokens[1]+"-"+tokens[2]);
                NetworkOut.startActionSettings(this, tokens[3],Integer.valueOf(tokens[4]));
                break;
            case "left":
                MessageFilter.startActionSignal(this, "Left");
                intent.putExtra("TAG", "Left");
                this.sendBroadcast(intent);
                break;

            case "center":
                MessageFilter.startActionSignal(this, "Center");
                intent.putExtra("TAG", "Center");
                this.sendBroadcast(intent);
                break;

            case "right":
                MessageFilter.startActionSignal(this, "Right");
                intent.putExtra("TAG", "Right");
                this.sendBroadcast(intent);
                break;

            case "start":
                MessageFilter.startActionStart(this);
                break;

            case "stop":
                MessageFilter.startActionStop(this);
                break;

            case "":
            case "message":
                String txt_message = null;
                String action = null;
                String command = null;

                for (int i=1; i<tokens.length; i++) {
                    switch(i) {
                        case 1:
                            txt_message = tokens[i];
                            break;
                        case 2:
                            action = tokens[i];
                            break;
                        case 3:
                            command = tokens[i];
                            break;
                        default:
                    }

                }
                Log.wtf("Network", "in message block");
                MessageFilter.startActionMessage(this, txt_message, action, command, time);
                intent.putExtra("TAG", "Message");
                intent.putExtra("MESSAGE", txt_message);
                this.sendBroadcast(intent);
                break;
        }

        sendNotification();
    }

    private void sendNotification() {
        NotificationCompat.Builder notificationBuild = new NotificationCompat.Builder(this);
        notificationBuild.setSmallIcon(android.R.drawable.stat_notify_chat);
        notificationBuild.setContentTitle("New Message");
        notificationBuild.setContentText("You have received a new message.");

        Notification notification = notificationBuild.build();
        notification.defaults |= Notification.DEFAULT_SOUND;

        noteManager.notify(0,notification);
    }

}
