package com.tianyaqu.thehand.app.database;

import com.tianyaqu.thehand.app.utils.ImageUtil;

/**
 * Created by Alex on 2015/11/17.
 */

public class Article {
    private String topic;
    private String title;
    private String sub;
    private String date;
    private String url;
    private String imageUrl;
    private String author;
    private String content;

    public Article(String topic, String title, String sub, String date, String url, String imageUrl,String author, String content) {
        this.topic = topic;
        this.title = title;
        this.sub = sub;
        this.date = date;
        this.url = url;
        this.imageUrl = imageUrl;
        this.author = author;
        this.content = content;
    }

    public String toString(){
        return build_html();
    }
    public String getContent() {
        return content;
    }

    public String getTopic() {
        return topic;
    }

    public String getTitle() {
        return title;
    }

    public String getSub() {
        return sub;
    }

    public String getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAuthor() {
        return author;
    }

    private String build_html(){

        String HEAD = "<!DOCTYPE html><html><head><meta charset='UTF-8' />";
        String TAIL = "</div></body></html>";

        String linkCss = "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/merriweather.css\">"
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/thehand.css\">";


        StringBuffer body = new StringBuffer();
        body.append(HEAD);
        body.append(linkCss);

        body.append("</head> <body><div class='article'>");

        //patch for 'world this week' section
        if(topic.equals("") && title.equals("")){
            body.append(String.format("<h2 class='topic'>%s</h2>", sub));
        }
        else{
            body.append(String.format("<h3 class='topic'>%s</h3>", topic));
            body.append(String.format("<h1 class='title'>%s</h1>", title));
            body.append(String.format("<h2 class='sub'>%s</h2>", sub));
        }


        String authorTag = String.format("<a class='author' href='%s'>%s</a>",url, author);
        body.append(String.format("<div class='authortime'>%s&nbsp;|&nbsp;%s</div>",date,authorTag));

        body.append(ImageUtil.stripImageAttrs(content,true));

        body.append("<br/>");

        body.append(TAIL);

        return body.toString();
    }
}
