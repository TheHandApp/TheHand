package com.tianyaqu.thehand.app.database;

import android.util.Log;
import com.activeandroid.ActiveAndroid;
import com.tianyaqu.thehand.app.AppContext;
import com.tianyaqu.thehand.app.Constants;
import com.tianyaqu.thehand.app.utils.CommonUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 2015/11/19.
 */

public class Parser {
    public final static String BASE_URL = "http://www.economist.com/";
    public final static String AUTHOR = "From print edition";

    public final static String IMG_TAG = "img[src]";
    public final static String ATTR_SRC = "src";
    public final static String ATTR_IMG = "img";
    public final static String H1_TAG = "h1";
    public final static String H2_TAG = "h2";
    public final static String H3_TAG = "h3";
    public final static String H4_TAG = "h4";
    public final static String H5_TAG = "h5";
    public final static String HREF_TAG = "href";
    public final static String A_TAG = "a";

    public final static String COVER_IMG_TAG = "print-cover-thumbnail";
    public final static String ARTICLE_TAG = "article";
    public final static String HGROUP_TAG = "hgroup";
    public final static String DATE_TAG = "date-created";
    public final static String MAIN_TAG = "main-content";

    public final static String BLOG_LOGO_TAG = "section-teaser-logo";
    public final static String BLOG_DESC_CONTENT_TAG ="section-teaser-content";
    public final static String BLOG_HEADLINE_TAG = "headline";
    public final static String BLOG_DESC_TAG = "rubric-teaser";

    public final static String EDITION_TAG = "view-content";
    public final static String PRINT_TAG = "print-cover-image";

    public final static String SECTION_TAG = "div.section";
    public final static String SECTION_ARTICLE_TAG = "div.article";

    private final static String DATE_PATTERN = "/([0-9]{8})_";
    private final static String SUFFIX = "/print";

    public static void parseEditionAll(String html){
        final String regex = "/([0-9]{4}-[0-9]{2}-[0-9]{2})";
        final String pattern = "yyyy-MM-dd";

        List<EditionModel> editionModels = new ArrayList<EditionModel>();
        Document doc = Jsoup.parse(html,BASE_URL);
        Element body = doc.body();
        if(body != null){
            Element container = body.getElementsByClass(EDITION_TAG).first();
            if (container != null){
                Elements prints = container.getElementsByClass(PRINT_TAG);
                for (Element print : prints){
                    Element inner = print.select(A_TAG).first();
                    if(inner != null) {
                        String print_url = inner.absUrl(HREF_TAG);
                        String date = CommonUtils.splitDate(regex, print_url);
                        long issueNum = CommonUtils.dateStrToLong(pattern, date);
                        String full = "";
                        Element img = inner.select(IMG_TAG).first();
                        if (img != null) {
                            String img_url = img.absUrl(ATTR_SRC);
                            full = img_url.replaceFirst("thumbnail", "full");
                        }
                        if(issueNum != 0 ) {
                            //EditionModel e = new EditionModel(issueNum, full, print_url);
                            editionModels.add(new EditionModel(issueNum, full, print_url));
                        }
                    }
                }
            }
        }

        ActiveAndroid.beginTransaction();
        try {
            for(EditionModel e:editionModels){
                if(!Query.isEditionExists(e.issueNum)){
                    e.save();
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        }catch (Exception e) {
            Log.d("Parser","something happend");
        }
            finally
         {
            ActiveAndroid.endTransaction();
        }
    }

    public static Article parseBlogArticle(String html,String url){
        final String BLOG_HEADER_TAG = "main-content-header";
        String topic = "";
        String title = "";
        String sub = "";
        String date = CommonUtils.prettyDateStr(new java.util.Date());
        String image = "";
        String content = "";
        Document doc = Jsoup.parse(html,BASE_URL);
        Element body = doc.body();
        if(body != null) {
            Element header = body.getElementsByClass(BLOG_HEADER_TAG).first();
            if(header != null){
                topic = header.select(H1_TAG).first().text();
                title = header.select(H3_TAG).first().text();
            }
            Element dateElement = body.getElementsByClass(DATE_TAG).first();
            if(dateElement != null){
                date = dateElement.text();
            }

            Element main = body.getElementsByClass(MAIN_TAG).first();
            if(main != null){
                content = main.toString();
            }
        }
        return new Article(topic,title,sub,date,url,image,AUTHOR,content);
    }

    public static Article parseArticle(String html, String url){
        String topic = "";
        String title = "";
        String sub = "";
        String date = CommonUtils.prettyDateStr(new java.util.Date());
        String image = "";
        String content = "";
        Document doc = Jsoup.parse(html,BASE_URL);
        Element body = doc.body();
        if(body != null){
            Element article = body.select(ARTICLE_TAG).first();
            if(article != null){
                Element hgroup = article.select(HGROUP_TAG).first();
                if(hgroup != null){
                    Element h2 = hgroup.select(H2_TAG).first();
                    if(h2 != null){
                        topic = h2.text();
                    }
                    Element h3 = hgroup.select(H3_TAG).first();
                    if(h3 != null){
                        title = h3.text();
                    }
                    Element h1 = hgroup.select(H1_TAG).first();
                    if(h1 != null){
                        sub = h1.text();
                    }
                }
                Element eleDate = article.getElementsByClass(DATE_TAG).first();
                if(eleDate != null){
                    date = eleDate.text();
                }

                Element main = article.getElementsByClass(MAIN_TAG).first();
                if(main != null){
                    Element img = main.select(ATTR_IMG).first();
                    if(img != null){
                        image = img.attr(ATTR_SRC);
                    }
                    content = main.toString();
                }
            }
        }

        return new Article(topic,title,sub,date,url,image,AUTHOR,content);
    }

    public static void parseEdition(String html){
        Document doc = Jsoup.parse(html,BASE_URL);
        Elements images = doc.select(IMG_TAG);

        EditionModel edition = new EditionModel();
        List<SectionModel> sectionModels = new ArrayList<SectionModel>();
        List<ArticleEntryModel> articleEntries = new ArrayList<ArticleEntryModel>();

        long date = 0;
        for(Element img : images){
            String url = img.absUrl(ATTR_SRC);
            if (url.contains(COVER_IMG_TAG)){
                String dateStr = CommonUtils.splitDate(DATE_PATTERN,url);

                date = CommonUtils.dateStrToLong("yyyyMMdd",dateStr);

                if(Query.isEditionExists(date)){
                    Log.d("parser","edition" + dateStr +" " +  date + "exists");
                    EditionModel t = Query.Edition(date);
                    if(t != null){
                        edition = t;
                        Log.d("parser","use the databases");
                    }
                }
                else {
                    edition.setCoverUrl(url.replaceFirst("thumbnail","full"));
                    edition.setIssueNum(date);
                }

                break;
            }
        }

        Elements sections = doc.select(SECTION_TAG);
        for (Element sec : sections) {
            //Section section = new Section(date);
            for (Element h4 : sec.getElementsByTag(H4_TAG)) {
                String sectionTitle = h4.text();
                System.out.println("section ["+ sectionTitle + "]");

                //section.setSectionName(sectionTitle);
                SectionModel section  = new SectionModel(sectionTitle,edition);
                sectionModels.add(section);

                Elements articles = sec.select(SECTION_ARTICLE_TAG);
                for(Element article : articles){

                    String prefix = "";
                    Element sub = article.previousElementSibling();
                    if(sub.tagName().equals(H5_TAG)){
                        prefix = sub.text() + ":";
                    }
                    Element inner = article.select(A_TAG).first();
                    String title = prefix + inner.text();
                    String url = inner.absUrl(HREF_TAG);
                    url += SUFFIX;
                    articleEntries.add(new ArticleEntryModel(date,title,url,section));
                }
            }
        }


        //transaction save
        ActiveAndroid.beginTransaction();
        try {
            edition.isDownloaded = true;
            edition.save();
            if(AppContext.getLatestIssueNum() < edition.issueNum) {
                AppContext.setLatestIssueNum(edition.issueNum);
            }

            for(SectionModel sec:sectionModels){
                sec.save();
            }

            for(ArticleEntryModel entry : articleEntries){
                entry.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }

    }

    public static void parseBlogIndex(String html){
        List<ArticleEntryModel> blogModels = new ArrayList<ArticleEntryModel>();

        EditionModel edition = new EditionModel(Constants.BLOG_SEED);
        SectionModel blog;
        Log.d("parser","parse blog");
        if(Query.isEditionExists(Constants.BLOG_SEED)){
            EditionModel t = Query.Edition(Constants.BLOG_SEED);
            if(t != null){
                edition = t;
            }
        }
        blog = new SectionModel(Constants.SECTION_BLOG,edition);
        if(Query.isBlogSectionExists(Constants.SECTION_BLOG)){
            SectionModel t = Query.BlogSection(Constants.SECTION_BLOG);
            Log.d("parser","blog section exist " + (t!=null));
            if(t != null){
                blog = t;
                Log.d("parser","blog use the databases blog section");
            }
        }
        Log.d("parser","new blog section");
        long d = new java.util.Date().getTime();
        Document doc = Jsoup.parse(html,BASE_URL);
        Element body = doc.body();
        if(body != null) {
            Element container = body.getElementsByClass(MAIN_TAG).first();
            if(container != null){
                for(Element article :container.select(ARTICLE_TAG)){
                    String link = "";
                    String img_url = "";
                    String topic = "";
                    String title = "";
                    String date = "";
                    String desc = "";

                    Element linka = article.select(A_TAG).first();
                    link = linka.absUrl(HREF_TAG);
                    link += SUFFIX;

                    if(linka != null) {
                        Element logo = linka.getElementsByClass(BLOG_LOGO_TAG).first();
                        Element img = logo.select(IMG_TAG).first();
                        img_url = img.absUrl(ATTR_SRC);

                        Element titleEle = linka.getElementsByClass(BLOG_DESC_CONTENT_TAG).first();
                        if (titleEle != null) {
                            topic = titleEle.select(H3_TAG).first().ownText();
                            title = titleEle.getElementsByClass(BLOG_HEADLINE_TAG).first().text();
                            date = titleEle.getElementsByClass(DATE_TAG).first().text();
                            desc = titleEle.getElementsByClass(BLOG_DESC_TAG).first().ownText();
                        }
                    }
                    blogModels.add(new ArticleEntryModel(d,date,title,topic,link,img_url,desc,blog));
                }
            }
        }

        //transaction save
        ActiveAndroid.beginTransaction();
        try {
            edition.save();
            blog.save();
            for(ArticleEntryModel article : blogModels){
                if(!Query.isBlogExists(article.url)){
                    article.save();
                }else {
                    break;
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }
}
