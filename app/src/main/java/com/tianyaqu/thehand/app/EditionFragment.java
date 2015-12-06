package com.tianyaqu.thehand.app;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.tianyaqu.thehand.app.database.Article;
import com.tianyaqu.thehand.app.database.EditionModel;
import com.tianyaqu.thehand.app.database.Query;
import com.tianyaqu.thehand.app.network.Fetcher;
import com.tianyaqu.thehand.app.task.ParserTask;
import com.tianyaqu.thehand.app.utils.CommonUtils;
import com.tianyaqu.thehand.app.widget.CheckableImageView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import cz.msebera.android.httpclient.Header;

import java.util.Date;

/**
 * Created by Alex on 2015/11/28.
 */
public class EditionFragment extends Fragment implements ParserTask.ParserTaskListener{
    private String mCoverUrl;
    private long mIssueNum;
    private String mLink;
    private boolean mDownloaded;
    private String mDate;

    private boolean mChanged = false;

    private TextView mTextView;
    private ImageView mImageView;

    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;

    public static EditionFragment newInstance(long date,String url,String link,boolean download){
        EditionFragment fragment = new EditionFragment();
        Bundle args = new Bundle();
        args.putString(Constants.ARG_COVER_IMG_URL,url);
        args.putString(Constants.ARG_EDITION_URL,link);
        args.putLong(Constants.ARG_ISSUE_NUM,date);
        args.putBoolean(Constants.ARG_ISSUE_DOWNLOAD_FLAG,download);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        if(args != null){
            mCoverUrl = args.getString(Constants.ARG_COVER_IMG_URL);
            mLink = args.getString(Constants.ARG_EDITION_URL);
            mIssueNum = args.getLong(Constants.ARG_ISSUE_NUM);
            mDate = CommonUtils.prettyDateDetailStr(new Date(mIssueNum));
            mDownloaded = args.getBoolean(Constants.ARG_ISSUE_DOWNLOAD_FLAG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setParserTaskListener(this);
        final View view =  inflater.inflate(R.layout.fragment_edition, container, false);
        mImageView = (ImageView)view.findViewById(R.id.editionCover);
        mTextView = (TextView)view.findViewById(R.id.editionDate);

        mOptions = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.icon)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        mImageLoader = ImageLoader.getInstance();

        if(savedInstanceState != null){
            mCoverUrl = savedInstanceState.getString(Constants.ARG_COVER_IMG_URL);
            mLink = savedInstanceState.getString(Constants.ARG_EDITION_URL);
            mIssueNum = savedInstanceState.getLong(Constants.ARG_ISSUE_NUM);
            mDate = CommonUtils.prettyDateDetailStr(new Date(mIssueNum));
            mDownloaded = savedInstanceState.getBoolean(Constants.ARG_ISSUE_DOWNLOAD_FLAG);
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(Constants.ARG_COVER_IMG_URL,mCoverUrl);
        outState.putString(Constants.ARG_EDITION_URL,mLink);
        outState.putLong(Constants.ARG_ISSUE_NUM,mIssueNum);
        outState.putBoolean(Constants.ARG_ISSUE_DOWNLOAD_FLAG,mDownloaded);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("edition fragment","on edition resume " + mDate);
        if(mTextView != null) {
            mTextView.setText(mDate);
        }

        CheckableImageView v = (CheckableImageView)mImageView;
        if(mDownloaded && !v.isChecked()){
            v.setChecked(true);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        loadImage();
    }

    private void loadImage(){
        mImageLoader.displayImage(mCoverUrl,mImageView,mOptions,new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                Log.d("----failed","fail");
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                mChanged = true;
                setAttacher();
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
            }
        });
    }

    private void setAttacher() {
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mChanged){
                    CheckableImageView checkView = (CheckableImageView)v;
                    if(!checkView.isChecked()) {
                        checkView.toggle();
                    }

                    EditionModel model = Query.Edition(mIssueNum);
                    if(model != null && !model.isDownloaded) {
                        //model.isDownloaded = true;
                        model.save();
                        mDownloaded = true;
                        Fetcher.fetchHtml(mLink, mHandler);
                    }
                }
            }
        });
    }

    private AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            new ParserTask(Constants.PARSER_EDITION,new String(bytes), mParserTaskListener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
        }
    };

    private ParserTask.ParserTaskListener mParserTaskListener;

    @Override
    public void setParserTaskListener(ParserTask.ParserTaskListener listener){
        mParserTaskListener = listener;
    }

    @Override
    public void onComplete(Article article) {
        Log.d("editon fragment","on complete interface");
    }
}