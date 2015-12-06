package com.tianyaqu.thehand.app.model;

import java.util.ArrayList;

/**
 * Created by Alex on 2015/11/16.
 */
public class Section {
    private String mSectionName;
    private long mDate;
    private ArrayList<ArticleEntry> articleEntries;

    public Section(long date){
        mDate = date;
    };
    public Section(long date,String name){
        mSectionName = name;
        mDate = date;
    }
    public Section(String name){
        mSectionName = name;
    }
    public void addEntry(long date,String articleName,String url){
        if(articleEntries == null){
            articleEntries = new ArrayList<ArticleEntry>();
        }
        articleEntries.add(new ArticleEntry(date,articleName,url));
    }

    public String getName() {
        return mSectionName;
    }
    public long getDate() {
        return mDate;
    }
    public ArrayList<ArticleEntry> getEntries() {
        return articleEntries;
    }

    public void setSectionName(String mSectionName) {
        this.mSectionName = mSectionName;
    }
}
