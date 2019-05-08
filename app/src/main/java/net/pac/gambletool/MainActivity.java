package net.pac.gambletool;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {


    public SharedPreferences preferences;

    public String lastDownloadTime = "Last Download: N/A";
    public String htmlFilePath = "/lodsemone";

    public void loadPreferences()
    {
        htmlFilePath = preferences.getString("htmlFilePath", "/lodsemone");
        lastDownloadTime = preferences.getString("lastDownloadTime", "Last Download: N/A");
        //String ringtoneUriString = preferences.getString("ringtoneUriString", "-1");


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        loadPreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadPreferences();

        TextView lastDownloadTimeTextView = (TextView) findViewById(R.id.label_lastDownloadTime);
        lastDownloadTimeTextView.setText(lastDownloadTime);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }

    public void openHTML(View view)
    {
        loadPreferences();

        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        File file = new File(Environment.getExternalStorageDirectory(), htmlFilePath + "/gamble.html");
        intent.setDataAndType(Uri.fromFile(file), "text/html");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void openSettings(View view)
    {
        Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);

        MainActivity.this.startActivity(myIntent);
    }
}
