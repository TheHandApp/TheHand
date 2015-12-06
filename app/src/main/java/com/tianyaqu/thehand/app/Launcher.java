package com.tianyaqu.thehand.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by Alex on 2015/12/4.
 */
public class Launcher {
    public static void launchActivity(Context context,Class to){
        Intent i = new Intent(context,to);
        context.startActivity(i);
    }

    public static void launchUrl(Context context,String action, String url){
        String title = context.getResources().getString(R.string.openInBrowser_action);
        Intent chooser = Intent.createChooser(new Intent(action, Uri.parse(url)),title);
        context.startActivity(chooser);
    }
}
