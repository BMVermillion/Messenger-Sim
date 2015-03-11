package com.example.iems.testapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkService extends Service implements Runnable {
    private ServerSocket welcomeSocket;
    private Thread thread;
    private boolean closeThread;


    @Override
    public void onCreate() {
        super.onCreate();

        thread = new Thread(this);
        closeThread = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!CacheLog.isInstantiated())
            CacheLog.instantiate();

        if (thread.getState() == Thread.State.NEW && thread != null) {
            CacheLog.writeLog("Thread starting");
            thread.start();
        }
        else
            CacheLog.writeLog("Service Already running");

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        try {
            welcomeSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        closeThread = true;
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


        try {
            welcomeSocket = new ServerSocket(50001);
        } catch (IOException e) {
            e.printStackTrace();

        }

        CacheLog.writeLog("Starting Networking Loop...");
        while(!closeThread)
        {
            BufferedReader inFromClient;
            try {

                Socket connectionSocket = welcomeSocket.accept();
                inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

                networkMessage = inFromClient.readLine();
                CacheLog.writeLog("Received: " + networkMessage);
                outToClient.writeBytes(networkMessage);

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
    }

}
