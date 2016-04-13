package xr.weweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import xr.weweather.bean.WeatherBean;

/**
 * 城市列表数据库的操作
 */
public class WeatherDatabase {

    private static WeatherOpenHelper weatherOpenHelper;
    private static WeatherDatabase weatherDB;

    //实例化帮助类对象
    public WeatherDatabase(Context context) {
        weatherOpenHelper = new WeatherOpenHelper(context, "weatherInfo.db", null, 1);
    }

    //初始化DataBase
    public synchronized static WeatherDatabase getInstance(Context context) {
        if (weatherDB == null) {
            weatherDB = new WeatherDatabase(context);
        }
        return weatherDB;
    }

    //保存省级数据
    public void saveWeather(WeatherBean mWeatehr) {
        SQLiteDatabase db = weatherOpenHelper.getWritableDatabase();
        if (mWeatehr != null) {
            ContentValues values = new ContentValues();
            values.put("county_name", mWeatehr.getLocation());
            values.put("weather_code", mWeatehr.getWeatherCode());
            values.put("weather", mWeatehr.getWeather());
            values.put("temp", mWeatehr.getTemperature());
            values.put("time", mWeatehr.getTime());
            db.insert("weatherCache", null, values);
        }
    }

    //查询当前天气数据库中地区的天气代码
    public ArrayList<WeatherBean> getOldWeather() {
        ArrayList<WeatherBean> list = new ArrayList<WeatherBean>();
        SQLiteDatabase db = weatherOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("weatherCache", null, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                WeatherBean mWeather = new WeatherBean();
                mWeather.setLocation(cursor.getString(1));
                mWeather.setWeatherCode(cursor.getString(2));
                mWeather.setWeather(cursor.getString(3));
                mWeather.setTemperature(cursor.getString(4));
                mWeather.setTime(cursor.getString(5));

                list.add(mWeather);
            }
        }
        return list;
    }


    //查询当前天气数据库中地区的天气代码
    public String getWeatherCode() {
        SQLiteDatabase db = weatherOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("weatherCache", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(2);
        }
        return null;
    }


    //判断数据库是否存在天气缓存
    public boolean DBIsExist() {
        SQLiteDatabase db = weatherOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("weatherCache", null, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            return true;
        }
        return false;
    }

}
