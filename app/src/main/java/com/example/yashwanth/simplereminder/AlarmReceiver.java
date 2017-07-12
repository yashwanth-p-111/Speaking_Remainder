package com.example.yashwanth.simplereminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;


public class AlarmReceiver extends BroadcastReceiver {

    Context mContext;
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext=context;
      //get the extras
        String message=intent.getStringExtra("msg");
        int hourly=intent.getIntExtra("hourly",-1);
        //get the audio manager for vibration
        AudioManager am= (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        Vibrator vib= (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        if(!(am.getRingerMode()==AudioManager.RINGER_MODE_SILENT)&&!am.isMusicActive()&&message!=null)
        {


                vib.vibrate(1500);
                Intent intent1= new Intent(context,Speaker.class);
                intent1.putExtra("speak_msg",message);
                intent1.putExtra("hourly",hourly);
                //intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startService(intent1);


        }
        else
        {
            int rm=am.getRingerMode();
            vib.vibrate(2000);
            //set again to previous mode
            am.setRingerMode(rm);
        }
        //also display the notification
        if(hourly!=1)
        displayNotification(message);
    }
    public void displayNotification(String message)
    {
        NotificationManager notificationManager= (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        //define an action for the notification
        //open an intent you want pass as pendingIntent
        Intent intent=new Intent(mContext,MainActivity.class);
        PendingIntent pIntent=PendingIntent.getActivity(mContext, (int)System.currentTimeMillis(),intent,0);

        //buid a compatible notification builder
        NotificationCompat.Builder builder =new NotificationCompat.Builder(mContext)
                                                .setColor(ContextCompat.getColor(mContext,R.color.colorPrimaryDark))
                                                .setSmallIcon(R.drawable.alarm)
                                                .setContentTitle("Remainder...")
                                                .setContentText(message).setPriority(Notification.PRIORITY_MAX)
                                                .setContentIntent(pIntent).setTicker("Remainder Alert").setAutoCancel(true);
        notificationManager.notify(0,builder.build());
    }



}
