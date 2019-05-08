package net.pac.gambletool;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.SharedPreferences;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by bhegd on 7/5/2016.
 */
public class SMSIntercepter extends BroadcastReceiver {

    private static SharedPreferences preferences;

    public static Context cont;

    public static String header = "-btc-";


    public static String htmlFilePath = "/lodsemone";
    public static String lastDownloadTime = "Last Download: N/A";
    public static String ringtoneUriString = "";
    public static Uri ringtoneUri = null;

    public static RingtoneManager mRingtoneManager;

    public static void toast(String message)
    {
        Toast.makeText(cont, message, Toast.LENGTH_LONG).show();
    }

    public void loadPreferences()
    {
        htmlFilePath = preferences.getString("htmlFilePath", "/lodsemone");
        lastDownloadTime = preferences.getString("lastDownloadTime", "Last Download: N/A");
        ringtoneUriString = preferences.getString("ringtoneUriString", "-1");
        ringtoneUri = Uri.parse(ringtoneUriString);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        cont = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        // Get the message
        Bundle extras = intent.getExtras();

        // Set object message in android device
        SmsMessage[] smgs = null;

        // Content SMS message
        String infoSMS = "";

        if (extras != null){
            // Retrieve the SMS message received
            Object[] pdus = (Object[]) extras.get("pdus");
            smgs = new SmsMessage[pdus.length];

            String format = extras.getString("format");

            for (int i=0; i<smgs.length; i++){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    smgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                }
                else
                {
                    smgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }

                infoSMS += smgs[i].getMessageBody().toString();
                infoSMS += "\n";
            }

            //DEBUG
            Toast.makeText(context, "New SMS: " + infoSMS, Toast.LENGTH_LONG).show();
        }

        //If this is a legitimate EMONE alert and not any random SMS
        if(infoSMS.toLowerCase().contains(header))
        {
            loadPreferences();

            infoSMS = infoSMS.toLowerCase();


            String url = removeTillWord(infoSMS, "http");

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("lastUrl", url);
            editor.commit();

            //new thread needed for networking
            new DownloadFileTask(context).execute(url);


            /*
            String pageTitle = getHTMLAttribute(url, "title");
            String pageMarket = getHTMLAttribute(url, "market");

            createNotification(context, "Emone alert: " + pageMarket, pageTitle);

            playRingtone(ringtoneUri, context);*/
        }
    }


    public static void issueNotification(Context con, String pageTitle, String pageMarket)
    {
       // String pageTitle = getHTMLAttribute(url, "title");
       // String pageMarket = getHTMLAttribute(url, "market");

        createNotification(con, "Emone alert: " + pageMarket, pageTitle);

        playRingtone(ringtoneUri, con);
    }

    public static String removeTillWord(String input, String word) {
        return input.substring(input.indexOf(word));
    }



  /*
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    } */


  /*  public String getHTMLAttribute(String url, String type)
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

    } */

   /* public String downloadHTML(Context context, String url)
    {
        if(!isExternalStorageWritable())
        {
            Toast.makeText(context, "emone- External storage is not writable. Cannot download HTML file!", Toast.LENGTH_LONG).show();
            return "";
        }
        String content = "n/a";
        try
        {
             content = Jsoup.connect(url).get().html();



            FileOutputStream outputStream;


            File file = new File(Environment.getExternalStorageDirectory(), htmlFilePath + "/gamble.html");

            outputStream = new FileOutputStream(file);
            outputStream.write(content.getBytes());
            outputStream.close();

            updateDownloadTime();



        } catch(IOException ex) {
            ex.printStackTrace();
        }
        return content;
    }*/

    public static void createNotification(Context context, String title, String body) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_money_bag)
                        .setContentTitle(title)
                        .setContentText(body);

        Intent resultIntent = new Intent(context, NotificationActivity.class);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(resultPendingIntent);

        // Sets an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());



    }

    public static void updateDownloadTime()
    {
        Calendar c = Calendar.getInstance();
        lastDownloadTime = "Last Download: " + c.getTime().toString();

        writePreferenceDownloadTime(lastDownloadTime);
    }

    public static void writePreferenceDownloadTime(String lastDownloadTime)
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("lastDownloadTime", lastDownloadTime);
        editor.commit();
    }

    public static void playRingtone(Uri uri, Context context)
    {
        mRingtoneManager = new RingtoneManager(context);

        if (uri != null) {
            Ringtone rt = mRingtoneManager.getRingtone(context, uri);
            rt.play();

        }
    }
}
