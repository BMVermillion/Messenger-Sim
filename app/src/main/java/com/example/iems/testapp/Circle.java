package com.example.iems.testapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

/**
 * Created by IEMS on 2/16/2015.
 */
class Circle extends View {
    private int w = 0;
    private int h = 0;
    private Paint paint;
    private Paint font;
    private String user;

    private int x;
    private int y;
    private int r;
    private int offset;

    public Circle(Context context, String user) {
        super(context);


        this.user = user;

        this.setBackgroundColor(Color.LTGRAY);

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);

        font = new Paint();

        font.setStyle(Paint.Style.STROKE);
        font.setColor(Color.BLACK);
        font.setTextAlign(Paint.Align.CENTER);
    }


    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(x, y, r, paint);
        canvas.drawText(user, x, y + offset, font);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.w = w;
        this.h = h;

        x = w / 2;
        y = w / 2;
        r = h / 2 - 10;

        int size = 0;
        while (true) {
            font.setTextSize(++size);
            if (font.measureText(user) > r) {
                font.setTextSize(--size);

                Rect rect = new Rect();
                font.getTextBounds(user, 0, 1, rect);
                offset = rect.height() / 2;
                break;
            }
        }

        System.err.println("Help");
        //super.onSizeChanged(w, h, oldw, oldh);
    }
}