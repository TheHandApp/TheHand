package com.tianyaqu.thehand.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Alex on 2015/11/13.
 */
public class PhotoViewerLayout extends RelativeLayout{
    public interface PhotoViewListener{
        void onTapPhotoView(Bitmap image);
    }

    private String mUrl;
    private Bitmap mImage;
    private boolean mChanged = false;
    private  PhotoViewListener mPhotoViewListener;
    private ImageView mImageView;
    private TextView mTxtError;
    private ProgressBar mProgress;

    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;

    public PhotoViewerLayout(Context context) {
        this(context,null);
    }

    public PhotoViewerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.photo_viewer,this);
        mImageView = (ImageView) findViewById(R.id.image_photo);
        mTxtError = (TextView) findViewById(R.id.text_error);
        mProgress = (ProgressBar) findViewById(R.id.progress_loading);

        mOptions = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.icon)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        mImageLoader = ImageLoader.getInstance();
    }

    public void setUrl(String url,PhotoViewListener listener){
        mUrl = url;
        mPhotoViewListener = listener;
        loadImage();
    }

    private void loadImage(){
        mImageLoader.displayImage(mUrl, mImageView, mOptions,new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                showProgress();
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                showError();
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                hideProgress();
                //mImage = bitmap.c
                //bitmap.copyPixelsToBuffer();
                mImage = bitmap.copy(bitmap.getConfig(), bitmap.isMutable());
                mChanged = true;
                setAttacher();

            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                hideProgress();
            }
        });
    }
    private void showError() {
        hideProgress();
        if (mTxtError != null) {
            mTxtError.setVisibility(View.VISIBLE);
        }
    }

    private void showProgress() {
        if (mProgress != null) {
            mProgress.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgress() {
        if (mProgress != null) {
            mProgress.setVisibility(View.GONE);
        }
    }

    private void setAttacher() {
        PhotoViewAttacher attacher = new PhotoViewAttacher(mImageView);
        attacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float v, float v2) {
                if (mPhotoViewListener != null && mChanged && mImage != null) {
                    mPhotoViewListener.onTapPhotoView(mImage);
                }
            }
        });
        attacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float v, float v2) {
                if (mPhotoViewListener != null && mChanged && mImage != null) {
                    mPhotoViewListener.onTapPhotoView(mImage);
                }
            }
        });
    }
}
