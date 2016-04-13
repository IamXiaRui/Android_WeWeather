package xr.weweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库帮助类对象
 */
public class CityListOpenHelper extends SQLiteOpenHelper {

    public CityListOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                              int version) {
        super(context, "cityList.db", null, 1);
    }

    //建表
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table Province (_id integer primary key autoincrement,province_name text,province_code text)");
        db.execSQL("create table City (_id integer primary key autoincrement,city_name text,city_code text,province_id integer)");
        db.execSQL("create table County (_id integer primary key autoincrement,county_name text,county_code text,city_id integer,weather_code text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
