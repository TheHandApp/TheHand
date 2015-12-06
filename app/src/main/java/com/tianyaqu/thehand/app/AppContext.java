package com.tianyaqu.thehand.app;

import android.app.Application;
import android.content.Context;
import com.activeandroid.ActiveAndroid;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.tianyaqu.thehand.app.utils.PackageUtil;
import com.tianyaqu.thehand.app.utils.PrefsUtil;

import java.io.File;

/**
 * Created by Alex on 2015/11/21.
 */
public class AppContext extends Application {
    private static long latestIssueNum = 0;
    private static String currentVersion;
    private static int currentVersionCode;

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        initConf();
        ActiveAndroid.initialize(this);
        initImageLoader(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }

    private void initConf(){
        currentVersion = PackageUtil.getVersionName(this);
        currentVersionCode = PackageUtil.getVersionCode(this);
        latestIssueNum = PrefsUtil.getLong(PrefsUtil.CONFIG_KEY.LATEST_ISSUE_NUM);
    }

    private void initImageLoader(Context context){
        File cacheDir = StorageUtils.getOwnCacheDirectory(context,Constants.IMG_CACHE_DIR);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .diskCache(new UnlimitedDiscCache(cacheDir))
                .build();

        ImageLoader.getInstance().init(config);
    }

    public static long getLatestIssueNum() {
        return latestIssueNum;
    }

    public static void setLatestIssueNum(long latestIssueNum) {
        AppContext.latestIssueNum = latestIssueNum;
        PrefsUtil.setLong(PrefsUtil.CONFIG_KEY.LATEST_ISSUE_NUM,latestIssueNum);
    }

    public static String getCurrentVersion() {
        return currentVersion;
    }

    public static int getCurrentVersionCode() {
        return currentVersionCode;
    }

    public static Context getContext() {
        return mContext;
    }
}
