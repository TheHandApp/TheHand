package com.tianyaqu.thehand.app.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Alex on 2015/12/1.
 */
@Table(name = "blogArticle")
public class BlogArticleModel extends Model {
    @Column(name = "url",unique = true,onUniqueConflict = Column.ConflictAction.REPLACE)
    public String url;

    @Column(name = "title")
    public String title;

    @Column(name = "topic")
    public String topic;

    @Column(name = "date")
    public String date;

    @Column(name = "desc")
    public String desc;

    @Column(name = "readed")
    public boolean readed;

    public BlogArticleModel() {
        super();
    }

    public BlogArticleModel(String date, String title, String topic,String url, String desc){
        super();
        this.date = date;
        this.title = title;
        this.topic = topic;
        this.url = url;
        this.desc = desc;
        this.readed = false;
    }
}
