package com.example.iems.testapp.display;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

/**
 * Created by IEMS on 2/16/2015.
 */
public class MessageBox extends RelativeLayout {
   // private Circle circle;
    private TextView text;
    private String data;


    public MessageBox(Context context, boolean orientation, String text) {
        super(context);
        data = text;
        init(context, orientation);
    }

    private void init(Context context, boolean orientation) {

        int sides = getPix(60f);
        int tb = getPix(15f);
        int minHeight = getPix(70f);
        int innerPadding = getPix(10f);

        text = new TextView(context);
        //circle = new Circle(context, (orientation ? "D" : "A"));

        text.setText(data);
        text.setTextSize(20.0f);
        text.setMinHeight(minHeight);
        text.setPadding(innerPadding,innerPadding,innerPadding,innerPadding);


       // LayoutParams c = new LayoutParams(padding, padding);
       // Space space = new Space(context);
       // space.setBackgroundColor(Color.GREEN);
       // space.setVisibility(VISIBLE);

        LayoutParams t = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        if (orientation) {

            //circle.setLayoutParams(c);
           // c.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            t.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            this.setPadding(sides, tb, tb, tb);
            text.setGravity(Gravity.RIGHT);
            text.setBackgroundColor(Color.rgb(133,194,255));

           // text.setLayoutParams(t);
        } else {

            //circle.setLayoutParams(c);
            //c.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            t.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            this.setPadding(tb, tb, sides, tb);
            text.setGravity(Gravity.LEFT);
            text.setBackgroundColor(Color.rgb(180, 222, 255));
            //text.setLayoutParams(t);
        }
       //space.setLayoutParams(c);
        text.setLayoutParams(t);

        //this.addView(space);
        this.addView(text);

    }

    private int getPix(float dp) {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        dp = metrics.density * dp;
        return(int) (dp + 0.5f);

    }

    public void changeBackground() {
        text.setBackgroundColor(Color.LTGRAY);
    }
}

