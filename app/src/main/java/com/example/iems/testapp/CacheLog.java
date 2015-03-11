package com.example.iems.testapp;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import static android.os.Environment.*;

/**
 * Created by IEMS on 3/3/2015.
 */
public class CacheLog {
    private static final String fileName = "Log.txt";
    private static ArrayList<String> log;
    private static ArrayList<String> data;
    private static long startTime = 0;
    private static long startLogTime = 0;

    public static void instantiate() {
        if (log == null)
            log = new ArrayList<String>();

        if (data == null) {
            data = new ArrayList<String>();
            data.add("TAG, MESSAGE, TIME");
        }



    }
    public static boolean isInstantiated() {
        if (log == null)
            return false;
        return true;
    }

    public static void writeLog(String info) {
        long time = System.currentTimeMillis();
        if (startLogTime == 0)
            startLogTime = time;

        log.add( "[" + (time-startLogTime) + "]:  " + info + "\n");
    }

    public static void writeData(String tag, String message) {
        writeData(tag, message,System.currentTimeMillis());
    }

    public static void writeData(String tag, String message, Long time) {
        Log.wtf("Data", message);
        Log.wtf("Size", String.valueOf(data.size()));
        if (startTime == 0)
            startTime = time;

        data.add( tag + ", " + message + ", " + (time - startTime) + "\n");
    }

    public static void clearData(String file) {

        CacheLog.outToFile(file);

        CacheLog.writeLog("Clearing Data!!!");
        startTime = 0;
        data.clear();
        data.add("TAG, MESSAGE, TIME");
    }

    public static ArrayList<String> getLog() {
        return log;
    }

    private static void outToFile(String name) {

        if (!isExternalStorageWritable())
            return;

        CacheLog.writeLog("Media is mounted");
        File file = Environment.getExternalStorageDirectory();

        File sim = new File(file, "Driving Sim App/");
        sim.getParentFile().mkdirs();
        sim = new File(sim,name+".txt");

        try {
            BufferedWriter bor = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sim)));
            for (String s : data) {
                Log.wtf("test",s);
                bor.write(s);
            }
            bor.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            CacheLog.writeLog("Failed to open file!");
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }


        CacheLog.writeLog("Wrote file: " + sim.toString());

    }

    private static boolean isExternalStorageWritable() {
        String state = getExternalStorageState();
        if (MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

}
