package fr.brubru.myhours.packUtils;

/**
 * Created by Stessy on 07/01/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import fr.brubru.myhours.packModel.Day;
import fr.brubru.myhours.packModel.Month;
import fr.brubru.myhours.packModel.Week;

public class DataBaseHelper extends SQLiteOpenHelper
{
    private final DayComparator myDayComparator;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "myHours.db";
    private static String TABLE;
    private static final String KEY_ID = "id";
    private static final String KEY_DAY = "day";
    private static final String KEY_H1 = "H1";
    private static final String KEY_H2 = "H2";
    private static final String KEY_H3 = "H3";
    private static final String KEY_ID_MONTH = "idMonth";
    private static final String KEY_ID_WEEK = "idWeek";
    private static final String KEY_H4 = "H4";
    private static final String KEY_NAME = "name";
    private static final String KEY_VALUE = "value";
    private static final String KEY_NUM_MONTH = "numMonth";
    private static final String KEY_YEAR = "year";
    private static final String KEY_NUMBER = "number";
    private static final String KEY_NUM_WEEK = "numWeek";

    private static final String CREATE_TABLE_MONTH = "CREATE TABLE "
            + "month" + " ( " + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME
            + " TEXT," + KEY_NUM_MONTH + " INTEGER," + KEY_YEAR + " INTEGER )";

    private static final String CREATE_TABLE_WEEK = "CREATE TABLE "
            + "week" + " ( " + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NUMBER
            + " INTEGER," + KEY_YEAR + " INTEGER, " + KEY_ID_MONTH + " INTEGER )";

    private static final String CREATE_TABLE_DAY = "CREATE TABLE "
            + "day" + " ( " + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_DAY
            + " TEXT," + KEY_H1 + " TEXT," + KEY_H2 + " TEXT,"  + KEY_H3 + " TEXT," + KEY_H4 + " TEXT, "
            + KEY_ID_MONTH + " INTEGER, " + KEY_NUM_WEEK + " INTEGER )";

    private static final String CREATE_TABLE_CONFIG = "CREATE TABLE "
            + "config" + " ( " + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_DAY
            + " TEXT," + KEY_H1 + " TEXT," + KEY_H2 + " TEXT,"  + KEY_H3 + " TEXT," + KEY_H4 + " TEXT )";

    private static final String CREATE_TABLE_SETTINGS = "CREATE TABLE settings ( "
            + KEY_ID + "TEXT, " + KEY_NAME + " TEXT, " + KEY_VALUE + " TEXT )";

    public DataBaseHelper(Context context, String t)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        myDayComparator = new DayComparator();
        TABLE = t;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        try
        {
            db.execSQL(CREATE_TABLE_DAY);
            db.execSQL(CREATE_TABLE_CONFIG);
            db.execSQL(CREATE_TABLE_SETTINGS);
            db.execSQL(CREATE_TABLE_MONTH);
            db.execSQL(CREATE_TABLE_WEEK);
        }
        catch (SQLException ex) { System.out.println("DatabaseHelper onCreate Error : " + ex.getMessage()); }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    public boolean checkSettingExists(String name)
    {
        try
        {
            SQLiteDatabase db = this.getReadableDatabase();
            String selectQuery = "SELECT COUNT(*) FROM " + TABLE + " WHERE " + KEY_NAME + " = '" + name + "'";
            Cursor c = db.rawQuery(selectQuery, null);
            int nb = 0;
            if(c.getCount() >= 1)
            {
                c.moveToFirst();
                nb = (c.getInt(c.getColumnIndex("COUNT(*)")));
            }
            if(c != null) c.close();
            return nb > 0;
        }
        catch (SQLException ex) { System.out.println("DatabaseHelper checkSettingExists Error : " + ex.getMessage()); return false; }
    }

    public long insertSetting(String name, String value)
    {
        long pid = -1;
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, name);
            values.put(KEY_VALUE, value);
            pid = db.insert(TABLE, null, values);
        }
        catch (SQLException ex) { System.out.println("DatabaseHelper insertSetting Error : " + ex.getMessage()); return pid; }
        return pid;
    }

    public String getSetting(String name)
    {
        String value = "";
        try
        {
            SQLiteDatabase db = this.getReadableDatabase();
            String selectQuery = "SELECT value FROM " + TABLE + " WHERE " + KEY_NAME + " = '" + name + "'";
            Cursor c = db.rawQuery(selectQuery, null);
            if(c.getCount() >= 1)
            {
                c.moveToFirst();
                value = c.getString(c.getColumnIndex(KEY_VALUE));
            }
            if(c != null) c.close();
            return value;
        }
        catch (SQLException ex) { System.out.println("DatabaseHelper getSetting Error : " + ex.getMessage()); return null; }
    }

    public long updateSetting(String name, String value)
    {
        long nb = -1;
        try
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_VALUE, value);
            nb = db.update(TABLE, values, KEY_NAME + " = '" + name + "'", null);
        }
        catch (SQLException ex) { System.out.println("DatabaseHelper updateSetting Error : " + ex.getMessage()); return nb; }
        return nb;
    }

    public long insertDay(Day d)
    {
        long pid = -1;
        try
        {
            if(TABLE != "config")
            {
                long idMonth = this.getIdMonthByDay(d.getDay());
                if(idMonth == -1) idMonth = this.insertMonth(d.getDay());
                d.setIdMonth(idMonth);

                long idWeek = this.getIdWeekByDay(d.getDay());
                if(idWeek == -1) this.insertWeek(d);
            }
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_DAY, d.getDayUS());
            values.put(KEY_H1, d.getH1());
            values.put(KEY_H2, d.getH2());
            values.put(KEY_H3, d.getH3());
            values.put(KEY_H4, d.getH4());
            if(TABLE != "config") values.put(KEY_ID_MONTH, d.getIdMonth());
            if(TABLE != "config") values.put(KEY_NUM_WEEK, d.getNumberWeek());
            pid = db.insert(TABLE, null, values);
            d.setId(pid);
        }
        catch (SQLException ex) { System.out.println("DatabaseHelper insertDay Error : " + ex.getMessage()); return pid; }
        return pid;
    }

    public long updateDay(Day d)
    {
        long nb = -1;
        try
        {
            if(TABLE != "config")
            {
                long idMonth = this.getIdMonthByDay(d.getDay());
                if(idMonth == -1) idMonth = this.insertMonth(d.getDay());
                d.setIdMonth(idMonth);

                long idWeek = this.getIdWeekByDay(d.getDay());
                if(idWeek == -1) this.insertWeek(d);
            }
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_DAY, d.getDayUS());
            values.put(KEY_H1, d.getH1());
            values.put(KEY_H2, d.getH2());
            values.put(KEY_H3, d.getH3());
            values.put(KEY_H4, d.getH4());
            if(TABLE != "config") values.put(KEY_ID_MONTH, d.getIdMonth());
            if(TABLE != "config") values.put(KEY_NUM_WEEK, d.getNumberWeek());
            nb = db.update(TABLE, values, KEY_ID + " = " + d.getId(), null);
        }
        catch (SQLException ex) { System.out.println("DatabaseHelper updateDay Error : " + ex.getMessage()); return nb; }
        return nb;
    }

    public boolean checkDayExists(String date)
    {
        try
        {
            SQLiteDatabase db = this.getReadableDatabase();
            String selectQuery = "SELECT COUNT(*) FROM " + TABLE + " WHERE " + KEY_DAY + " = '" + Utils.Format_FR_US(date) + "'";
            Cursor c = db.rawQuery(selectQuery, null);
            int nb = 0;
            if(c.getCount() >= 1)
            {
                c.moveToFirst();
                nb = (c.getInt(c.getColumnIndex("COUNT(*)")));
            }
            if(c != null) c.close();
            return nb > 0;
        }
        catch (SQLException ex) { System.out.println("DatabaseHelper checkDayExists Error : " + ex.getMessage()); return false; }
    }

    public Day getDayByDay(String day)
    {
        Day d = null;
        try
        {
            SQLiteDatabase db = this.getReadableDatabase();
            String selectQuery = "SELECT * FROM " + TABLE + " WHERE " + KEY_DAY + " = '" + Utils.Format_FR_US(day) + "'";
            Cursor c = db.rawQuery(selectQuery, null);
            if(c.getCount() >= 1)
            {
                c.moveToFirst();
                d = new Day();
                d.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                d.setDay(day);
                d.setH1((c.getString(c.getColumnIndex(KEY_H1))));
                d.setH2((c.getString(c.getColumnIndex(KEY_H2))));
                d.setH3((c.getString(c.getColumnIndex(KEY_H3))));
                d.setH4((c.getString(c.getColumnIndex(KEY_H4))));
                if(TABLE != "config") d.setIdMonth(c.getLong(c.getColumnIndex(KEY_ID_MONTH)));
                if(TABLE != "config") d.setNumberWeek(c.getInt(c.getColumnIndex(KEY_NUM_WEEK)));
            }
            if(c != null) c.close();
            return d;
        }
        catch (SQLException ex) { System.out.println("DatabaseHelper getDayByDay Error : " + ex.getMessage()); return null; }
    }

    public List<Day> getDaysByPeriod(String start, String end)
    {
        List<Day> days = new ArrayList<>();
        try
        {
            SQLiteDatabase db = this.getReadableDatabase();
            String selectQuery = "SELECT  * FROM " + TABLE +
                    " WHERE " + KEY_DAY + " >= DATE('" + Utils.Format_FR_US(start) + "')" + " AND " + KEY_DAY + " <= DATE('" + Utils.Format_FR_US(end) + "')";
            Cursor c = db.rawQuery(selectQuery, null);
            if(c.getCount() >= 1)
            {
                c.moveToFirst();
                while(!c.isAfterLast())
                {
                    Day day = new Day();
                    day.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                    day.setDay(Utils.Format_US_FR(c.getString(c.getColumnIndex(KEY_DAY))));
                    day.setH1((c.getString(c.getColumnIndex(KEY_H1))));
                    day.setH2((c.getString(c.getColumnIndex(KEY_H2))));
                    day.setH3((c.getString(c.getColumnIndex(KEY_H3))));
                    day.setH4((c.getString(c.getColumnIndex(KEY_H4))));
                    if(TABLE != "config") day.setIdMonth(c.getLong(c.getColumnIndex(KEY_ID_MONTH)));
                    if(TABLE != "config") day.setNumberWeek(c.getInt(c.getColumnIndex(KEY_NUM_WEEK)));
                    days.add(day);
                    c.moveToNext();
                }
            }
            if(c != null) c.close();
            Collections.sort(days, myDayComparator);
            return days;
        }
        catch (SQLException ex) { System.out.println("DatabaseHelper getDaysByPeriod Error : " + ex.getMessage()); return null; }
    }

    public List<Day> getDaysByNumberWeek(int num)
    {
        List<Day> days = new ArrayList<>();
        try
        {
            SQLiteDatabase db = this.getReadableDatabase();
            String selectQuery = "SELECT  * FROM " + TABLE + " WHERE " + KEY_NUM_WEEK + " = " + num;
            Cursor c = db.rawQuery(selectQuery, null);
            if(c.getCount() >= 1)
            {
                c.moveToFirst();
                while(!c.isAfterLast())
                {
                    Day day = new Day();
                    day.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                    day.setDay(Utils.Format_US_FR(c.getString(c.getColumnIndex(KEY_DAY))));
                    day.setH1((c.getString(c.getColumnIndex(KEY_H1))));
                    day.setH2((c.getString(c.getColumnIndex(KEY_H2))));
                    day.setH3((c.getString(c.getColumnIndex(KEY_H3))));
                    day.setH4((c.getString(c.getColumnIndex(KEY_H4))));
                    if(TABLE != "config") day.setIdMonth(c.getLong(c.getColumnIndex(KEY_ID_MONTH)));
                    if(TABLE != "config") day.setNumberWeek(c.getInt(c.getColumnIndex(KEY_NUM_WEEK)));
                    days.add(day);
                    c.moveToNext();
                }
            }
            if(c != null) c.close();
            Collections.sort(days, myDayComparator);
            return days;
        }
        catch (SQLException ex) { System.out.println("DatabaseHelper getDaysByNumberWeek Error : " + ex.getMessage()); return null; }
    }

    public List<Day> getListDays()
    {
        List<Day> days = new ArrayList<>();
        Day d;
        try
        {
            SQLiteDatabase db = this.getReadableDatabase();
            String selectQuery = "SELECT  * FROM " + TABLE;
            Cursor c = db.rawQuery(selectQuery, null);
            if(c.getCount() >= 1)
            {
                c.moveToFirst();
                while(!c.isAfterLast())
                {
                    d = new Day();
                    d.setId(c.getLong(c.getColumnIndex(KEY_ID)));
                    d.setDay(Utils.Format_US_FR(c.getString(c.getColumnIndex(KEY_DAY))));
                    d.setH1(c.getString(c.getColumnIndex(KEY_H1)));
                    d.setH2(c.getString(c.getColumnIndex(KEY_H2)));
                    d.setH3(c.getString(c.getColumnIndex(KEY_H3)));
                    d.setH4(c.getString(c.getColumnIndex(KEY_H4)));
                    if(TABLE != "config") d.setNumberWeek(c.getInt(c.getColumnIndex(KEY_NUM_WEEK)));
                    if(TABLE != "config") d.setIdMonth(c.getLong(c.getColumnIndex(KEY_ID_MONTH)));
                    days.add(d);
                    c.moveToNext();
                }
            }
            if(c != null) c.close();
            Collections.sort(days, myDayComparator);
            return days;
        }
        catch (SQLException ex) { System.out.println("DatabaseHelper getList Error : " + ex.getMessage()); return null; }
    }

    public long insertWeek(Day d)
    {
        long pid = -1;
        try
        {
            final String myFormat = "dd/MM/yyyy";
            final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
            try
            {
                Date myDate = sdf.parse(d.getDay());
                String numWeekString  = new SimpleDateFormat("w").format(myDate);
                int numWeek = 0;
                try
                {
                    numWeek = Integer.parseInt(numWeekString);
                }
                catch(Exception e) { System.out.println("insertWeek parse 1 error " + e.getMessage()); }
                String year = (String) android.text.format.DateFormat.format("yyyy", myDate); //2015
                Week w = new Week();
                w.setNumber(numWeek);
                w.setYear(Integer.parseInt(year));
                w.setIdMonth(d.getIdMonth());
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(KEY_NUMBER, w.getNumber());
                values.put(KEY_YEAR, w.getYear());
                values.put(KEY_ID_MONTH, w.getIdMonth());
                pid = db.insert("week", null, values);
            }
            catch (ParseException e) { System.out.println("insertWeek parse 2 error : " + e.getMessage()); }
        }
        catch (SQLException ex) { System.out.println("DatabaseHelper insertWeek Error : " + ex.getMessage()); return pid; }
        return pid;
    }

    public long getIdWeekByDay(String day)
    {
        long idWeek = -1;
        try
        {
            final String myFormat = "dd/MM/yyyy";
            final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
            try
            {
                Date myDate = sdf.parse(day);
                String year = (String) android.text.format.DateFormat.format("yyyy", myDate); //2015
                String numWeekString  = new SimpleDateFormat("w").format(myDate);
                int numWeek = 0;
                try
                {
                    numWeek = Integer.parseInt(numWeekString);
                }
                catch(Exception e) { System.out.println("getIdWeekByDay parse 1 error " + e.getMessage()); }
                SQLiteDatabase db = this.getReadableDatabase();
                String selectQuery = "SELECT " + KEY_ID + " FROM week WHERE " + KEY_NUMBER + " = " + numWeek + " AND " + KEY_YEAR + " = " + Integer.parseInt(year);
                Cursor c = db.rawQuery(selectQuery, null);
                if(c.getCount() >= 1)
                {
                    c.moveToFirst();
                    idWeek = c.getInt(c.getColumnIndex(KEY_ID));
                }
                if(c != null) c.close();
                return idWeek;
            }
            catch (ParseException e) { System.out.println("getIdWeekByDay parse 2 error : " + e.getMessage()); return idWeek; }
        }
        catch (SQLException ex) { System.out.println("DatabaseHelper getIdWeekByDay Error : " + ex.getMessage()); return idWeek; }
    }

    public List<Week> getWeeksByIdMonth(long id)
    {
        List<Week> weeks = new ArrayList<>();
        Week w;
        try
        {
            SQLiteDatabase db = this.getReadableDatabase();
            String selectQuery = "SELECT * FROM week WHERE " + KEY_ID_MONTH + " = " + id;
            Cursor c = db.rawQuery(selectQuery, null);
            if(c.getCount() >= 1)
            {
                c.moveToFirst();
                while(!c.isAfterLast())
                {
                    w = new Week();
                    long idWeek = c.getLong(c.getColumnIndex(KEY_ID));
                    w.setId(idWeek);
                    int numWeek = c.getInt(c.getColumnIndex(KEY_NUMBER));
                    w.setNumber(numWeek);
                    w.setIdMonth(c.getInt(c.getColumnIndex(KEY_ID_MONTH)));
                    w.setYear(c.getInt(c.getColumnIndex(KEY_YEAR)));
                    TABLE = "day";
                    w.setMyDays(this.getDaysByNumberWeek(numWeek));
                    weeks.add(w);
                    c.moveToNext();
                }
            }
            if (c != null) c.close();
            // TODO sort
            //Collections.sort(months, myMonthComparator);
            return weeks;
        }
        catch (SQLException ex) { System.out.println("DatabaseHelper getWeeksByIdMonth Error : " + ex.getMessage()); return weeks; }
    }

    public long insertMonth(String date)
    {
        long pid = -1;
        try
        {
            final String myFormat = "dd/MM/yyyy";
            final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
            try
            {
                Date myDate = sdf.parse(date);
                String stringMonth = (String) android.text.format.DateFormat.format("MMMM", myDate); //Jun
                String intMonth = (String) android.text.format.DateFormat.format("MM", myDate); //06
                String year = (String) android.text.format.DateFormat.format("yyyy", myDate); //2013
                Month m = new Month();
                m.setName(stringMonth);
                m.setNumMonth(Integer.parseInt(intMonth));
                m.setYear(Integer.parseInt(year));
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(KEY_NAME, m.getName());
                values.put(KEY_NUM_MONTH, m.getNumMonth());
                values.put(KEY_YEAR, m.getYear());
                pid = db.insert("month", null, values);
            }
            catch (ParseException e) { System.out.println("insertMonth parse error : " + e.getMessage()); }
        }
        catch (SQLException ex) { System.out.println("DatabaseHelper insertMonth Error : " + ex.getMessage()); return pid; }
        return pid;
    }

    public long getIdMonthByDay(String day)
    {
        long idMonth = -1;
        try
        {
            final String myFormat = "dd/MM/yyyy";
            final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
            try
            {
                Date myDate = sdf.parse(day);
                String intMonth = (String) android.text.format.DateFormat.format("MM", myDate); //01
                String year = (String) android.text.format.DateFormat.format("yyyy", myDate); //2015
                SQLiteDatabase db = this.getReadableDatabase();
                String selectQuery = "SELECT " + KEY_ID + " FROM month WHERE " + KEY_NUM_MONTH + " = " + Integer.parseInt(intMonth) + " AND " + KEY_YEAR + " = " + Integer.parseInt(year);
                Cursor c = db.rawQuery(selectQuery, null);
                if(c.getCount() >= 1)
                {
                    c.moveToFirst();
                    idMonth = c.getInt(c.getColumnIndex(KEY_ID));
                }
                if(c != null) c.close();
                return idMonth;
            }
            catch (ParseException e) { System.out.println("insertMonth parse error : " + e.getMessage()); return idMonth; }
        }
        catch (SQLException ex) { System.out.println("DatabaseHelper insertMonth Error : " + ex.getMessage()); return idMonth; }
    }

    public List<Month> getListByMonth()
    {
        List<Month> months = new ArrayList<>();
        Month m;
        try
        {
            SQLiteDatabase db = this.getReadableDatabase();
            String selectQuery = "SELECT  * FROM " + TABLE + " WHERE " + KEY_YEAR + " != 10007";
            Cursor c = db.rawQuery(selectQuery, null);
            if(c.getCount() >= 1)
            {
                c.moveToFirst();
                while(!c.isAfterLast())
                {
                    m = new Month();
                    long idMonth = c.getLong(c.getColumnIndex(KEY_ID));
                    m.setId(idMonth);
                    m.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                    m.setNumMonth(c.getInt(c.getColumnIndex(KEY_NUM_MONTH)));
                    m.setYear(c.getInt(c.getColumnIndex(KEY_YEAR)));
                    TABLE = "day";
                    m.setMyWeeks(this.getWeeksByIdMonth(idMonth));
                    months.add(m);
                    c.moveToNext();
                }
            }
            if(c != null) c.close();
            // TODO sort
            //Collections.sort(months, myMonthComparator);
            return months;
        }
        catch (SQLException ex) { System.out.println("DatabaseHelper getListByMonth Error : " + ex.getMessage()); return null; }
    }


    public long getMaxId()
    {
        long max = 0;
        try
        {
            SQLiteDatabase db = this.getReadableDatabase();
            String selectQuery = "SELECT  MAX("+KEY_ID+") FROM " + TABLE;
            Cursor c = db.rawQuery(selectQuery, null);
            if(c.getCount() >= 1)
            {
                c.moveToFirst();
                max = c.getInt(c.getColumnIndex("MAX("+KEY_ID+")"));
            }
            return max;
        }
        catch (SQLException ex) { System.out.println("DatabaseHelper getMaxId Error : " + ex.getMessage()); return max; }
    }

    public void closeDB() throws SQLException
    {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen()) db.close();
    }

    public boolean deleteDay(long id)
    {
        try
        {
            SQLiteDatabase db = this.getReadableDatabase();
            int nb = db.delete(TABLE, KEY_ID + " = " + id, null);
            // TODO delete week and month
            return nb == 1;
        }
        catch (SQLException ex) { System.out.println("DatabaseHelper deleteDay Error : " + ex.getMessage()); return false; }
    }

    public boolean dropDay()
    {
        try
        {
            SQLiteDatabase db = this.getReadableDatabase();
            db.execSQL("DROP TABLE IF EXISTS " + TABLE);
            db.execSQL(CREATE_TABLE_DAY);
            db.execSQL("DROP TABLE IF EXISTS month");
            db.execSQL(CREATE_TABLE_MONTH);
            db.execSQL("DROP TABLE IF EXISTS week");
            db.execSQL(CREATE_TABLE_WEEK);
            return true;
        }
        catch (SQLException ex) { System.out.println("DatabaseHelper dropDay Error : " + ex.getMessage()); return false; }
    }

    public class DayComparator implements Comparator<Day>
    {
        @Override
        public int compare(Day d1, Day d2)
        {
            if(d1.getDateDay() == null || d2.getDateDay() == null) return 0;
            return d1.getDateDay().compareTo(d2.getDateDay());
        }
    }
}
