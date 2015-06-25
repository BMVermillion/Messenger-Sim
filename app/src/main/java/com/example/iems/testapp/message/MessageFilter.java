package com.example.iems.testapp.message;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.example.iems.testapp.network.NetworkOut;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */

public class MessageFilter extends IntentService {

    private static final String ACTION_START = "com.example.iems.testapp.message.action.START";
    private static final String ACTION_STOP = "com.example.iems.testapp.message.action.STOP";
    private static final String ACTION_PICKUP = "com.example.iems.testapp.message.action.PICKUP";
    private static final String ACTION_FOCUS = "com.example.iems.testapp.message.action.FOCUS";
    private static final String ACTION_CLICK = "com.example.iems.testapp.message.action.CLICK";
    private static final String ACTION_SEND = "com.example.iems.testapp.message.action.SEND";
    private static final String ACTION_PUTDOWN = "com.example.iems.testapp.message.action.PUTDOWN";
    private static final String ACTION_MESSAGE = "com.example.iems.testapp.message.action.MESSAGE";
    //private static final String ACTION_EVENT = "com.example.iems.testapp.message.action.EVENT";
    private static final String ACTION_FAIL = "com.example.iems.testapp.message.action.FAIL";
    private static final String ACTION_SIGNAL = "com.example.iems.testapp.message.action.SIGNAL";


    private static String EventAction;
    private static String EventMessage;

    // TODO: Rename parameters
    private static final String MESSAGE = "com.example.iems.testapp.message.extra.MESSAGE";
    private static final String EXTRA = "com.example.iems.testapp.message.extra.EXTRA";
    private static final String ACTION = "com.example.iems.testapp.message.extra.ACTION";
    private static final String RET_MESSAGE = "com.example.iems.testapp.message.extra.RETMESSAGE";
    private static final String TIME = "com.example.iems.testapp.message.extra.TIME";



    public static void startActionStart(Context context) {
        Intent intent = new Intent(context, MessageFilter.class);
        intent.setAction(ACTION_START);
        context.startService(intent);
    }

    public static void startActionStop(Context context) {
        Intent intent = new Intent(context, MessageFilter.class);
        intent.setAction(ACTION_STOP);
        context.startService(intent);
    }

    //TODO: Future feature possibility
    public static void startActionPickUp(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MessageFilter.class);
        //intent.setAction(ACTION_FOO);
       // intent.putExtra(EXTRA_PARAM1, param1);
        //intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    //TODO: Future feature possibility
    public static void startActionPutDown(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MessageFilter.class);
        //intent.setAction(ACTION_FOO);
        //intent.putExtra(EXTRA_PARAM1, param1);
        //intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public static void startActionFocus(Context context, long time) {
        Intent intent = new Intent(context, MessageFilter.class);
        intent.setAction(ACTION_FOCUS);
        intent.putExtra(TIME, time);
        context.startService(intent);
    }
    public static void startActionClick(Context context, long time) {
        Intent intent = new Intent(context, MessageFilter.class);
        intent.setAction(ACTION_CLICK);
        intent.putExtra(TIME, time);
        context.startService(intent);
    }

    public static void startActionSend(Context context, String message, long time) {
        Intent intent = new Intent(context, MessageFilter.class);
        intent.setAction(ACTION_SEND);
        intent.putExtra(TIME, time);
        intent.putExtra(MESSAGE, message);
        context.startService(intent);
    }
    public static void startActionMessage(Context context, String message, String action, String command, long time) {
        Intent intent = new Intent(context, MessageFilter.class);
        intent.setAction(ACTION_MESSAGE);
        intent.putExtra(MESSAGE, message);
        intent.putExtra(ACTION, action);
        intent.putExtra(RET_MESSAGE, command);
        intent.putExtra(TIME, time);

        context.startService(intent);
    }
    public static void startActionFail(Context context, String message, String how) {
        Intent intent = new Intent(context, MessageFilter.class);
        intent.setAction(ACTION_FAIL);
        intent.putExtra(MESSAGE, message);
        intent.putExtra(EXTRA, how);
        context.startService(intent);
    }
    public static void startActionSignal(Context context, String sig) {
        Intent intent = new Intent(context, MessageFilter.class);
        intent.setAction(ACTION_SIGNAL);
        intent.putExtra(MESSAGE, sig);
        context.startService(intent);
    }

    public MessageFilter() {
        super("MessageFilter");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.wtf("MessageHandler", intent.getAction());
            Log.wtf("Message", EventAction + " -- " + EventMessage);


            final String action = intent.getAction();
            checkEvent(action);

            if (ACTION_START.equals(action)) {
                CacheLog.newFile();
            } else if (ACTION_STOP.equals(action)) {
                CacheLog.closeFile();
            } else if (ACTION_PICKUP.equals(action)) {
                //final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                //final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                //handleActionBaz(param1, param2);
            } else if (ACTION_PUTDOWN.equals(action)) {
                //final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                //final String param2 = intent.getStringExtra(EXTRA_PARAM2);
               // handleActionBaz(param1, param2);
            } else if (ACTION_CLICK.equals(action)) {
                final long time = intent.getLongExtra(TIME,0);
                record("Click", "", time);
            } else if (ACTION_SEND.equals(action)) {
                final long time = intent.getLongExtra(TIME,0);
                final String message = intent.getStringExtra(MESSAGE);
                record("Send",message,time);
            } else if (ACTION_FOCUS.equals(action)) {
                final long time = intent.getLongExtra(TIME, 0);
                record("Focus", "", time);
            } else if (ACTION_MESSAGE.equals(action)) {
                final String message = intent.getStringExtra(MESSAGE);
                final String event_action = intent.getStringExtra(ACTION);
                final String command = intent.getStringExtra(RET_MESSAGE);
                final long time = intent.getLongExtra(TIME,0);
                //Log.wtf("Message", event_action + " -- " + command);

                if (event_action != null && event_action != "") {
                    Log.wtf("Message", "Writing to EventAction");
                    EventAction = "com.example.iems.testapp.message.action." + event_action.toUpperCase();
                }

                if (command != null && command != "")
                    EventMessage = command;

                Log.wtf("Message", "EventAction: " + EventAction + " EventMessage: " + EventMessage);
                CacheLog.writeToFile("Received", message, time);
            } else if (ACTION_FAIL.equals(action)) {
                final String message = intent.getStringExtra(MESSAGE);
                final String how = intent.getStringExtra(EXTRA);
                record("Failed to send", "reason: " + how + ", message: " + message );
            }
            else if (ACTION_SIGNAL.equals(action)) {
                final String sig = intent.getStringExtra(MESSAGE);
                record(sig, "");
            }


        }
    }


    private void record(String tag, String message) {
        CacheLog.writeToFile(tag, message);
    }

    private void record(String tag, String message, long time) {
        CacheLog.writeToFile(tag, message);
    }

    private void initFile(String subject, String csvFile) {
        CacheLog.setFileParams("Subject-" + subject + "-" + csvFile);
    }

    private void checkEvent(String tag) {
        if (EventAction != null && EventAction.equals(tag)) {
            NetworkOut.startActionMessageOut(this, EventMessage);
            EventAction = null;
            EventMessage = null;
        }


    }

}
