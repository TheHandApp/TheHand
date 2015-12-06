package com.tianyaqu.thehand.app.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

/**
 * Created by Alex on 2015/11/22.
 */
@Table(name = "section")
public class SectionModel extends Model {
    @Column(name = "sectionName",index = true,uniqueGroups = {"uniqSection"},onUniqueConflict = Column.ConflictAction.REPLACE)
    public String name;

    @Column(name = "edition",uniqueGroups = {"uniqSection"})
    public EditionModel edition;

    public SectionModel() {
        super();
    }

    public SectionModel(String name,EditionModel edition){
        super();
        this.name = name;
        this.edition = edition;
    }

    public List<ArticleEntryModel> articleEntries(){
        return getMany(ArticleEntryModel.class,"SectionModel");
    }
}
