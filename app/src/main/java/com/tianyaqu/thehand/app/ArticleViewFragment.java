package com.tianyaqu.thehand.app;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import com.tianyaqu.thehand.app.database.Article;
import com.tianyaqu.thehand.app.network.Fetcher;
import com.tianyaqu.thehand.app.task.ExtraPara;
import com.tianyaqu.thehand.app.task.ParserTask;
import com.loopj.android.http.AsyncHttpResponseHandler;
import cz.msebera.android.httpclient.Header;


/**
 * Created by Alex on 2015/11/16.
 */
public class ArticleViewFragment extends Fragment implements ArticleScrollView.ScrollDirectionListener,ArticleWebView.ArticleClickListener,ParserTask.ParserTaskListener {
    public interface ProgressbarListener{
        public void onShowHideProgressbar(boolean show);
    }

    private String mArticleUrl;
    private Article mArticle;
    private ArticleWebView mArticleView;
    private ArticleScrollView mScrollView;
    //private TextView mNotifier;
    private ProgressbarListener mProgressbarListener;

    private AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            Log.d(ArticleViewFragment.class.getName(),mArticleUrl);
            ParserTask task;
            if(!isBlog(mArticleUrl)){
                task = new ParserTask(Constants.PARSER_ARTICLE,new String(bytes), mParserTaskListener);
            }
            else {
                task = new ParserTask(Constants.PARSER_BLOG_ARTICLE,new String(bytes), mParserTaskListener);
            }
            task.setExtraPara(new ExtraPara().add(mArticleUrl));
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Log.d(ArticleViewFragment.class.getName(),mArticleUrl);
            failPostActions();
        }
    };

    private Boolean isBlog(String url){
        return url.contains("/blogs/");
    }

    static ArticleViewFragment newInstance(String url){
        ArticleViewFragment fragment = new ArticleViewFragment();
        Bundle args = new Bundle();
        args.putString(Constants.ARG_ARTICLE_URL,url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        if(args != null){
            mArticleUrl = args.getString(Constants.ARG_ARTICLE_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        setParserTaskListener(this);
        final View view =  inflater.inflate(R.layout.fragment_article_view, container, false);
        //mNotifier = (TextView)view.findViewById(R.id.notifier);
        mScrollView = (ArticleScrollView) view.findViewById(R.id.scroll_view_reader);
        mScrollView.setScrollDirectionListener(this);
        mArticleView = (ArticleWebView)view.findViewById(R.id.article_webview);
        mArticleView.setArticleClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if(mArticle != null){
            showArticle();
        }else {
            fetchArticle();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof ProgressbarListener){
            mProgressbarListener = (ProgressbarListener) context;
        }
    }

    private void fetchArticle(){
        mProgressbarListener.onShowHideProgressbar(true);
        Fetcher.fetchArticle(mArticleUrl,mHandler);
    }

    private void showArticle(){
        mScrollView.setVisibility(View.VISIBLE);
        mArticleView.loadDataWithBaseURL("", mArticle.toString(), "text/html", "UTF-8", "");
    }

    private void successPostActions(){
        mProgressbarListener.onShowHideProgressbar(false);
        if(mArticle != null) {
            showArticle();
        }
    }

    private void failPostActions(){
        mProgressbarListener.onShowHideProgressbar(false);
        //textNotify(R.string.internet_error);
    }

    @Override
    public boolean onUrlClick(String url) {
        Log.d(this.getClass().getName(),"in urlclick:" + url);

        Launcher.launchUrl(getActivity(),Intent.ACTION_VIEW,url);
        /*
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        String title = getResources().getString(R.string.openInBrowser_action);
        Intent chooser = Intent.createChooser(intent, title);
        startActivity(chooser);
        */
        return true;
    }

    @Override
    public boolean onImageUrlClick(String imageUrl, View view, int x, int y) {
        Log.d(this.getClass().getName(),"touch image:" + imageUrl);

        Intent intent = new Intent(getActivity(), PhotoViewerActivity.class);
        intent.putExtra(Constants.ARG_IMAGE_URL, imageUrl);
        if (!TextUtils.isEmpty(mArticle.getContent())) {
            intent.putExtra(Constants.ARG_CONTENT, mArticle.getContent());
        }
        startActivity(intent);
        return true;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.article_menu, menu);
        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.menu_share) {
            String title = (!mArticle.getTitle().equals(""))?mArticle.getTitle():mArticle.getSub();
            String txt = "The Economist | " + title + " " + mArticleUrl;
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, txt);
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.article_share_subject, getString(R.string.app_name)));
            startActivity(Intent.createChooser(intent, getString(R.string.article_share_link)));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
    }

    @Override
    public void onScrollUp() {

    }

    @Override
    public void onScrollDown() {

    }

    @Override
    public void onScrollCompleted() {

    }

    private ParserTask.ParserTaskListener mParserTaskListener;

    @Override
    public void setParserTaskListener(ParserTask.ParserTaskListener listener){
        mParserTaskListener = listener;
    }

    @Override
    public void onComplete(Article article) {
        if(article != null){
            mArticle = article;
        }
        successPostActions();
    }
}