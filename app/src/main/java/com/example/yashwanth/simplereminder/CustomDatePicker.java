package com.example.yashwanth.simplereminder;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.DatePicker;

/**
 * Created by Yashwanth on 20-Dec-16.
 */

public class CustomDatePicker extends DatePicker {
    public CustomDatePicker(Context context) {
        super(context);
    }

    public CustomDatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomDatePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN)
        {
            ViewParent p = getParent();

            if (p != null)
                p.requestDisallowInterceptTouchEvent(true);
        }

        return false;
    }
}
