package com.example.iems.testapp.display;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by IEMS on 5/29/2015.
 */
public class MyScroll extends ScrollView {

    public MyScroll(Context c) {
        super(c);
    }

    public MyScroll(Context c, AttributeSet attrs) {
        super(c, attrs);
    }

    public MyScroll(Context c, AttributeSet attrs, int defStyle) {
        super(c, attrs, defStyle);
    }



    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        post(new Runnable() {
            public void run() {
                fullScroll(View.FOCUS_DOWN);
            }
        });
    }
}
