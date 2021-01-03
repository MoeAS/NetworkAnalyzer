package com.example.project;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

public class Compare extends Activity {

    DatabaseHelper mydbh;
    private static final String TAG = "Database";
    private TextView DisplayDate;
    private TextView DisplayDate2;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private DatePickerDialog.OnDateSetListener mDateSetListener2;
    private TextView DisplayTime;
    private TextView DisplayTime2;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener2;
    private Button btnComp;
    private Button btnDelEntries;
    public String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compare);
        DisplayDate = (TextView) findViewById(R.id.editTextDate);
        DisplayDate2 = (TextView) findViewById(R.id.editTextDate3);
        DisplayTime = (TextView) findViewById(R.id.editTextDate2);
        DisplayTime2 = (TextView) findViewById(R.id.editTextDate4);
        btnComp = (Button) findViewById(R.id.button);
        btnDelEntries = (Button) findViewById(R.id.button2);

        mydbh = new DatabaseHelper(this);



        /*
         * Display Date for Start
         * */
        DisplayDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(Compare.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Calendar c1 = Calendar.getInstance();
                String day = String.valueOf(dayOfMonth);
                String Year = String.valueOf(year);
                String Month = months[month];
                if (dayOfMonth < 10){day = "0" + day;}
                String date = Month + " " + day + " " + Year;
                DisplayDate.setText(date);
            }
        };


        /*
         * Display Date for End
         * */
        DisplayDate2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(Compare.this,
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        mDateSetListener2,
                        year, month, day);
                dialog.show();
            }
        });

        mDateSetListener2 = new DatePickerDialog.OnDateSetListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Calendar c1 = Calendar.getInstance();
                String day = String.valueOf(dayOfMonth);
                String Year = String.valueOf(year);
                String Month = months[month];
                if (dayOfMonth < 10){day = "0" + day;}
                String date = Month + " " + day + " " + Year;
                DisplayDate2.setText(date);
            }
        };



        /*
         * Display time for Start
         * */
        DisplayTime.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog dialog = new TimePickerDialog(Compare.this,
                        AlertDialog.THEME_HOLO_DARK,
                        mTimeSetListener,
                        hour, minute,
                        DateFormat.is24HourFormat(Compare.this));

                dialog.show();
            }
        });
        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                String hour = String.valueOf(hourOfDay);
                if (hourOfDay < 10){hour = "0" + hour;}
                String min = String.valueOf(minute);
                if (minute < 10){min = "0" + min;}
                String time = hour + ":" + min;
                DisplayTime.setText(time);
            }

        };


        /*
         * Display time for End
         * */
        DisplayTime2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog dialog = new TimePickerDialog(Compare.this,
                        AlertDialog.THEME_HOLO_DARK,
                        mTimeSetListener2,
                        hour, minute,
                        DateFormat.is24HourFormat(Compare.this));
                dialog.show();
            }
        });
        mTimeSetListener2 = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                String hour = String.valueOf(hourOfDay);
                if (hourOfDay < 10){hour = "0" + hour;}
                String min = String.valueOf(minute);
                if (minute < 10){min = "0" + min;}
                String time = hour + ":" + min;
                DisplayTime2.setText(time);
            }

        };

        /*
        * When compare button is pressed it gets the Date and Time from the database and checks for existence
        * if Date and Time exist then compare and compute Averages of SignalStrength, SINR, Opeartor, NetworkType
        * else prompt error toast message
        * */
        btnComp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((DisplayDate.getText().toString().length() == 0) || (DisplayDate2.getText().toString().length() == 0) || (DisplayTime.getText().toString().length() == 0) || (DisplayTime2.getText().toString().length() == 0)){
                    Toast.makeText(getApplicationContext(), "Invalid", Toast.LENGTH_SHORT).show();
                }
                else {
                    int ID1 = getID(DisplayDate.getText().toString().substring(0, 3), DisplayDate.getText().toString().substring(4, 6), DisplayDate.getText().toString().substring(7, 11), DisplayTime.getText().toString().substring(0, 2), DisplayTime.getText().toString().substring(3, 5));
                    int ID2 = getID(DisplayDate2.getText().toString().substring(0, 3), DisplayDate2.getText().toString().substring(4, 6), DisplayDate2.getText().toString().substring(7, 11), DisplayTime2.getText().toString().substring(0, 2), DisplayTime2.getText().toString().substring(3, 5));
                Intent screen = new Intent(Compare.this, ListViewCompare.class);
                screen.putExtra("ID1", ID1);
                screen.putExtra("ID2", ID2);
                if((ID1==0)){Toast.makeText(getApplicationContext(), "Invalid Start Date or Time, No records found in the database", Toast.LENGTH_SHORT).show();}
                else if((ID2==0)){Toast.makeText(getApplicationContext(), "Invalid End Date or Time, No records found in the database", Toast.LENGTH_SHORT).show();}
                else if((ID2 < ID1)){Toast.makeText(getApplicationContext(), "Please place the Start & End accordingly", Toast.LENGTH_SHORT).show();}
                else{startActivity(screen);}}
            }
        });


        /*
         * When delete button is pressed it gets the Date and Time from the database and checks for existence
         * if Date and Time exist then delete entries from Start Date and Time till End Date and Time (everything in between)
         * */
        btnDelEntries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((DisplayDate.getText().toString().length() == 0) || (DisplayDate2.getText().toString().length() == 0) || (DisplayTime.getText().toString().length() == 0) || (DisplayTime2.getText().toString().length() == 0)){
                    Toast.makeText(getApplicationContext(), "Invalid", Toast.LENGTH_SHORT).show();
                }
                else {
                final int ID1 = getID(DisplayDate.getText().toString().substring(0, 3), DisplayDate.getText().toString().substring(4, 6),DisplayDate.getText().toString().substring(7, 11), DisplayTime.getText().toString().substring(0, 2), DisplayTime.getText().toString().substring(3, 5));
                final int ID2 = getID(DisplayDate2.getText().toString().substring(0, 3), DisplayDate2.getText().toString().substring(4, 6),DisplayDate2.getText().toString().substring(7, 11), DisplayTime2.getText().toString().substring(0, 2), DisplayTime2.getText().toString().substring(3, 5));
                AlertDialog.Builder builder = new AlertDialog.Builder(Compare.this);
                builder.setCancelable(true);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure you want to delete?");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mydbh.DeleteMultipleEntries(ID1, ID2);
                                if ((ID2 >= ID1)){
                                Toast.makeText(getApplicationContext(), "Deleted entries from the database", Toast.LENGTH_SHORT).show();}
                                else {Toast.makeText(getApplicationContext(), "Error: Cannot delete entries from the database", Toast.LENGTH_SHORT).show();}
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }}
        });

    }


    /*
    * get ID of corresponding Date and Time from the database in order to allow for multiple
    * Date and Time to be computed rather than two only
    * */
    public int getID(String month, String day, String year, String hour, String minute) {
        Cursor data = mydbh.getDateandTimeDataID(day, month, year, hour, minute);
        int ID = 0;
        while (data.moveToNext()) {
            ID = data.getInt(0);
        }
        return ID;
    }

}