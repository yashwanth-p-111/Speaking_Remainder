package com.example.yashwanth.simplereminder;

/**
 * Created by Yashwanth on 14-May-17.
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class Speaker extends Service implements TextToSpeech.OnInitListener
{
    TextToSpeech tts;
String msg_to_speak;
    int hourly;
 public static final  String base_message="You Got a Remainder.";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //Toast.makeText(this, "ON CREATE", Toast.LENGTH_SHORT).show();
        super.onCreate();
        tts= new TextToSpeech(this,this);
    }



    public void speakOut(String message)
    {
        //Toast.makeText(this, "SPEAKING", Toast.LENGTH_SHORT).show();
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int amStreamMusicMaxVol = am.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
        am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, amStreamMusicMaxVol, 0);
        //check if device is mute,vibrate or silent
        //this is for normal alarms
        if(message!=null)
        {
            Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
            } else
                tts.speak(message, TextToSpeech.QUEUE_FLUSH, null);
            //display the service after a certain delay
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Speaker.this.stopSelf();
                }
            },10000);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
          msg_to_speak=intent.getStringExtra("speak_msg");
          hourly=intent.getIntExtra("hourly",-1);
        Toast.makeText(this, ""+hourly, Toast.LENGTH_SHORT).show();
          return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int language = tts.setLanguage(Locale.ENGLISH);
            tts.setPitch(1.3f);
            tts.setSpeechRate(0.9f);


            if (language == TextToSpeech.LANG_MISSING_DATA || language == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "The language not available", Toast.LENGTH_SHORT).show();
            }
            else{
                //Toast.makeText(this, "Initialisation", Toast.LENGTH_SHORT).show();
                if(hourly!=1)
                {
                    speakOut(base_message+"\""+msg_to_speak.trim()+"\"");
                }
                else
                {
                    //get the current hour
                    Calendar current=Calendar.getInstance();
                    int hour=current.get(Calendar.HOUR);
                    int a=current.get(Calendar.AM_PM);
                    String ap=(Calendar.AM==a)?"AM":"PM";
                    //build the message
                    String m="The Time is "+hour+ap;
                    speakOut(m);
                }
            }

        }
        else
            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
       // Toast.makeText(this, "DESTROYING SERVICE", Toast.LENGTH_SHORT).show();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}

