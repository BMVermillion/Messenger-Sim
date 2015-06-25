package com.example.iems.testapp.message;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.os.Environment.MEDIA_MOUNTED;
import static android.os.Environment.getExternalStorageState;

/**
 * Created by IEMS on 3/3/2015.
 */
public class CacheLog {
    private static long startTime = 0;
    private static long startLogTime = 0;

    private static String fileDir = "Driving Sim";
    private static String fileName = "No-file-name-set";
    private static BufferedWriter fileOut;


    public static void writeToFile(String tag, String message) {
        if (fileOut != null)
            try {
                fileOut.write(tag + ", " + message + ", " + (System.currentTimeMillis() - startTime) + "\n");
                fileOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        else {
            newFile();
        }

    }

    public static void writeToFile(String tag, String message, long time) {

        if (fileName == null)
            return;

        if (fileOut != null)
            try {
                fileOut.write(tag + ", " + message + ", " + (time - startTime) + "\n");
                fileOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        else {
            newFile();
        }

    }

    public static void newFile() {
        if (fileOut != null)
            try {
                fileOut.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.wtf("Log", "File error on new file: close");
                return;
            }

        startTime = System.currentTimeMillis();

        if (!isExternalStorageWritable()) {
            return;
        }


        File dir = new File(Environment.getExternalStorageDirectory(), fileDir + "/");
        dir.mkdirs();

        File file = new File(dir, fileName + ".csv");

        int num = 1;
        while(file.exists()) {
            file = new File(dir, fileName + "-" + num + ".csv");
        }

        try {
            fileOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            fileOut.write("TAG, MESSAGE, TIME\n");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }


        writeToFile("start","",startTime);
    }

    public static void setFileParams(String name) {
        fileName = name;
    }

    public static void closeFile() {
        try {
            writeToFile("stop","");
            fileOut.close();
            fileOut = null;
            fileName = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static boolean isExternalStorageWritable() {
        String state = getExternalStorageState();
        if (MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static void setFileDir(String s) {
        fileDir = s;
    }

}
