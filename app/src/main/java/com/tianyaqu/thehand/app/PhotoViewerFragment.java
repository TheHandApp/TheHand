package com.tianyaqu.thehand.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Alex on 2015/11/13.
 */
public class PhotoViewerFragment extends Fragment {
    private String mUrl;
    private PhotoViewerLayout mPhotoViewerLayout;
    private PhotoViewerLayout.PhotoViewListener mListener;

    static PhotoViewerFragment newInstance(String url){
        Bundle arg = new Bundle();
        arg.putString(Constants.ARG_IMAGE_URL,url);
        PhotoViewerFragment fragment = new PhotoViewerFragment();
        fragment.setArguments(arg);
        return  fragment;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        if (args != null) {
            mUrl = args.getString(Constants.ARG_IMAGE_URL);
            Log.d("set url:",mUrl);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_viewer, container, false);
        mPhotoViewerLayout = (PhotoViewerLayout)view.findViewById(R.id.photo_view);
        if(savedInstanceState != null){
            mUrl = savedInstanceState.getString(Constants.ARG_IMAGE_URL);
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(Constants.ARG_IMAGE_URL,mUrl);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof PhotoViewerLayout.PhotoViewListener){
            mListener = (PhotoViewerLayout.PhotoViewListener)context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        showImage();
    }

    private void showImage(){
        if(isAdded() && !TextUtils.isEmpty(mUrl)){
            mPhotoViewerLayout.setUrl(mUrl,mListener);
        }
    }
}