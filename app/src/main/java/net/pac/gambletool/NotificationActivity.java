package net.pac.gambletool;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;

/**
 * Created by bhegd on 7/7/2016.
 */
public class NotificationActivity extends Activity {
    private static SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String lastUrl = preferences.getString("lastUrl", "http://pacnet-netty.github.io/samplepage.html");


        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(lastUrl));
        startActivity(browserIntent);

        finish();
    }
}