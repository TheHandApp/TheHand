package com.tianyaqu.thehand.app.network;

import android.content.Context;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tianyaqu.thehand.app.database.Article;
import com.tianyaqu.thehand.app.database.Parser;
import com.tianyaqu.thehand.app.model.Section;
import cz.msebera.android.httpclient.HttpResponseInterceptor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Alex on 2015/11/16.
 */
public class Fetcher {
    public final static String ROOT = "http://www.economist.com/printedition/";
    public final static String BLOGS = "http://www.economist.com/latest-updates";
    public final static String EDITION_INDEX = "http://www.economist.com/printedition/covers?print_region=76978";
    public final static String UPDATE_URL = "http://7xow78.com1.z0.glb.clouddn.com/";

    private static boolean isSetClientCircularRedirect = false;

    public static Article fetch_article(Context ctx, String url){
        //locally debug
        String content = "";
        content = getRes(ctx);
        return Parser.parseArticle(content,url);
    }

    public static void checkApkUpdate(AsyncHttpResponseHandler handler){
        fetchHtml(UPDATE_URL,handler);
    }

    public static void fetchArticle(String url,AsyncHttpResponseHandler handler){
        EcoHttpClient.get(url,null,handler);
    }

    public static void fetchHtml(String url,AsyncHttpResponseHandler handler){
        EcoHttpClient.get(url,null,handler);
    }

    public static void fetchCurrentEdition(AsyncHttpResponseHandler handler){
        fetchHtml(ROOT,handler);
    }

    public static void fetchLatestNews(AsyncHttpResponseHandler handler){
        fetchHtml(BLOGS,handler);
    }

    public static void fetchEditionIndex(AsyncHttpResponseHandler handler){
        fetchHtml(EDITION_INDEX,handler);
    }

    public static void addResponseInterceptor(HttpResponseInterceptor interceptor){
        EcoHttpClient.addResponseInterceptor(interceptor);
    }
    public static void setClientCircularRedirect(){
        if(!isSetClientCircularRedirect){
            EcoHttpClient.setCircularRedirect(true);
            isSetClientCircularRedirect = true;
        }
    }

    private static String getRes(Context ctx){
        StringBuffer sb = new StringBuffer();
        try{
            InputStreamReader input = new InputStreamReader(ctx.getResources().getAssets().open("java_lead2.html"));
            BufferedReader br = new BufferedReader(input);
            String line;

            while((line = br.readLine()) != null)
                sb.append(line);
        }catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static Section fetchSection(String name){
        Section sec = LoadASection(name);
        return sec;
    }

    public static Section fetchSection(String name,long date){
        Section sec = new Section(name);
        if(name.equals("Britain")) {
            sec.addEntry(20151119,"Welfare:Credit crunch", "http://www.economist.com/news/britain/21677233-plan-cut-tax-credits-defeated-now-coming-up-face-saving-alternative-will-be/print");
            sec.addEntry(20151119,"The House of Lords:Crisis? What crisis?", "http://www.economist.com/news/britain/21677239-tax-credit-row-has-put-reform-upper-house-back-agenda-crisis-what-crisis/print");
        }else if(name.equals("Politics")){
            sec.addEntry(20151119,"Reinventing the company","http://www.economist.com/news/leaders/21676767-entrepreneurs-are-redesigning-basic-building-block-capitalism-reinventing-company/print");
            sec.addEntry(20151119,"Frinds in need","http://www.economist.com/news/leaders/21676768-britain-has-rolled-out-red-carpet-xi-jinping-it-must-not-forget-its-better/print");
        }else if(name.equals("Covery Story")){
            sec.addEntry(20151119,"Cracking the vault","http://www.economist.com/news/finance-and-economics/21676826-grip-banks-have-over-their-customers-weakening-cracking-vault/print");
        }else{
            sec.addEntry(20151119,"Funny numbers","http://www.economist.com/news/finance-and-economics/21676830-americas-gdp-statistics-are-becoming-less-reliable-funny-numbers/print");
        }
        return sec;
    }

    static ArrayList<Section> fetch_sections(Context ctx,long num){
        ArrayList<Section> sections = new ArrayList<Section>();
        return sections;
    }

    private static Section LoadASection(String name){
        Section sec = new Section(name);
        if(name.equals("Britain")) {
            sec.addEntry(20151119,"Welfare:Credit crunch", "http://www.economist.com/news/britain/21677233-plan-cut-tax-credits-defeated-now-coming-up-face-saving-alternative-will-be/print");
            sec.addEntry(20151119,"The House of Lords:Crisis? What crisis?", "http://www.economist.com/news/britain/21677239-tax-credit-row-has-put-reform-upper-house-back-agenda-crisis-what-crisis/print");
            sec.addEntry(20151123,"Electoral reform:Cast out", "http://www.economist.com/news/britain/21677258-update-electoral-register-could-miss-2m-voters-who-benefits-cast-out/print");
        }else if(name.equals("Politics")){
            sec.addEntry(20151119,"Reinventing the company","http://www.economist.com/news/leaders/21676767-entrepreneurs-are-redesigning-basic-building-block-capitalism-reinventing-company/print");
            sec.addEntry(20151123,"Frinds in need","http://www.economist.com/news/leaders/21676768-britain-has-rolled-out-red-carpet-xi-jinping-it-must-not-forget-its-better/print");
        }else if(name.equals("Covery Story")){
            sec.addEntry(20151119,"Cracking the vault","http://www.economist.com/news/finance-and-economics/21676826-grip-banks-have-over-their-customers-weakening-cracking-vault/print");
        }else{
            sec.addEntry(20151119,"Funny numbers","http://www.economist.com/news/finance-and-economics/21676830-americas-gdp-statistics-are-becoming-less-reliable-funny-numbers/print");
        }
        return sec;
    }
}
