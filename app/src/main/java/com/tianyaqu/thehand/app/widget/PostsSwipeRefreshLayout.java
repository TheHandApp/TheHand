package com.tianyaqu.thehand.app.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Alex on 2015/11/17.
 */
public class PostsSwipeRefreshLayout extends SwipeRefreshLayout {
    public PostsSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PostsSwipeRefreshLayout(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }
}
