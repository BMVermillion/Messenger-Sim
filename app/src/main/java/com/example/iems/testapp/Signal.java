package com.example.iems.testapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by IEMS on 2/11/2015.
 */
public class Signal extends View {

    private int w;
    private int h;
    private Paint outer;
    private Paint inner;
    private Paint flash;
    private int r;

    private boolean left;
    private boolean center;
    private boolean right;


    public Signal(Context context) {
        super(context);
        init();
    }

    public Signal(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Signal(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);

        outer = new Paint();
        outer.setStyle(Paint.Style.FILL);
        outer.setColor(Color.parseColor("#FF3300"));
        outer.setFlags(Paint.ANTI_ALIAS_FLAG);

        inner = new Paint();
        inner.setStyle(Paint.Style.FILL);
        inner.setColor(Color.parseColor("#CC2900"));
        inner.setFlags(Paint.ANTI_ALIAS_FLAG);

        flash = new Paint();
        flash.setStyle(Paint.Style.FILL);
        flash.setColor(Color.YELLOW);
        flash.setFlags(Paint.ANTI_ALIAS_FLAG);

        r = 50;
        right = false;
        center = false;
        left = false;


    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.w = w;
        this.h = h;

        r = h / 3;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int div = w / 3;


        System.out.println("Width = " + w + " Height = " + h);
        for (int i = 1; i <= 3; i++) {
            canvas.drawCircle(div * i - div / 2, h / 2, r, outer);
            canvas.drawCircle(div * i - div / 2, h / 2, r - 10, inner);
        }


        if (left) {
            canvas.drawCircle(div - div / 2, h / 2, r - 10, flash);
            left = false;
        }

        if (right) {
            canvas.drawCircle(div * 3 - div / 2, h / 2, r - 10, flash);
            right = false;
        }

        if (center) {
            canvas.drawCircle(div * 2 - div / 2, h / 2, r - 10, flash);
            center = false;
        }


    }

    public void setLeft() {
        left = true;
        postInvalidate();
    }

    public void setRight() {
        right = true;
        postInvalidate();
    }

    public void setCenter() {
        center = true;
        postInvalidate();
    }

    public void reset() {
        left = false;
        center = false;
        right = false;
        postInvalidate();
    }


}
