package com.mobilelearning.student.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

/**
 * This class provide screen size capture. So, we can get screen size of device programatically
 * Created by Taofik Muhammad on 11/02/2017.
 */
public class ScreenSize {
    private int width;
    private int height;


    public ScreenSize(Activity context)
    {
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.width = size.x;
        this.height = size.y;
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getHeight()
    {
        return this.height;
    }
}
