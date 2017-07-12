package com.example.yashwanth.simplereminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class ChooserActivity extends AppCompatActivity {
    DatePicker datePicker;
    TimePicker timePicker;
    EditText messageToSay;
    Calendar cal,c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);
        c=Calendar.getInstance();
        datePicker = (CustomDatePicker) findViewById(R.id.datePicker);
        timePicker = (CustomTimePicker) findViewById(R.id.timePicker);
        datePicker.init(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH),null);
        timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
        messageToSay= (EditText)findViewById(R.id.editMessage);
    }
    //called on button click
    public void setDateAndTime(View view)
    {
          //from date picker
        int yr=datePicker.getYear();
        int mth=datePicker.getMonth();
        int date=datePicker.getDayOfMonth();
        //from time picker
        int hr=timePicker.getCurrentHour();
        int min= timePicker.getCurrentMinute();


        String str=yr+"/"+mth+"/"+date+"   "+hr+":"+min;
        //Toast.makeText(this, "Selected "+" "+str, Toast.LENGTH_SHORT).show();
        //set to calendar
        cal= Calendar.getInstance();
        cal.set(yr,mth,date,hr,min);
        //from EditText
        String message=messageToSay.getText().toString();
        if(message.equals(null)||message.equals(""))
            Toast.makeText(this, "PLEASE ENTER A MESSAGE", Toast.LENGTH_SHORT).show();
        else
            send(cal,message);

    }

    private void send(Calendar cal, String s) {
        Intent intent= getIntent();
        intent.putExtra("calendar",cal);
        intent.putExtra("message",s);
        this.setResult(RESULT_OK,intent);
        finish();
    }
}
