package com.example.project;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Database extends Activity {

    DatabaseHelper mydbh;
    private ListView lv;
    private DatePicker dp;

    /*
    * creates all the components needed to show (layout, listview, datepicker)
    * */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database);
        lv = (ListView) findViewById(R.id.listView);
        dp = (DatePicker) findViewById(R.id.datePicker1);

        mydbh = new DatabaseHelper(this);

        /*
        * run poplv() directly when created (when app is opened)
        * */
        poplv();
    }


    /*
    * get date from the datepicker then find the data corresponding to the date the client picked
    * if date found then display on the listview
    * else toast message not found
    * */
    private void poplv(){
        String month;
        String day = String.valueOf(dp.getDayOfMonth());
        if (dp.getDayOfMonth() < 10){day = "0" + day;}
        String year = String.valueOf(dp.getYear());
        switch (dp.getMonth()+1) {
            case 1: month = "Jan"; break;
            case 2: month = "Feb"; break;
            case 3: month = "Mar"; break;
            case 4: month = "Apr"; break;
            case 5: month = "May"; break;
            case 6: month = "Jun"; break;
            case 7: month = "Jul"; break;
            case 8: month = "Aug"; break;
            case 9: month = "Sep"; break;
            case 10: month = "Oct"; break;
            case 11: month = "Nov"; break;
            case 12: month = "Dec";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + dp.getMonth() + 1);
        }
        Cursor data = mydbh.getDateData(day, month, year);
        final ArrayList<String> DataList = new ArrayList<>();
        Parcelable state = lv.onSaveInstanceState();

        /*
        * check database if data is found
        * if found then add to datalist to display on the listview using array adapter method
        * */
        while(data.moveToNext()) {
            DataList.add(data.getString(6));
            ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, DataList);
            lv.setAdapter(adapter);
        }

        /*
        * if data not found show empty list view and display a toast message not found
        * */
        if (data.isBeforeFirst()){
            Toast.makeText(getApplicationContext(), "Not found in database", Toast.LENGTH_SHORT).show();
            final ArrayList<String> EmptyList = new ArrayList<>();
            ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, EmptyList);
            lv.setAdapter(adapter);
        }


        /*
        * when clicked on an entry of a listview, start a new activity which displays data of the
        * corresponding date pressed saved on the database
        * */
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String date = parent.getItemAtPosition(position).toString();

                Intent screen = new Intent(Database.this, ShowData.class);
                screen.putExtra("Data", date);
                startActivity(screen);
            }
        });


        /*
        * Refresh database every one second
        * */
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @SuppressWarnings("unchecked")
                    public void run() {
                        try {
                            Intent intent = getIntent();
                            poplv();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doTask, 1000);

        lv.onRestoreInstanceState(state);
    }
}
