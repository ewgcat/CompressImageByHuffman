package com.lishuaihua.test;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

public class TouchEventViewPager extends ViewPager {

    public TouchEventViewPager(@NonNull Context context) {
        super(context);
    }

    public TouchEventViewPager(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 重写onInterceptTouchEvent()方法来解决图片点击缩小时候的Crash问题
     *
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        try {
            return super.onInterceptTouchEvent(event);
        } catch (IllegalArgumentException  e) {
            Log.e( "ImageOriginPager-error" , "IllegalArgumentException 错误被活捉了！");
            e.printStackTrace();
        }
        return false ;
    }

}
