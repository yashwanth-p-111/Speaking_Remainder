package com.example.yashwanth.simplereminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerAdapter adapter;
    PendingIntent pendingIntent;
    public static int req=0;
    Calendar calendar;
    String message;
    ArrayList<DataItem> items=new ArrayList<>();
    Toolbar toolbar;
    SwitchCompat switchHour;
   public static final int REQ_CODE=1;
 public static final String[] months={"","Jan","Feb","Mar","Apr","May","June","July","Aug","Sep","Oct","Nov","Dec"};
    private boolean duplicate=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar= (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        switchHour= (SwitchCompat) findViewById(R.id.switchHour);
        recyclerView = (RecyclerView) findViewById(R.id.rv);
        switchHour.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
              setHourlyRemainder(isChecked);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerAdapter(this);
        recyclerView.setAdapter(adapter);
        //recyclerView.setHasFixedSize(true);
         ArrayList<DataItem> currentList=getStoredAlarms();
         if(currentList!=null)
         {
             for(DataItem d : currentList)
             {
               items.add(d);
             }
             adapter.add(currentList);
         }
       req=getRequestCode();
    }
    //store the requestcode for pending intent
    public void storeRequestCode(int req) {
        SharedPreferences preferencces = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferencces.edit();
        editor.putInt("request", req);
        editor.apply();

    }
    //get the stored request code
    public int getRequestCode() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        int reqCode = sp.getInt("request", 0);
        return reqCode;
    }
    //get the stored alarms in the preferences
    public ArrayList<DataItem> getStoredAlarms()
    {
        SharedPreferences sp= getSharedPreferences("mySp",Context.MODE_PRIVATE);;
        Gson gson=new Gson();
        String json=sp.getString("alarm_list",null);
        if(json!=null)
        {
            Type type= new TypeToken<ArrayList<DataItem>>(){}.getType();
            ArrayList<DataItem> items=gson.fromJson(json,type);
            return items;
        }
        else
            return null;
    }
    //when add button is clicked process the alarm by calling choserActivity
       public void onClickAdd(View view) {
        Intent intent =  new Intent(this,ChooserActivity.class);
        startActivityForResult(intent,REQ_CODE);
    }
    //receive result from the ChooserActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(REQ_CODE==requestCode)
        {
            if(resultCode==RESULT_OK)
            {
                Calendar current=Calendar.getInstance();
                calendar=(Calendar)data.getSerializableExtra("calendar");
                //Toast.makeText(this, (calendar.getTimeInMillis())/1000+"", Toast.LENGTH_SHORT).show();
                //Toast.makeText(this, (calendar.getTimeInMillis())+"", Toast.LENGTH_SHORT).show();
                message=data.getStringExtra("message");//check if alarm is set previously for this calendar

                for(DataItem d:items)
               {
                   if((d.calendar.getTimeInMillis()/60000)*60000==(calendar.getTimeInMillis()/60000)*60000)
                   {
                       Toast.makeText(this, "Remainder Already exists", Toast.LENGTH_SHORT).show();
                       duplicate=true;
                       break;
                   }
               }
                if(!duplicate) addToView_setAlarm(calendar,message);
            }
        }
    }




//add new items
    private void addToView_setAlarm(Calendar calendar, String message) {
        DataItem dataItem=new DataItem();
        dataItem.calendar=calendar;
        //capitalize first character
        char cap_char=message.trim().toUpperCase().charAt(0);
        dataItem.message=cap_char+message.substring(1,message.length());
        //request must be different for  different alarms... store in the preferences
        req++;
        //useful while deleting
        dataItem.req_code=req;

        storeRequestCode(req);
      //  Toast.makeText(this, ""+req, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("msg",message);
        //retreieve a PI on AlarmReceiver class
        pendingIntent = PendingIntent.getBroadcast(this,req, intent, 0);
        Calendar current = Calendar.getInstance();
        if (calendar.compareTo(current) < 0) {
            Toast.makeText(this, "INVALID TIME", Toast.LENGTH_SHORT).show();
        } else {

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            //Trigger(Set) the alarm
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //Toast.makeText(this, ""+(System.currentTimeMillis()/1000)*1000+(Math.abs(current.getTimeInMillis()-calendar.getTimeInMillis())/1000)*1000, Toast.LENGTH_SHORT).show();
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,(calendar.getTimeInMillis()/60000)*60000, pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP,(calendar.getTimeInMillis()/60000)*60000, pendingIntent);
            }
            //Toast.makeText(this, "Alarm Set for " + time.getText().toString(), Toast.LENGTH_SHORT).show();
            items.add(dataItem);
            adapter.add(items);
        }

    }

    //enable or disable hourly remainder
    public void setHourlyRemainder(boolean state)
    {
        int h2m=3600000;
        long  current=System.currentTimeMillis();
        long prev=(current/h2m)*h2m;
        long next=prev+h2m;
        int gap=(int)(next-prev)/h2m;
        //Toast.makeText(this, "Previouss "+prev/h2m, Toast.LENGTH_SHORT).show();
        AlarmManager alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);
        final int r_code=-11;
        Intent intent =new Intent(this,AlarmReceiver.class);
        intent.putExtra("hourly",1);
        intent.putExtra("msg","");
        PendingIntent pi=PendingIntent.getBroadcast(this,r_code,intent,0);

        if(state==true)
        {
            alarmManager.cancel(pi);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,next,h2m,pi);
            Toast.makeText(this, "Hourly Remainder Set", Toast.LENGTH_SHORT).show();
        }
        else
        {
            alarmManager.cancel(pi);
            Toast.makeText(this, "Hourly Remainder Cancelled", Toast.LENGTH_SHORT).show();
        }



    }
}
