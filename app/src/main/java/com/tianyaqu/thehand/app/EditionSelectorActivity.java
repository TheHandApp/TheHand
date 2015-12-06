package com.tianyaqu.thehand.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import com.tianyaqu.thehand.app.database.Article;
import com.tianyaqu.thehand.app.database.EditionModel;
import com.tianyaqu.thehand.app.database.Query;
import com.tianyaqu.thehand.app.network.Fetcher;
import com.tianyaqu.thehand.app.task.ParserTask;
import com.loopj.android.http.AsyncHttpResponseHandler;
import cz.msebera.android.httpclient.Header;

import java.util.List;

/**
 * Created by Alex on 2015/11/28.
 */
public class EditionSelectorActivity extends AppCompatActivity implements ParserTask.ParserTaskListener{
    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private EditionPagerAdapter mAdapter;
    private int mIndex = -1;
    private boolean newDownload = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edition_selector);
        setParserTaskListener(this);

        mViewPager = (ViewPager)findViewById(R.id.edition_view_pager);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if(mIndex != -1) {
                    mIndex = position;
                }
            }
        });
        mViewPager.setAdapter(getAdapter());

        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.edition_title);
        }
        refresh();
        if(savedInstanceState != null){
            mIndex = savedInstanceState.getInt(Constants.ARG_VP_INDEX);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEditions();
    }

    private void refresh(){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Fetcher.fetchEditionIndex(mHandler);
            }
        });

    }
    private void loadEditions(){
        List<EditionModel> editions = Query.allEditions();

        if(mIndex == -1 && editions != null && editions.size() > 0) {
            mIndex = editions.size();
        }

        if(newDownload && editions != null && mIndex < editions.size()){
            mIndex = editions.size();
            newDownload = false;
        }

        getAdapter().setEditions(editions);
        Log.d(this.getClass().getName(),""+ mIndex + " "+ editions.size());
    }

    private boolean hasAdapter()
    {
        return (mAdapter!=null);
    }

    private EditionPagerAdapter getAdapter(){
        if(mAdapter == null){
            mAdapter = new EditionPagerAdapter(getSupportFragmentManager());
        }
        return mAdapter;
    }

    private class EditionPagerAdapter extends FragmentStatePagerAdapter {
        private List<EditionModel> editions;

        public EditionPagerAdapter(FragmentManager fm){
            super(fm);
        }

        public void setEditions(List<EditionModel> editions){
            this.editions = editions;
            notifyDataSetChanged();
            mViewPager.setCurrentItem(mIndex);
        }

        @Override
        public Fragment getItem(int i) {
            EditionModel edition = editions.get(i);
            return EditionFragment.newInstance(edition.issueNum,edition.coverUrl,edition.url,edition.isDownloaded);
        }

        @Override
        public int getCount() {
            return (editions == null) ? 0 : editions.size();
        }
    }

    private AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            Log.d(EditionSelectorActivity.class.getName(),"fetch success ");
            new ParserTask(Constants.PARSER_EDITION_ALL,new String(bytes), mParserTaskListener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Log.d(EditionSelectorActivity.class.getName(),"main fetch failed ");
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(Constants.ARG_VP_INDEX,mIndex);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private ParserTask.ParserTaskListener mParserTaskListener;

    @Override
    public void setParserTaskListener(ParserTask.ParserTaskListener listener){
        mParserTaskListener = listener;
    }

    @Override
    public void onComplete(Article article) {
        Log.d(this.getClass().getName(),"on complete interface");
        newDownload = true;
        loadEditions();
    }
}