package com.tianyaqu.thehand.app;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

/**
 * Created by Alex on 2015/11/12.
 */
public class ArticleWebView extends WebView {

    public interface ArticleClickListener{
        boolean onUrlClick(String url);
        boolean onImageUrlClick(String imageUrl, View view, int x, int y);
    }
    public ArticleWebView(Context context) {
        super(context);
    }

    public ArticleWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ArticleWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private ArticleClickListener mArticleClickListener;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP && mArticleClickListener != null) {
            HitTestResult hr = getHitTestResult();
            if (hr != null && isValidClickedUrl(hr.getExtra())) {
                if (isImageUrl(hr.getExtra())) {
                    return mArticleClickListener.onImageUrlClick(
                            hr.getExtra(),
                            this,
                            (int) event.getX(),
                            (int) event.getY());
                } else {
                    return mArticleClickListener.onUrlClick(hr.getExtra());
                }
            }
        }
        return super.onTouchEvent(event);
    }

    public void setArticleClickListener(ArticleClickListener listener) {
        mArticleClickListener = listener;
    }

    private static boolean isValidClickedUrl(String url) {
        return (url != null && url.startsWith("http"));
    }

    private static boolean isImageUrl(String url){
        String url_clean = Uri.parse(url).buildUpon().clearQuery().toString();
        return url_clean.endsWith("jpg") || url_clean.endsWith("jpeg") ||
                url_clean.endsWith("gif") || url_clean.endsWith("png");
    }
}
