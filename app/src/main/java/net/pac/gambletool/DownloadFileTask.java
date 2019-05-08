package net.pac.gambletool;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by bhegd on 7/7/2016.
 */
public class DownloadFileTask extends AsyncTask<String, Void, String> {

    private Context mContext;

    public String currUrl = "";

    public DownloadFileTask(Context con)
    {
        mContext = con;
    }

    @Override
    protected String doInBackground(String... junk) {
        for(String s : junk)
        {
            downloadHTML(s);

            currUrl = s;

            String pageTitle = getHTMLAttribute(currUrl, "title");
            String pageMarket = getHTMLAttribute(currUrl, "market");

            SMSIntercepter.issueNotification(mContext, pageTitle, pageMarket);
        }

        return "";
    }



    protected void onPostExecute(Long result) {

        String pageTitle = getHTMLAttribute(currUrl, "title");
        String pageMarket = getHTMLAttribute(currUrl, "market");

        SMSIntercepter.issueNotification(mContext, pageTitle, pageMarket);
    }



    public String getHTMLAttribute(String url, String type)
    {
        try
        {
            Document doc = Jsoup.connect(url).get();
            if(type.equals("title"))
            {
                return doc.title();
            }
            else
            {
                Element market = doc.select(type).first();
                return market.text();
            }

        } catch(IOException ex)
        {
            ex.printStackTrace();
        }
        return "n/a";

    }

    public String downloadHTML(String url)
    {

        String content = "n/a";
        try
        {
            content = Jsoup.connect(url).get().html();



            FileOutputStream outputStream;


            File file = new File(Environment.getExternalStorageDirectory(), SMSIntercepter.htmlFilePath + "/gamble.html");

            outputStream = new FileOutputStream(file);
            outputStream.write(content.getBytes());
            outputStream.close();

            SMSIntercepter.updateDownloadTime();



        } catch(IOException ex) {
            ex.printStackTrace();
        }
        return content;
    }
}
