package com.tianyaqu.thehand.app;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import com.tianyaqu.thehand.app.database.ArticleEntryModel;
import com.tianyaqu.thehand.app.database.Query;
import com.tianyaqu.thehand.app.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 2015/11/16.
 */
public class ReaderViewActivity extends AppCompatActivity implements
        ArticleViewFragment.ProgressbarListener {
    private ProgressBar mProgressbar;
    private ViewPager mArticlePager;
    private ArticlePagerAdapter mAdapter;
    private Toolbar mToolBar;
    private String mCurArticle;
    private String mSectionName;
    private long mIssueNum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reader_view);
        mProgressbar = (ProgressBar)findViewById(R.id.progressBar);
        mArticlePager = (ViewPager)findViewById(R.id.reader_view_pager);
        mArticlePager.setAdapter(getAdapter());

        mArticlePager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                //onShowHideToolbar(true);
            }
        });

        mToolBar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);

        mSectionName = getIntent().getStringExtra(Constants.ARG_SECTION_NAME);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mSectionName);
        }
        mIssueNum = getIntent().getLongExtra(Constants.ARG_ISSUE_NUM,0);
        if(savedInstanceState != null){
            mCurArticle = savedInstanceState.getString(Constants.ARG_ARTICLE_URL);
        }else {
            mCurArticle = getIntent().getStringExtra(Constants.ARG_ARTICLE_URL);
        }
        Log.d(this.getClass().getName(),"----section name-----" + mSectionName);
        updateArticles();
    }

    private void updateArticles(){
        ArrayList<String> articles = new ArrayList<String>();
        /*
        Section section = Fetcher.fetchSection(mSectionName,mIssueNum);
        ArrayList<String> articles = new ArrayList<String>();
        for(ArticleEntry entry :section.getEntries()){
            articles.add(entry.getUrl());
        }
        */
        List<ArticleEntryModel> tt = Query.XSection(mSectionName,mIssueNum);
        Log.d(this.getClass().getName(),"size:" + tt.size());
        for(ArticleEntryModel one : tt){
            articles.add(one.url);
        }
        getAdapter().setUrls(articles);
    }
    private ArticlePagerAdapter getAdapter(){
        if(mAdapter == null){
            mAdapter = new ArticlePagerAdapter(getSupportFragmentManager());
        }
        return mAdapter;
    }
    private boolean hasAdapter()
    {
        return (mAdapter!=null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        if(hasAdapter()){
            outState.putString(Constants.ARG_ARTICLE_URL,mCurArticle);
        }
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private class ArticlePagerAdapter extends FragmentStatePagerAdapter {
        private ArrayList<String> mUrls;
        public ArticlePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setUrls(ArrayList<String>list){
            mUrls = (ArrayList<String>)list.clone();
            notifyDataSetChanged();
            int position = CommonUtils.indexOfItem(mUrls,mCurArticle);
            if(isValidPosition(position)){
                mArticlePager.setCurrentItem(position);
            }
        }
        private boolean isValidPosition(int position) {
            return (mUrls != null
                    && position >= 0
                    && position < getCount());
        }

        @Override
        public Fragment getItem(int i) {
            return ArticleViewFragment.newInstance(mUrls.get(i));
        }

        @Override
        public int getCount() {
            return (mUrls==null) ? 0 : mUrls.size();
        }
    }

    @Override
    public void onShowHideProgressbar(boolean show) {
        if(show){
            showProgress();
        }else{
            hideProgress();
        }

    }

    private void showProgress() {
        if (mProgressbar != null) {
            mProgressbar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgress() {
        if (mProgressbar != null) {
            mProgressbar.setVisibility(View.GONE);
        }
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
}
