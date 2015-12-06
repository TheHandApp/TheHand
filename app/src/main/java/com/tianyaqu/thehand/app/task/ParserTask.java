package com.tianyaqu.thehand.app.task;

import android.os.AsyncTask;
import com.tianyaqu.thehand.app.Constants;
import com.tianyaqu.thehand.app.database.Article;
import com.tianyaqu.thehand.app.database.Parser;

/**
 * Created by Alex on 2015/12/2.
 */
public class ParserTask extends AsyncTask<Void, Void, Boolean> {
    private boolean isParsing = false;
    private String htmlContent;
    private ParserTaskListener mListener;
    private int mParserType = -1;
    private ExtraPara mParams;
    private Article mArticle;

    public interface ParserTaskListener {
        void setParserTaskListener(ParserTaskListener listener);
        void onComplete(Article article);
    }

    public Boolean hasPara(){
        return mParams != null;
    }
    public void setExtraPara(ExtraPara para){
        mParams = para;
    }

    public ParserTask(int type, String html, ParserTaskListener listener) {
        super();
        mParserType = type;
        htmlContent = html;
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        isParsing = true;
    }

    @Override
    protected void onCancelled() {
        isParsing = false;
    }

    @Override
    protected void onPostExecute(Boolean ret) {
        isParsing = false;
        if(ret == true){
            //NO check
            mListener.onComplete(mArticle);
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Boolean ret = true;
        if(!htmlContent.equals("")) {
            switch (mParserType){
                case Constants.PARSER_ARTICLE:
                    String articlePara = "";
                    if(hasPara()){
                        articlePara = mParams.getParameters().get(0);
                    }
                    mArticle = Parser.parseArticle(htmlContent,articlePara);
                    break;
                case Constants.PARSER_BLOG_ARTICLE:
                    String blog_Para = "";
                    if(hasPara()){
                        blog_Para = mParams.getParameters().get(0);
                    }
                    mArticle = Parser.parseBlogArticle(htmlContent,blog_Para);
                    break;
                case Constants.PARSER_EDITION:
                    Parser.parseEdition(htmlContent);
                    break;
                case Constants.PARSER_BLOG_INDEX:
                    Parser.parseBlogIndex(htmlContent);
                    break;
                case Constants.PARSER_EDITION_ALL:
                    Parser.parseEditionAll(htmlContent);
                    break;
                default:
                    ret = false;
            }
        }else {
            ret = false;
        }
        return ret;
    }

    public boolean isParsing() {
        return isParsing;
    }
}
