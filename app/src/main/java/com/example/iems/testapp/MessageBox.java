package com.example.iems.testapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.text.Editable;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

/**
 * Created by IEMS on 2/16/2015.
 */
public class MessageBox extends RelativeLayout {
    Circle circle;
    TextView text;
    String data;

    public MessageBox(Context context, boolean orientation, Editable text) {
        super(context);
        data = text.toString();
        init(context, orientation);
    }

    private void init(Context context, boolean orientation) {

        this.setBackgroundColor(Color.LTGRAY);
        this.setPadding(20,10,20,10);


        text = new TextView(context);
        circle = new Circle(context, (orientation?"D":"A"));

        text.setText(data);
        text.setTextSize(20.0f);

        LayoutParams c = new LayoutParams(100, 100);
        LayoutParams t = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        if (orientation) {
            c.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            circle.setLayoutParams(c);

            t.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            text.setLayoutParams(t);
        }
        else {
            c.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            circle.setLayoutParams(c);

            t.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            text.setLayoutParams(t);
        }

        this.addView(circle);
        this.addView(text);


    }



}

