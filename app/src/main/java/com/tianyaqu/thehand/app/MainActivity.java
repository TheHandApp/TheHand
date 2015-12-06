package com.tianyaqu.thehand.app;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.tianyaqu.thehand.app.database.Parser;
import com.tianyaqu.thehand.app.network.Fetcher;
import com.tianyaqu.thehand.app.utils.CommonUtils;
import cz.msebera.android.httpclient.*;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.protocol.ExecutionContext;
import cz.msebera.android.httpclient.protocol.HttpContext;

import java.io.IOException;
import java.net.URI;


public class MainActivity extends AppCompatActivity {
    private ViewPager vp;
    private SmartTabLayout smartTab;
    private long newIssueNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vp = (ViewPager)findViewById(R.id.viewpager);
        smartTab = (SmartTabLayout)findViewById(R.id.viewpagertab);

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add(R.string.News, SectionFragment.class)
                .add(R.string.ThisWeek, SectionFragment.class)
                .add(R.string.Leaders, SectionFragment.class)
                .add(R.string.Letters, SectionFragment.class)
                .add(R.string.Briefing, SectionFragment.class)
                .add(R.string.US, SectionFragment.class)
                .add(R.string.Americas, SectionFragment.class)
                .add(R.string.Asia, SectionFragment.class)
                .add(R.string.China, SectionFragment.class)
                .add(R.string.MEAA, SectionFragment.class)
                .add(R.string.Europe, SectionFragment.class)
                .add(R.string.Britain, SectionFragment.class)
                .add(R.string.International, SectionFragment.class)
                .add(R.string.Business, SectionFragment.class)
                .add(R.string.Finance, SectionFragment.class)
                .add(R.string.Science, SectionFragment.class)
                .add(R.string.Books, SectionFragment.class)
                .add(R.string.Obituary, SectionFragment.class)
                .add(R.string.Figures, SectionFragment.class)
                .create());

        vp.setAdapter(adapter);
        smartTab.setViewPager(vp);
    }

    private void loadIndex(){
        String url = "http://www.economist.com/printedition/2015-11-14";
        Fetcher.fetchHtml(url,mHandler);
    }

    private HttpResponseInterceptor interceptor_302 = new HttpResponseInterceptor(){
        @Override
        public void process(HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {
            if (httpResponse.getStatusLine().getStatusCode() == 302) {
                final URI reqUri = ((HttpUriRequest) httpContext.getAttribute(ExecutionContext.HTTP_REQUEST)).getURI();
                final HttpHost currentHost = (HttpHost) httpContext.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
                final String url = (reqUri.isAbsolute()) ? reqUri.toString() : (currentHost.toURI() + reqUri.getPath());
                final String redirect = httpResponse.getLastHeader("Location").getValue();

                String pattern = "([0-9]{4}-[0-9]{2}-[0-9]{2})";
                String strDate="";
                if(url.equals(Fetcher.ROOT)) {
                    strDate = CommonUtils.splitDate(pattern,redirect);
                    newIssueNum = CommonUtils.dateStrToLong("yyyy-MM-dd",strDate);

                }
                Log.d("interceptor",redirect + ":::" + strDate + " "+ newIssueNum);
                Log.d("interceptor",url);
            }
        }
    };

    private AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            Log.d("main fetch success","url:root");
            Parser.parseEdition(new String(bytes));
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            Log.d("main fetch failed","url:root");
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    /*
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        Log.d(this.getClass().getName(),"press menu key");

        Launcher.launchActivity(this,SettingsActivity.class);

        return super.onMenuOpened(featureId, menu);
    }
    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.menu_settings) {
            Log.d(this.getClass().getName(),"setting pressed");
            Launcher.launchActivity(this,SettingsActivity.class);
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }
}


