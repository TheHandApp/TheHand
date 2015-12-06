package com.tianyaqu.thehand.app.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

/**
 * Created by Alex on 2015/11/22.
 */

@Table(name = "edition")
public class EditionModel extends Model {
    @Column(name = "issueNum", index = true,unique = true,onUniqueConflict = Column.ConflictAction.REPLACE)
    public long issueNum;

    @Column(name = "coverUrl")
    public String coverUrl;

    @Column(name = "url")
    public String url;

    @Column(name = "isDownloaded")
    public boolean isDownloaded = false;

    public EditionModel() {
        super();
    }

    public EditionModel(long date){
        super();
        issueNum = date;
    }

    public EditionModel(long date,String cover,String link){
        super();
        issueNum = date;
        coverUrl = cover;
        url = link;
    }

    public void setIssueNum(long date){
        issueNum = date;
    }

    public void setCoverUrl(String url){
        coverUrl = url;
    }

    public List<SectionModel> sections(){
        return getMany(SectionModel.class,"EditionModel");
    }

}
