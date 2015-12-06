package com.tianyaqu.thehand.app.model;

import java.util.ArrayList;

/**
 * Created by Alex on 2015/11/21.
 */
public class Edition {
    private long mIssumeNum = 0;
    private String mCoverImage = "";
    private ArrayList<Section> mSections;

    public Edition(){

    }

    public Edition(long num){
        mIssumeNum = num;
    }

    public void addSection(Section section){
        if(mSections == null){
            mSections = new ArrayList<Section>();
        }
        mSections.add(section);
    }

    public ArrayList<Section> getSections(){
        return mSections;
    }

    public String getCoverImage() {
        return mCoverImage;
    }

    public void setCoverImage(String mCoverImage) {
        this.mCoverImage = mCoverImage;
    }

    public void setIssumeNum(long mIssumeNum) {
        this.mIssumeNum = mIssumeNum;
    }
}
