package xr.weweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库帮助类对象
 */
public class WeatherOpenHelper extends SQLiteOpenHelper {

    public WeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                             int version) {
        super(context, "weatherInfo.db", null, 1);
    }

    //建表
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table weatherCache (_id integer primary key autoincrement,county_name text,weather_code text,weather text,temp text,time text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
