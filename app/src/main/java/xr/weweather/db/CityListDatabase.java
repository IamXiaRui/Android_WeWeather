package xr.weweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import xr.weweather.bean.CityBean;
import xr.weweather.bean.CountyBean;
import xr.weweather.bean.FixedConstants;
import xr.weweather.bean.ProvinceBean;

/**
 * 城市列表数据库的操作
 */
public class CityListDatabase {

    private static CityListOpenHelper cityListOpenHelper;
    private static CityListDatabase cityListDB;

    //实例化帮助类对象
    public CityListDatabase(Context context) {
        cityListOpenHelper = new CityListOpenHelper(context, "cityList.db", null, 1);
    }

    //初始化DataBase
    public synchronized static CityListDatabase getInstance(Context context) {
        if (cityListDB == null) {
            cityListDB = new CityListDatabase(context);
        }
        return cityListDB;
    }

    //保存省级数据
    public void saveProvince(ProvinceBean mProvince) {
        SQLiteDatabase db = cityListOpenHelper.getWritableDatabase();
        if (mProvince != null) {
            ContentValues values = new ContentValues();
            values.put("province_name", mProvince.getProvinceName());
            values.put("province_code", mProvince.getProvinceCode());
            db.insert("Province", null, values);
        }
    }

    //保存市级数据
    public void saveCity(CityBean mCity) {
        SQLiteDatabase db = cityListOpenHelper.getWritableDatabase();
        if (mCity != null) {
            ContentValues values = new ContentValues();
            values.put("city_name", mCity.getCityName());
            values.put("city_code", mCity.getCityCode());
            values.put("province_id", mCity.getProvinceId());
            db.insert("City", null, values);
        }
    }

    //保存县级数据
    public void saveCounty(CountyBean mCounty) {
        SQLiteDatabase db = cityListOpenHelper.getWritableDatabase();
        if (mCounty != null) {
            ContentValues values = new ContentValues();
            values.put("county_name", mCounty.getCountyName());
            values.put("county_code", mCounty.getCountyCode());
            values.put("city_id", mCounty.getCityId());
            values.put("weather_code", mCounty.getWeatherCode());
            db.insert("County", null, values);
        }
    }


    //从数据库读取全国所有的省级信息。
    public List<ProvinceBean> getProvinces() {
        List<ProvinceBean> list = new ArrayList<ProvinceBean>();
        SQLiteDatabase db = cityListOpenHelper.getReadableDatabase();
        Cursor cursor = db
                .query("Province", null, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                ProvinceBean mProvince = new ProvinceBean();
                mProvince.setId(cursor.getInt(0));
                mProvince.setProvinceName(cursor.getString(1));
                mProvince.setProvinceCode(cursor.getString(2));
                list.add(mProvince);
            }
        }
        return list;
    }

    //从数据库读取所有的市级信息。
    public List<CityBean> getCities(int provinceId) {
        List<CityBean> list = new ArrayList<CityBean>();
        SQLiteDatabase db = cityListOpenHelper.getReadableDatabase();
        //按照省份的ID获取
        Cursor cursor = db.query("City", null, "province_id = ?",
                new String[]{String.valueOf(provinceId)}, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                CityBean mCity = new CityBean();
                mCity.setId(cursor.getInt(0));
                mCity.setCityName(cursor.getString(1));
                mCity.setCityCode(cursor.getString(2));
                mCity.setProvinceId(provinceId);
                list.add(mCity);
            }
        }
        return list;
    }

    //从数据库读取所有的县级信息。
    public List<CountyBean> getCounties(int cityId) {
        List<CountyBean> list = new ArrayList<CountyBean>();
        SQLiteDatabase db = cityListOpenHelper.getReadableDatabase();
        //按照市级的ID获取
        Cursor cursor = db.query("County", null, "city_id = ?",
                new String[]{String.valueOf(cityId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                CountyBean mCounty = new CountyBean();
                mCounty.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                mCounty.setCountyName(cursor.getString(cursor
                        .getColumnIndex("county_name")));
                mCounty.setCountyCode(cursor.getString(cursor
                        .getColumnIndex("county_code")));
                mCounty.setCityId(cityId);
                mCounty.setWeatherCode(cursor.getString(cursor
                        .getColumnIndex("weather_code")));
                list.add(mCounty);
            } while (cursor.moveToNext());
        }
//        if (cursor != null && cursor.getCount() > 0) {
//            while (cursor.moveToNext()) {
//                CountyBean mCounty = new CountyBean();
//                mCounty.setId(cursor.getInt(0));
//                mCounty.setCountyName(cursor.getString(1));
//                mCounty.setCountyCode(cursor.getString(2));
//                mCounty.setCityId(cityId);
//                mCounty.setWeatherCode(cursor.getString(4));
//                list.add(mCounty);
//            }
//        }
        return list;
    }

    //判断数据库是否存在
    public boolean DBIsExist() {
        SQLiteDatabase db = cityListOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        int count = 0;
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                count++;
            }
        }
        if (count == FixedConstants.PROVINCE_COUNT) {
            return true;
        } else
            return false;
    }
}
