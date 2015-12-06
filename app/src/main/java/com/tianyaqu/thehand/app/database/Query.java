package com.tianyaqu.thehand.app.database;

import android.util.Log;
import com.activeandroid.query.Select;
import com.tianyaqu.thehand.app.utils.CommonUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by Alex on 2015/11/22.
 */
public class Query {

    public static boolean isBlogExists(String url){
        return new Select()
                .from(ArticleEntryModel.class)
                .where("url = ?",url)
                .exists();
    }

    public static boolean isBlogSectionExists(String name){
        return new Select()
                .from(SectionModel.class)
                .where("sectionName = ?",name)
                .exists();
    }

    public static boolean isEditionExists(long issueNum) {
        return new Select()
                .from(EditionModel.class)
                .where("issueNum = ?",issueNum)
                .exists();
    }

    public static List<EditionModel> allEditions(){
        String year = CommonUtils.dateToDateStr("yyyy",new Date());
        long date = (CommonUtils.dateStrToLong("yyyy-MM-dd",year+"-01-01"));
        Log.d("query",year+"-01-01-"+date);
        return new Select()
                .from(EditionModel.class)
                .orderBy("issueNum ASC")
                .where("issueNum >= ?",date)
                .limit(52)
                .execute();
    }

    public static EditionModel Edition(long issueNum){
        return new Select()
                .from(EditionModel.class)
                .where("issueNum = ?",issueNum)
                .executeSingle();
    }

    public static SectionModel BlogSection(String name){
        return new Select()
                .from(SectionModel.class)
                .where("sectionName = ?",name)
                .executeSingle();
    }

    public static List<ArticleEntryModel> XSection(String name){
        final int num = 20;
        return new Select()
                .from(ArticleEntryModel.class)
                .join(SectionModel.class)
                .on("articleEntry.section= section.Id")
                .where("section.sectionName = ?",name)
                .orderBy("date DESC")
                .limit(num)
                .execute();
    }

    public static List<ArticleEntryModel> XSection(String name,long date){
        final int num = 20;
        return new Select()
                .from(ArticleEntryModel.class)
                .join(SectionModel.class)
                .on("articleEntry.section = section.Id")
                .join(EditionModel.class)
                .on("section.edition = edition.Id")
                .where("section.sectionName = ?",name)
                .where("edition.issueNum = ?",date)
                .limit(num)
                .execute();
    }
}
