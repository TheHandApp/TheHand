package com.tianyaqu.thehand.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.tianyaqu.thehand.app.database.Article;
import com.tianyaqu.thehand.app.database.ArticleEntryModel;
import com.tianyaqu.thehand.app.network.Fetcher;
import com.tianyaqu.thehand.app.task.ParserTask;
import com.tianyaqu.thehand.app.utils.CommonUtils;
import com.tianyaqu.thehand.app.widget.PostsSwipeRefreshLayout;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import cz.msebera.android.httpclient.*;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.protocol.ExecutionContext;
import cz.msebera.android.httpclient.protocol.HttpContext;

import java.io.IOException;
import java.net.URI;

public class SectionFragment extends Fragment implements ArticlesAdapter.OnArticleSelectedListener, SwipeRefreshLayout.OnRefreshListener,ParserTask.ParserTaskListener {
    private final static String[] sectionNames = { "Latest","The world this week", "Leaders", "Letters", "Briefing",
            "United States", "The Americas", "Asia", "China", "Middle East and Africa", "Europe",
            "Britain", "International", "Business", "Finance and economics", "Science and technology",
            "Books and arts", "Obituary", "Economic and financial indicators"};

    private RecyclerView mRecyclerView;
    private ArticlesAdapter mAdapter;
    private PostsSwipeRefreshLayout mRefreshLayout;
    private TextView mNotifier;
    private String mSectionName;
    private long newIssueNum;
    private static boolean interceptorAdded = false;
    private final String pattern = "([0-9]{4}-[0-9]{2}-[0-9]{2})";


    private ArticlesAdapter getAdapter(){
        if(mAdapter == null){
            mAdapter = new ArticlesAdapter(getActivity(),mSectionName);
            mAdapter.setArticleSelectedListener(this);
        }
        return mAdapter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setParserTaskListener(this);
        int position = FragmentPagerItem.getPosition(getArguments());
        mSectionName = sectionNames[position];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup)inflater.inflate(R.layout.fragment_section, container, false);
        mNotifier = (TextView)root.findViewById(R.id.notifier);
        mRecyclerView = (RecyclerView)root.findViewById(R.id.recylerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRefreshLayout = (PostsSwipeRefreshLayout)root.findViewById(R.id.container_layout);
        mRefreshLayout.setColorSchemeResources(R.color.process_bar_color);
        mRefreshLayout.setOnRefreshListener(this);
        return root;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(this.getClass().getName(),"set visible pre");
        if (isVisibleToUser) {
            Log.d(this.getClass().getName(),"set visible hint");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mRecyclerView.getAdapter() == null){
            mRecyclerView.setAdapter(getAdapter());
        }
        Log.d(this.getClass().getName(),"on resume" + mSectionName);
        loadArticles();
    }

    private void loadArticles(){
        getAdapter().loadArticles();
    }

    private void textNotify(final int i) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mNotifier.setText(i);
                mNotifier.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mNotifier.setVisibility(View.GONE);
                    }
                }, 2000);
            }
        }, 1000);
    }

    @Override
    public void onArticleSelected(View v,ArticleEntryModel entry) {
        Log.d(this.getClass().getName(),"save readed tag to db");

        //TODO click
        if(!entry.readed){
            entry.readed = true;
            entry.save();
        }

        Intent intent = new Intent(getActivity(), ReaderViewActivity.class);
        intent.putExtra(Constants.ARG_SECTION_NAME,mSectionName);
        intent.putExtra(Constants.ARG_ISSUE_NUM,entry.section.edition.issueNum);
        intent.putExtra(Constants.ARG_ARTICLE_URL,entry.url);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        if(!mSectionName.equals(Constants.SECTION_BLOG)) {
            loadCurrentEdition();
        }else
        {
            loadLatestNews();
            loadCurrentEdition();
        }
    }

    private void loadLatestNews(){
        Fetcher.fetchLatestNews(blogHandler);
    }
    private void loadCurrentEdition(){
        if(!interceptorAdded) {
            Fetcher.setClientCircularRedirect();
            Fetcher.addResponseInterceptor(interceptor_302);
            interceptorAdded = true;
        }
        Fetcher.fetchCurrentEdition(mHandler);
    }

    private HttpResponseInterceptor interceptor_302 = new HttpResponseInterceptor(){
        @Override
        public void process(HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {
            if (httpResponse.getStatusLine().getStatusCode() == 302) {
                final URI reqUri = ((HttpUriRequest) httpContext.getAttribute(ExecutionContext.HTTP_REQUEST)).getURI();
                final HttpHost currentHost = (HttpHost) httpContext.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
                final String url = (reqUri.isAbsolute()) ? reqUri.toString() : (currentHost.toURI() + reqUri.getPath());
                final String redirect = httpResponse.getLastHeader("Location").getValue();

                String strDate = "";
                if(url.equals(Fetcher.ROOT)) {
                    strDate = CommonUtils.splitDate(pattern,redirect);
                    newIssueNum = CommonUtils.dateStrToLong("yyyy-MM-dd",strDate);

                }
                Log.d(SectionFragment.class.getName(),"interceptor" + redirect + ":::" + strDate + " "+ (newIssueNum >AppContext.getLatestIssueNum()));
                Log.d(SectionFragment.class.getName(),"interceptor: " + url);
            }
        }
    };


    private AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            Log.d(SectionFragment.class.getName(),"main fetch success" + "url:root" + CommonUtils.longToDateStr("yyyy-MM-dd",newIssueNum));
            if(AppContext.getLatestIssueNum() < newIssueNum) {
                AppContext.setLatestIssueNum(newIssueNum);
                new ParserTask(Constants.PARSER_EDITION,new String(bytes), mParserTaskListener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }else {
                textNotify(R.string.no_new_issue);
            }
            mRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Log.d(SectionFragment.class.getName(),"main fetch failed url:root");
            mRefreshLayout.setRefreshing(false);
            textNotify(R.string.internet_error);
        }
    };

    private AsyncHttpResponseHandler blogHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            new ParserTask(Constants.PARSER_BLOG_INDEX,new String(bytes), mParserTaskListener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Log.d(SectionFragment.class.getName(),"main fetch failed url:root");
            mRefreshLayout.setRefreshing(false);
            textNotify(R.string.internet_error);
        }
    };

    private ParserTask.ParserTaskListener mParserTaskListener;

    @Override
    public void setParserTaskListener(ParserTask.ParserTaskListener listener){
        mParserTaskListener = listener;
    }

    @Override
    public void onComplete(Article article) {
        loadArticles();
        mRefreshLayout.setRefreshing(false);
    }
}
