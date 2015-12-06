package com.tianyaqu.thehand.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.tianyaqu.thehand.app.utils.CommonUtils;

/**
 * Created by Alex on 2015/12/4.
 */
public class AboutActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView version = (TextView)findViewById(R.id.about_version);
        version.setText(getString(R.string.app_version) + " " + AppContext.getCurrentVersion());

        TextView copyright = (TextView)findViewById(R.id.about_copyright);
        String copyrightContent = String.format(getString(R.string.app_copyright), CommonUtils.currentYear(),getString(R.string.app_project));
        copyright.setText(copyrightContent);
    }
}