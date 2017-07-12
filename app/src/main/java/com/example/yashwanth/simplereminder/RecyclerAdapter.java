package com.example.yashwanth.simplereminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.yashwanth.simplereminder.MainActivity.months;

/**
 * Created by Yashwanth on 15-Dec-16.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.CustomViewHolder> {
    LayoutInflater layoutInflater;
   ArrayList<DataItem> item;
    DataItem dataItem;
    TextView empty_message;
    boolean set = false;
    //use this context when needed
    Context mContext;
    public RecyclerAdapter(Context context) {
        mContext=context;
        layoutInflater = LayoutInflater.from(context);
        item=new ArrayList<>();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = layoutInflater.inflate(R.layout.single_row, parent, false);
        CustomViewHolder cvh = new CustomViewHolder(item);
        return cvh;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int position) {
        dataItem = item.get(position);
        Calendar calendar=dataItem.calendar;
        int hour=calendar.get(Calendar.HOUR);
        if(hour==0) hour=12;
        int minutes=calendar.get(Calendar.MINUTE);
        //get AM_PM
        int a=calendar.get(Calendar.AM_PM);
        String am_pm=(a==Calendar.AM)?"AM":"PM";
        int year=calendar.get(Calendar.YEAR);
        int month= calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);
       String s_day=day+"-"+months[month+1]+"-"+year;
        //format the time eg : 5:5 => 05:05
        SimpleDateFormat format=new SimpleDateFormat("hh:mm");
        String s_time=format.format(calendar.getTime())+" "+am_pm;


            holder.date.setText(s_day);
            holder.time.setText(s_time);
            holder.message.setText(dataItem.message);

           // Toast.makeText(mContext, ""+position, Toast.LENGTH_SHORT).show();
            set=false;
            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=holder.getAdapterPosition();
                    remove(pos);
                   // Toast.makeText(mContext, "position"+pos, Toast.LENGTH_SHORT).show();
                }
            });




    }
 //public void add(int pos,DataItem dataItem)
    public void add(ArrayList<DataItem> item)
 {

     //item.add(pos,dataItem);
     //Toast.makeText(mContext, "added at"+pos, Toast.LENGTH_SHORT).show();
     //notifyItemInserted(item.size()-1);
     this.item=item;
     store(this.item);
     notifyItemRangeChanged(0,item.size());

 }
   public void remove(int position)
  {
      cancelAlarmTime(item.get(position));
      item.remove(position);
      store(item);
      notifyItemRemoved(position);
  }
    @Override
    public int getItemCount() {

        return  item.size();
    }


  public void cancelAlarmTime(DataItem r_item){
      Intent cancelIntent=new Intent(mContext,AlarmReceiver.class);
      PendingIntent pendingIntent=PendingIntent.getBroadcast(mContext,r_item.req_code,cancelIntent,0);
      AlarmManager am=(AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
      am.cancel(pendingIntent);
      Toast.makeText(mContext, "Alarm Cancelled", Toast.LENGTH_SHORT).show();
  }
    public static class CustomViewHolder extends RecyclerView.ViewHolder{
        TextView date, time, message;
         Button button;
        public CustomViewHolder(View item) {
            super(item);
            date = (TextView) item.findViewById(R.id.date);
            time = (TextView) item.findViewById(R.id.time);
            message = (TextView) item.findViewById(R.id.message);
            button=(Button)item.findViewById(R.id.button);

        }
    }
    public void store(ArrayList<DataItem> item)
    {
        SharedPreferences sp= mContext.getSharedPreferences("mySp",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        Gson gson=new Gson();
        String json=gson.toJson(item);
        editor.putString("alarm_list",json);
        editor.apply();
        //Toast.makeText(mContext, "Size"+item.size(), Toast.LENGTH_SHORT).show();
    }

}
