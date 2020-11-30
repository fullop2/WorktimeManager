package com.android.worktimemanager.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



public class DBHelper extends SQLiteOpenHelper {

    public static int DBVERSION = 2;
    static String lastOperator;
    static String lastTable;
    static DAO lastObject;
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory, DBVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE HOLIDAY (WORKDAY DAY PRIMARY KEY, COMMENT TEXT )");
            db.execSQL("CREATE TABLE EXTRAWORK (WORKDAY DAY PRIMARY KEY, WORKTIME INTEGER, WORKTYPE TEXT CHECK( WORKTYPE IN ('특근','연장') ) )");
            db.execSQL("CREATE TABLE DAYOFF (WORKDAY DAY PRIMARY KEY, OFFTIME INTEGER, COMMENT TEXT)");
            db.execSQL("CREATE TABLE TOTALDAYOFF(YEAR INTEGER PRIMARY KEY, DAYOFFS INTEGER)");

            db.setLocale(Locale.KOREA);
        }
        catch(SQLiteException e)
        {
            System.err.println(e.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("CREATE TABLE TOTALDAYOFF(YEAR INTEGER PRIMARY KEY, DAYOFFS INTEGER)");
    }

    private void saveLastQuery(String tableName, String operator, DAO object)
    {
        this.lastTable = tableName;
        this.lastOperator = operator;
        this.lastObject = object;
    }

    public final String getLastTable() {return lastTable;}
    public final String getLastOperator() {return lastOperator;}
    public final DAO getLastDAO() {return lastObject;}
    public Boolean undoLastQuery()
    {
        SQLiteDatabase db =getWritableDatabase();
        String query;
        if(lastTable == null)
        {
            return false;
        }
        if(lastOperator.equals("INSERT"))
        {
            query = "DELETE FROM " + lastTable + " WHERE WORKDAY = '"+ lastObject.getDate() + "'";
            lastOperator = "DELETE";
        }
        else
        {
            query = "INSERT INTO " + lastTable + " VALUES ("+lastObject.toSql() + ")";
            lastOperator = "INSERT";
        }
        db.execSQL(query);
        return true;
    }
    public Boolean add(Schedule schedule)
    {
        SQLiteDatabase db = getWritableDatabase();
        String tableName;
        if(schedule.getComment().equals("특근") || schedule.getComment().equals("연장"))
            tableName = "EXTRAWORK";
        else
            tableName = "DAYOFF";
        try {
            db.execSQL("INSERT INTO " + tableName + " VALUES (" + schedule.toSql() + ")");
        }
        catch(Exception e)
        {
            return false;

        }
        saveLastQuery(tableName,"INSERT",schedule);
        return true;
    }
    public Boolean remove(DAO dao)
    {
        if( dao instanceof Schedule)
        {
            return remove((Schedule)dao);
        }
        else
        {
            return remove((Holiday)dao);
        }
    }
    private Boolean remove(Schedule schedule)
    {
        SQLiteDatabase db = getWritableDatabase();
        String tableName;
        if(schedule.getComment().equals("특근") || schedule.getComment().equals("연장"))
            tableName = "EXTRAWORK";
        else
            tableName = "DAYOFF";
        try {
            db.execSQL("DELETE FROM " + tableName + " WHERE WORKDAY = '" + schedule.getDate() + "'");
        }
        catch(Exception e)
        {
            return false;
        }
        saveLastQuery(tableName,"DELETE",schedule);
        return true;
    }

    public List getSchedule(LocalDate date, String tableName, List scheduleList, String type)
    {
        Cursor cursor = getScheduleCursor(date,tableName,type);

        if(scheduleList == null)
            scheduleList = new ArrayList();
        else
            scheduleList.clear();
        Schedule day;
        while(cursor.moveToNext())
        {
            day = new Schedule(
                    cursor.getString(0),
                    (double)cursor.getInt(1)/1000.0,
                    cursor.getString(2)
            );
            scheduleList.add(day);
        }
        return scheduleList;
    }
    private Cursor getScheduleCursor(LocalDate date, String tableName, String type)
    {
        SQLiteDatabase db = getReadableDatabase();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM "+tableName);

        if(type != null)
        {
            String fromDay = date.format(DateTimeFormatter.ofPattern("yyyy-MM"))+"-16";
            date =  date.plusMonths(1);
            String toDay = date.format(DateTimeFormatter.ofPattern("yyyy-MM"))+"-16";
            sql.append(" WHERE WORKDAY >= '"+fromDay+"' AND WORKDAY < '"+toDay+ "' AND WORKTYPE = '" + type + "'");
        }
        else
        {
            String fromDay = date.getYear()+"-01-01";
            String toDay= (date.getYear()+1)+"-01-01";
            sql.append(" WHERE WORKDAY >= '"+fromDay+"' AND WORKDAY < '"+toDay+ "'");
        }
        sql.append(" ORDER BY WORKDAY DESC");
        Cursor cursor = db.rawQuery(sql.toString(),null);
        return cursor;
    }

    public Boolean add(Holiday holiday)
    {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.execSQL("INSERT INTO HOLIDAY VALUES (" + holiday.toSql() + ")");
        }
        catch(Exception e)
        {
            return false;
        }
        saveLastQuery("HOLIDAY","INSERT",holiday);
        return true;
    }
    private Boolean remove(Holiday holiday)
    {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.execSQL("DELETE FROM HOLIDAY WHERE WORKDAY = '" + holiday.getDate() + "'");
        }
        catch(Exception e)
        {
            return false;
        }
        saveLastQuery("HOLIDAY","DELETE",holiday);
        return true;
    }

    public List getHoliday(LocalDate date, List holidayList)
    {
        Cursor cursor = getHolidayCursor(date);

        if(holidayList == null)
            holidayList = new ArrayList();
        else
            holidayList.clear();
        Holiday day;
        while(cursor.moveToNext())
        {
            day = new Holiday(
                    cursor.getString(0),
                    cursor.getString(1)
            );
            holidayList.add(day);
        }
        return holidayList;
    }

    private Cursor getHolidayCursor(LocalDate date)
    {
        SQLiteDatabase db = getReadableDatabase();

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM HOLIDAY");

        String fromDay = date.format(DateTimeFormatter.ofPattern("yyyy-MM"))+"-16";
        date =  date.plusMonths(1);
        String toDay = date.format(DateTimeFormatter.ofPattern("yyyy-MM"))+"-16";
        sql.append(" WHERE WORKDAY >= '"+fromDay+"' AND WORKDAY < '"+toDay+ "'");

        sql.append(" ORDER BY WORKDAY DESC");
        Cursor cursor = db.rawQuery(sql.toString(),null);
        return cursor;
    }

    public double getTotalDayoff(int year)
    {
        Cursor cursor = getTotalDayoffCursor(year);

        if(cursor.getCount() == 0)
            return 0;
        else
        {
            cursor.moveToNext();
            return ((double)cursor.getInt(0))/(double)10;
        }
    }
    private Cursor getTotalDayoffCursor(int year)
    {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT DAYOFFS FROM TOTALDAYOFF WHERE YEAR = "+ year;

        Cursor cursor = db.rawQuery(sql,null);
        return cursor;
    }
    public Boolean setTotalDayoff(int year, double totalDayoff)
    {
        SQLiteDatabase db = getWritableDatabase();
        try
        {
            db.execSQL("INSERT INTO TOTALDAYOFF VALUES (" + year + ", "+ totalDayoff*10+ ")" );
        }
        catch(SQLiteException e)
        {
            db.execSQL("UPDATE TOTALDAYOFF SET DAYOFFS = "+totalDayoff*10+" WHERE YEAR = "+year );
        }
        return true;
    }
}
