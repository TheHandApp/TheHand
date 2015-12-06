package com.tianyaqu.thehand.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tianyaqu.thehand.app.network.Fetcher;
import cz.msebera.android.httpclient.Header;
import org.json.JSONObject;

/**
 * Created by Alex on 2015/12/4.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener{
    private AlertDialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.activity_settings_prefs);

        findPreference(getString(R.string.pref_key_settings_get_previous))
                .setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_key_settings_about))
                .setOnPreferenceClickListener(this);
        findPreference(getString(R.string.pref_key_settings_update))
                .setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = "";
        if(preference != null) {
            key = preference.getKey();
        }
        if (key.equals(getString(R.string.pref_key_settings_get_previous))) {
            return getPreviousEditions();
        } else if (key.equals(getString(R.string.pref_key_settings_about))) {
            return showAbout();
        } else if (key.equals(getString(R.string.pref_key_settings_update))) {
            return checkUpdate();
        }
        return false;
    }

    private Boolean getPreviousEditions(){
        Launcher.launchActivity(getActivity(),EditionSelectorActivity.class);
        return true;
    }

    private Boolean showAbout(){
        Log.d(this.getClass().getName(),"about pressed");
        Launcher.launchActivity(getActivity(),AboutActivity.class);
        return true;
    }

    private Boolean checkUpdate(){
        Log.d(this.getClass().getName(),"check pressed");
        Fetcher.checkApkUpdate(mHandler);
        return true;
    }

    private void showDialog(String newVersion,final String url){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setMessage(getString(R.string.settings_new_version) + newVersion);

        dialogBuilder.setPositiveButton(getString(R.string.settings_update_now), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Launcher.launchUrl(getActivity(),Intent.ACTION_VIEW,url);
            }
        });

        dialogBuilder.setNegativeButton(getString(R.string.settings_update_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mDialog = dialogBuilder.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    private void noUpdate(){
        String msg = getString(R.string.settings_no_new_version) + AppContext.getCurrentVersion();
        Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
    }

    private AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int i, Header[] headers, byte[] bytes) {
            try{
                JSONObject json = new JSONObject(new String(bytes));
                String t = json.get(Constants.APP_VER_CODE).toString();
                int ver = Integer.parseInt(t);
                if(ver > AppContext.getCurrentVersionCode()){
                    String url = json.get(Constants.APP_DOWNLOAD_URL).toString();
                    String versionName = json.get(Constants.APP_VER_NAME).toString();
                    showDialog(versionName,url);
                }else{
                    noUpdate();
                }
            }catch (Exception e){

            }
        }

        @Override
        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
        }
    };
}
