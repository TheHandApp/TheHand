package com.tianyaqu.thehand.app.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Alex on 2015/11/22.
 */

@Table(name = "articleEntry")
public class ArticleEntryModel extends Model {
    @Column(name = "url",unique = true,onUniqueConflict = Column.ConflictAction.REPLACE)
    public String url;

    @Column(name = "title")
    public String title;

    @Column(name = "date")
    public long date;

    @Column(name = "readed")
    public boolean readed;

    @Column(name = "section", index = true)
    public SectionModel section;

    //below used for blog article
    @Column(name = "topic")
    public String topic;

    @Column(name = "strDate")
    public String strDate;

    @Column(name = "desc")
    public String desc;

    @Column(name = "imgUrl")
    public String imgUrl;

    public ArticleEntryModel() {
        super();
    }

    public ArticleEntryModel(long date,String title,String url,SectionModel section){
        super();
        this.date = date;
        this.title = title;
        this.url = url;
        this.section = section;
        this.readed = false;
    }

    public ArticleEntryModel(long d,String date, String title, String topic,String url,String img,String desc,SectionModel section){
        super();
        this.date = d;
        this.strDate = date;
        this.title = title;
        this.topic = topic;
        this.url = url;
        this.imgUrl = img;
        this.desc = desc;
        this.section = section;
        this.readed = false;
    }
}
