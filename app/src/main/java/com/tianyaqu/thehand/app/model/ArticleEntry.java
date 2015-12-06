package com.tianyaqu.thehand.app.model;

/**
 * Created by Alex on 2015/11/17.
 */
public class ArticleEntry{
    private long mDate;
    private String mName;
    private String mUrl;

    public ArticleEntry(long date,String name,String url){
        mDate = date;
        mName = name;
        mUrl = url;
    }

    public long getDate() {
        return mDate;
    }

    public void setDate(long mDate) {
        this.mDate = mDate;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }
}
