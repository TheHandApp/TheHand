package com.tianyaqu.thehand.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by Alex on 2015/11/16.
 */

public class ArticleScrollView extends ScrollView {
    public interface ScrollDirectionListener {
        public void onScrollUp();
        public void onScrollDown();
        public void onScrollCompleted();
    }
    private float mLastMotionY;
    private int mInitialScrollCheckY;
    private boolean mIsMoving;
    private static final int SCROLL_CHECK_DELAY = 100;

    private ScrollDirectionListener mScrollDirectionListener;

    public ArticleScrollView(Context context) {
        super(context);
    }

    public ArticleScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ArticleScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setScrollDirectionListener(ScrollDirectionListener listener) {
        mScrollDirectionListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mScrollDirectionListener != null) {
            int action = event.getAction() & MotionEvent.ACTION_MASK;

            switch (action) {
                case MotionEvent.ACTION_MOVE :
                    if (mIsMoving) {
                        int yDiff = (int) (event.getY() - mLastMotionY);
                        if (yDiff < 0) {
                            mScrollDirectionListener.onScrollDown();
                        } else if (yDiff > 0) {
                            mScrollDirectionListener.onScrollUp();
                        }
                        mLastMotionY = event.getY();
                    } else {
                        mIsMoving = true;
                        mLastMotionY = event.getY();
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    if (mIsMoving) {
                        mIsMoving = false;
                        startScrollCheck();
                    }
                    break;

                default :
                    mIsMoving = false;
                    break;
            }
        }

        return super.onTouchEvent(event);
    }

    private void startScrollCheck() {
        mInitialScrollCheckY = getScrollY();
        post(mScrollTask);
    }

    private final Runnable mScrollTask = new Runnable() {
        @Override
        public void run() {
            if (mInitialScrollCheckY == getScrollY()) {
                if (mScrollDirectionListener != null) {
                    mScrollDirectionListener.onScrollCompleted();
                }
            } else {
                mInitialScrollCheckY = getScrollY();
                postDelayed(mScrollTask, SCROLL_CHECK_DELAY);
            }
        }
    };

    public boolean canScrollUp() {
        return canScrollVertically(-1);
    }
    public boolean canScrollDown() {
        return canScrollVertically(1);
    }
}
