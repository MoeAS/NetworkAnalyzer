package com.example.project;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ListViewCompare extends Activity {

    private ListView list;
    private int ID1;
    private int ID2;
    DatabaseHelper mydbh;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listviewcompare);
        list = (ListView) findViewById(R.id.listView);

        mydbh = new DatabaseHelper(this);

        /*
        * display a new page of a listview listing all of the components needed for average
        * */
        Intent intent = getIntent();
        ID1 = intent.getIntExtra("ID1", -1);
        ID2 = intent.getIntExtra("ID2", -1);

        compareData(ID1, ID2);
    }


    public void compareData(int ID1, int ID2){

        /*
         * Calculate avg network type
         * get Data from the database of the specified column index and computes using counters
         * */
        Cursor data = mydbh.getDateandTimeData(ID1, ID2);
        int i = 0;
        int j = 0;
        int k = 0;
        while(data.moveToNext()) {
            String x = data.getString(2);
            if (x.equals("LTE")){i++;}
            else if ((x.equals("GSM")) || (x.equals("EDGE")) || (x.equals("GPRS"))){j++;}
            else if (x.equals("UNKNOWN")){}
            else {k++;}
        }

        double total = i+j+k;
        double LTE = (i/total) * 100;
        double GSM = (j/total) * 100;
        double HSPA = (k/total) * 100;
        System.out.println(total + " " + i + " " + j + " " + k + " % " + LTE + " " + GSM + " " + HSPA + " " + (100/5));

        /*
         * Calculate avg network operator
         * */
        Cursor data2 = mydbh.getDateandTimeData(ID1, ID2);
        int i1 = 0;
        int j1 = 0;
        int k1 = 0;
        while(data2.moveToNext()) {
            String operator = data2.getString(1);
            if (operator.equals("touch")) {i1++;}
            else if (operator.equals("alfa")) {j1++;}
            else {k1++;}
        }

        double total4 = i1 + j1 + k1;
        double touch = (i1/total4) * 100;
        double alfa = (j1/total4) * 100;
        double other = (k1/total4) * 100;


        /*
         * Calculate avg Signal Strength
         * */
        Cursor data3 = mydbh.getDateandTimeData(ID1, ID2);
        int y = 0;
        int total2 = 0;
        while(data3.moveToNext()) {
            int y1 = data3.getInt(3);
            total2++;
            y+=y1;
        }

        int avgSS = y/total2;

        /*
         * Calculate avg SINR
         * */
        Cursor data4 = mydbh.getDateandTimeData(ID1, ID2);
        int y2 = 0;
        int total3 = 0;
        while(data4.moveToNext()) {
            int y1 = data4.getInt(4);
            total3++;
            y2+=y1;
        }

        int avgSINR = y2/total3;

        String text1 = "Avg Network Type: ";
        String text2 = "LTE = " + LTE + "%   " + "GSM = " + GSM + "%   " + "3G = " + HSPA + "%";
        String text5 = "Avg Operator Type: ";
        String text6 = "touch= " + touch + "%  " + "alfa= " + alfa + "%  " + "Other= " + other +"%";
        String text3 = "Avg Signal Strength: " + avgSS;
        String text4 = "Avg SINR: " + avgSINR;

        ArrayList<String> DataList = new ArrayList<>();

        /*
        * Add all of the data to display on a listview
        * */
        DataList.add(text1);
        DataList.add(text2);
        DataList.add(text5);
        DataList.add(text6);
        DataList.add(text3);
        DataList.add(text4);

        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, DataList);
        list.setAdapter(adapter);

    }
}
