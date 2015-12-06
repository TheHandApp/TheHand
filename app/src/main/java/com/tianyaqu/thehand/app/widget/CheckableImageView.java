package com.tianyaqu.thehand.app.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageView;
import com.tianyaqu.thehand.app.R;

/**
 * Created by Alex on 2015/11/30.
 */
public class CheckableImageView extends ImageView implements Checkable{
    private boolean mChecked = false;

    public CheckableImageView(Context context) {
        super(context);
    }

    public CheckableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mChecked){
            Bitmap check = BitmapFactory.decodeResource(
                    getResources(), R.drawable.check);
            int width = check.getWidth();
            int height = check.getHeight();
            int margin = 10;
            int x = canvas.getWidth() - width - margin;
            int y = canvas.getHeight() - height - margin - 15;
            canvas.drawBitmap(check,x,y,new Paint());
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void setChecked(boolean checked) {
        //so setups
        mChecked = !mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
        invalidate();
    }
}
