package net.pac.gambletool;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {

    public RingtoneManager mRingtoneManager;

    public SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        loadPreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();


        loadPreferences();
    }

    public void chooseRingtone(View view)
    {
        mRingtoneManager = new RingtoneManager(this);
        Cursor mcursor = mRingtoneManager.getCursor();
        String title = mRingtoneManager.EXTRA_RINGTONE_TITLE;
        Intent Mringtone = new Intent(mRingtoneManager.ACTION_RINGTONE_PICKER);
        Mringtone.putExtra(mRingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);

        Mringtone.putExtra(mRingtoneManager.EXTRA_RINGTONE_TITLE, "Choose Notification Sound...");

        String uri = null;
        if(uri != null) {
            Mringtone.putExtra(mRingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse( uri ));
        } else {
            Mringtone.putExtra(mRingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri)null);
        }

        startActivityForResult(Mringtone, 0);



    }

    public void loadPreferences()
    {
        String htmlFilePath = preferences.getString("htmlFilePath", "/lodsemone");
        //String lastDownloadTime = preferences.getString("lastDownloadTime", "Last Download: N/A");
        //String ringtoneUriString = preferences.getString("ringtoneUriString", "-1");

        EditText htmlFilePathBox = (EditText) findViewById(R.id.editText_htmlFilePath);
        htmlFilePathBox.setText(htmlFilePath);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent Mringtone) {
        if(resultCode == RESULT_OK) {

            Uri uri = Mringtone.getParcelableExtra(mRingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            String text = uri.toString();

            //DEBUG
            Toast.makeText(getApplicationContext(), "Selected ringtone: " + text, Toast.LENGTH_LONG).show();

            if (uri != null) {
                Ringtone rt = mRingtoneManager.getRingtone(this, uri);
                rt.play();

            }


            writePreferenceRingtone(text);
            SMSIntercepter.ringtoneUri = uri;

        }

    }

    /*
    public void playRingtone(RingtoneManager rm, Uri uri)
    {
        if (uri != null) {
            Ringtone rt = rm.getRingtone(this, uri);
            rt.play();

        }
    }
    */


    public void writePreferenceRingtone(String uriString)
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ringtoneUriString", uriString);
        editor.commit();
    }

    public void writePreferences(View view)
    {
        EditText htmlFilePathBox = (EditText) findViewById(R.id.editText_htmlFilePath);
        String path = htmlFilePathBox.getText().toString();


        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("htmlFilePath", path);
        editor.commit();

        File mFolder = new File(Environment.getExternalStorageDirectory() + path);
        if (!mFolder.exists()) {
            mFolder.mkdir();
        }
    }



}
