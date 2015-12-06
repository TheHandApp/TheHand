package com.tianyaqu.thehand.app;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.tianyaqu.thehand.app.utils.CommonUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import android.app.Fragment;
//import android.app.FragmentManager;
//import android.support.v13.app.FragmentStatePagerAdapter;

/**
 * Created by Alex on 2015/11/13.
 */
public class PhotoViewerActivity extends AppCompatActivity implements PhotoViewerLayout.PhotoViewListener {
    private String mImageUrl;
    private String mContent;
    private ViewPager mViewPager;
    private PhotoPagerAdapter mAdapter;
    private TextView mTxtTitle;
    private boolean mIsTitleVisible;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);
        mViewPager = (ViewPager)findViewById(R.id.viewpager);
        mTxtTitle = (TextView)findViewById(R.id.text_title);
        mTxtTitle.setVisibility(View.GONE);

        if(savedInstanceState != null){
            mImageUrl = savedInstanceState.getString(Constants.ARG_IMAGE_URL);
            mContent = savedInstanceState.getString(Constants.ARG_CONTENT);
        }else{
            mImageUrl = getIntent().getStringExtra(Constants.ARG_IMAGE_URL);
            mContent = getIntent().getStringExtra(Constants.ARG_CONTENT);
        }

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                updateTitle(position);
            }
        });

        mViewPager.setAdapter(getAdapter());
        loadImageList();
    }

    private void updateTitle(int position){
        if(getImageCount() > 1){
            String titlePhotoViewer = getString(R.string.title_photo_viewer);
            String title = String.format(titlePhotoViewer, position + 1, getImageCount());
            if (title.equals(mTxtTitle.getText())) {
                return;
            }
            mTxtTitle.setText(title);
        }
    }

    private PhotoPagerAdapter getAdapter(){
        if(mAdapter == null){
            mAdapter = new PhotoPagerAdapter(getSupportFragmentManager());
        }
        return mAdapter;
    }

    private void loadImageList(){
        ArrayList<String> urlList = getImageList();

        if (!TextUtils.isEmpty(mImageUrl) && !hasUrl(urlList,mImageUrl)) {
            urlList.add(0, mImageUrl);
        }
        getAdapter().setUrlList(urlList,mImageUrl);
    }

    private boolean hasUrl(ArrayList<String>list,String url){
        return (CommonUtils.indexOfItem(list,url) > -1);
    }

    private ArrayList<String> getImageList() {
        final Pattern IMG_TAG_PATTERN = Pattern.compile(
                "<img(\\s+.*?)(?:src\\s*=\\s*(?:'|\")(.*?)(?:'|\"))(.*?)>",
                Pattern.DOTALL| Pattern.CASE_INSENSITIVE);

        ArrayList<String> imageList = new ArrayList<String>();
        Log.d("--------get imag list","in line 108");
        Matcher imgMatcher = IMG_TAG_PATTERN.matcher(mContent);
        while (imgMatcher.find()) {
            String imgTag = mContent.substring(imgMatcher.start(), imgMatcher.end());
            String srcUrl = getSrcAttrValue(imgTag);
            if (srcUrl != null && srcUrl.startsWith("http")) {
                String uri = Uri.parse(srcUrl).buildUpon().clearQuery().toString();
                String url;
                if (uri.endsWith("/")) {
                    url = uri.substring(0, uri.length() - 1);
                }
                else{
                    url =  uri;
                }
                Log.d("add image",url);
                imageList.add(url);
            }
        }
        return imageList;
    }

    private String getSrcAttrValue(final String tag) {
        final Pattern SRC_ATTR_PATTERN = Pattern.compile(
                "src\\s*=\\s*(?:'|\")(.*?)(?:'|\")",
                Pattern.DOTALL|Pattern.CASE_INSENSITIVE);

        if (tag == null) {
            return null;
        }

        Matcher matcher = SRC_ATTR_PATTERN.matcher(tag);
        if (matcher.find()) {
            return tag.substring(matcher.start() + 5, matcher.end() - 1);
        } else {
            return null;
        }
    }

    private boolean hasAdapter()
    {
        return (mAdapter!=null);
    }

    private int getImageCount() {
        if (hasAdapter()) {
            return getAdapter().getCount();
        } else {
            return 0;
        }
    }

    @Override
    public void onTapPhotoView(Bitmap image) {

        /*
        // not implementated yet for the dependency of gradle
        final int CODE_SD = 0;
        Intent i = new Intent(PhotoViewerActivity.this,
                FilePickerActivity.class);
        i.setAction(Intent.ACTION_GET_CONTENT);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR,
                true);

        int mode = AbstractFilePickerFragment.MODE_DIR;
        i.putExtra(FilePickerActivity.EXTRA_MODE, mode);

        startActivityForResult(i, CODE_SD);
        */
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(hasAdapter()){
            //mImageUrl = XX
            outState.putString(Constants.ARG_IMAGE_URL,mImageUrl);
        }
        outState.putString(Constants.ARG_CONTENT,mContent);
        super.onSaveInstanceState(outState);
    }

    private class PhotoPagerAdapter extends FragmentStatePagerAdapter {
        private ArrayList<String> mUrlList;

        public PhotoPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setUrlList(ArrayList<String>urlList,String url){
            mUrlList = (ArrayList<String>)urlList.clone();
            notifyDataSetChanged();

            int position = CommonUtils.indexOfItem(mUrlList,url);
            if (isValidPosition(position)) {
                mViewPager.setCurrentItem(position);
                if ( getImageCount() > 1) {
                    mTxtTitle.setVisibility(View.VISIBLE);
                    mIsTitleVisible = true;
                    updateTitle(position);
                } else {
                    mIsTitleVisible = false;
                }
            }
        }

        private boolean isValidPosition(int position) {
            return (mUrlList != null
                    && position >= 0
                    && position < getCount());
        }

        @Override
        public int getCount() {
            return (mUrlList != null ? mUrlList.size(): 0);
        }

        @Override
        public Fragment getItem(int i) {
            return (Fragment) PhotoViewerFragment.newInstance(mUrlList.get(i));
        }
    }
}
