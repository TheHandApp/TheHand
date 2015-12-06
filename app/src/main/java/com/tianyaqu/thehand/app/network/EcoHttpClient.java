package com.tianyaqu.thehand.app.network;

import android.util.Log;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import cz.msebera.android.httpclient.HttpResponseInterceptor;
import cz.msebera.android.httpclient.client.params.ClientPNames;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Created by Alex on 2015/11/19.
 */
public class EcoHttpClient {
    private static final String BASE_URL = "";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }
/*
    public static DefaultHttpClient getHttpClient() {
        return ((DefaultHttpClient) client.getHttpClient());
    }
    */

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public static void addResponseInterceptor(HttpResponseInterceptor interceptor){
        Log.d("EcoHttpClient",client.getHttpClient().getClass().getName());
        Log.d("EcoHttpClient","is DefaultHttpClient: "+ (client.getHttpClient() instanceof DefaultHttpClient));
        DefaultHttpClient defaultHttpClient = (DefaultHttpClient)client.getHttpClient();
        defaultHttpClient.addResponseInterceptor(interceptor);
    }

    public static void setCircularRedirect(boolean decision){
        client.getHttpClient().getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, decision);
    }

}
