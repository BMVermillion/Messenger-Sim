package com.example.iems.testapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkService extends Service implements Runnable {
    private NotificationManager noteManager;
    private ServerSocket socket;
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

        if (!CacheLog.isInstantiated())
            CacheLog.instantiate();

        if (thread.getState() == Thread.State.NEW && thread != null) {
            CacheLog.writeLog("Thread starting");
            try {
                socket = new ServerSocket(port);
            } catch (IOException e) {
                e.printStackTrace();

            }
            thread.start();
        } else {
            CacheLog.writeLog("Service Already running");
            int getPort = intent.getIntExtra("port", port);
            if (getPort != port) {
                CacheLog.writeLog("Changing port to: " + getPort);
                try {
                    port = getPort;
                    socket.close();
                    socket = new ServerSocket(port);

                    Thread t = new Thread(this);
                    t.start();
                } catch (IOException e) {
                    CacheLog.writeLog("Failed to open/close port");
                    CacheLog.writeLog(e.getMessage());
                }


            }
        }

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        try {
            socket.close();
        } catch (IOException e) {
            CacheLog.writeLog("Failed to close port");
            CacheLog.writeLog(e.getMessage());
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
        CacheLog.writeLog("Successfully started thread");
        String networkMessage;


        ServerSocket localSocket = socket;

        CacheLog.writeLog("Starting Networking Loop...");
        while (!localSocket.isClosed()) {
            BufferedReader reader;
            try {

                Socket connectionSocket = localSocket.accept();
                reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

                networkMessage = reader.readLine();
                CacheLog.writeLog("Received: " + networkMessage);

                sendMessage(networkMessage);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void sendMessage(String message) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra("message", message);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        NotificationCompat.Builder notificationBuild = new NotificationCompat.Builder(this);
        notificationBuild.setSmallIcon(android.R.drawable.stat_notify_chat);
        notificationBuild.setContentTitle("New Message");
        notificationBuild.setContentText("You have received a new message.");

        Notification notification = notificationBuild.build();
        notification.defaults |= Notification.DEFAULT_SOUND;

        noteManager.notify(0,notification);
    }

}
