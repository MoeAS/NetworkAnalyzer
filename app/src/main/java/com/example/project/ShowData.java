package com.example.project;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ShowData extends Activity {
    private ListView data;
    private Button btnDel;


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showdata);
        data = (ListView) findViewById(R.id.listView1);
        btnDel = (Button) findViewById(R.id.btnDelete);

        final DatabaseHelper mydbh = new DatabaseHelper(this);

        /*
        * creates a new page which when pressed on a particular listview it shows all of the
        * data in the corresponding date in the database
        * */
        Intent intent = getIntent();
        final String Data = intent.getStringExtra("Data");

        ArrayList<String> DataList1 = new ArrayList<>();
        Cursor data1 = mydbh.getmoreData(Data);

        /*
        * if data found, add to a list and display on the listview using array adapter
        * */
        while (data1.moveToNext()){
            for (int j =0;j<data1.getColumnCount();j++)
            {
                DataList1.add(data1.getColumnName(j) + ": " + data1.getString(j));
            }
        }

        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, DataList1);
        data.setAdapter(adapter);


        /*
        * Delete provided Date and Listview corresponding to the data in the database
        * */
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowData.this);
                builder.setCancelable(true);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure you want to delete?");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mydbh.DeleteData(Data);
                                Toast.makeText(getApplicationContext(), "Deleting from the database", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

}
