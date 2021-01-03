package com.example.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PhoneData";
    private static final String TABLE_NAME = "Data";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    /*
    * Creates the database on the start of the app if no database exists
    * Database Name: PhoneData
    * Table Name: Data
    * Entries of table
    * ID Integer
    * Operator String
    * NetworkType String
    * SignalStrength Integer
    * SINR Integer
    * CellID Integer
    * TimeStamp Date
    * FrequencyBand Integer
    * */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String table = "CREATE TABLE IF NOT EXISTS Data (ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, Operator STRING, NetworkType STRING, SignalStrength INTEGER, SINR INTEGER, CellID INTEGER, TimeStamp DATE, FrequencyBand INTEGER)";
        db.execSQL(table);
    }


    /*
    * If Database version is different, the table should be remade in case of any future error
    * */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN Operator STRING");
            case 2:
                db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN NetworkType STRING");
            case 3:
                db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN SignalStrength INTEGER");
            case 4:
                db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN SINR INTEGER");
            case 5:
                db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN CellID INTEGER");
            case 6:
                db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN TimeStamp DATE");
            case 7:
                db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN FrequencyBand INTEGER");
            default: db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        }
    }


    /*
    * SQL syntax querying to add data to the database
    * */
    public void addData(String oper, String nt, int ss, int sinr, int cid, Date ts, String Band) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "INSERT INTO Data (Operator, NetworkType, SignalStrength, SINR, CellID, TimeStamp, FrequencyBand)" + " VALUES " + "('" + oper + "'" + ", " + "'" + nt + "'" + ", " + ss + ", " + sinr + ", " + cid + ", " + "'" + ts +"', '" + Band + "'" + ") ";
        db.execSQL(query);

    }

    /*
     * SQL syntax querying to get all of the data from the database
     * */
    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }


    /*
     * SQL syntax querying to get all of the data from the database of a specific TimeStamp or Date
     * @param String TimeStamp
     * */
    public Cursor getmoreData (String TimeStamp){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_NAME +" WHERE TimeStamp = '" + TimeStamp + "'";
        Cursor data1 = db.rawQuery(query, null);
        return data1;
    }


    /*
     * SQL syntax querying to delete row of data from the database of a specific TimeStamp or Date
     * @param String TimeStamp
     * */
    public void DeleteData (String TimeStamp){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM "+TABLE_NAME +" WHERE TimeStamp = '" + TimeStamp + "'";
        db.execSQL(query);
    }

    /*
     * SQL syntax querying to get all of the data from the database where the Date the user picks matches
     * the Date in the database using LIKE method
     * @params: Strings day, month, year
     * */
    public Cursor getDateData(String day, String month, String year){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_NAME+" WHERE TimeStamp LIKE '%" + month + "%' AND TimeStamp LIKE '% " + day + " %' AND TimeStamp LIKE '%" + year + "%'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }


    /*
     * SQL syntax querying to get all of the data from the database with specific Date and Time the user
     * want to pick and matches in the database
     * @params: Strings day, month, year, hour, minute
     * */
    public Cursor getDateandTimeDataID(String day, String month, String year, String hour, String minute){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT ID FROM "+TABLE_NAME+" WHERE TimeStamp LIKE '%" + month + "%' AND TimeStamp LIKE '% " + day + " %' AND TimeStamp LIKE '%" + year + "%' AND TimeStamp LIKE '% " + hour + ":" + minute + "%'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }


    /*
     * SQL syntax querying to get all of the data from the database where the ID is between two IDs
     * to get everything between both IDs
     * @params: integers ID1, ID2
     * */
    public Cursor getDateandTimeData(int ID1, int ID2){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+TABLE_NAME+" WHERE ID BETWEEN " + ID1 + " AND " + ID2;
        Cursor data = db.rawQuery(query, null);
        return data;
    }


    /*
     * SQL syntax querying to delete all of the rows whose data from the database is between the two
     * IDs given
     *
     * @params: Integers ID1, ID2
     * */
    public void DeleteMultipleEntries (int ID1, int ID2){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM "+TABLE_NAME +" WHERE ID BETWEEN " + ID1 + " AND " + ID2;
        db.execSQL(query);
    }
}
