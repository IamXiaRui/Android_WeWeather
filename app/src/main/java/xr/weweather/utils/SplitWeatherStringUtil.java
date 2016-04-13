package xr.weweather.utils;

import android.content.Context;

import java.util.ArrayList;

import xr.weweather.bean.WeatherBean;
import xr.weweather.db.WeatherDatabase;

public class SplitWeatherStringUtil {

    private static WeatherDatabase weatherDB;

    public static ArrayList<WeatherBean> splitWeatherInfo(Context context, String info) {

        weatherDB = weatherDB.getInstance(context);

        ArrayList<WeatherBean> infoList = new ArrayList<WeatherBean>();

        String[] infoArray = info.split("##");

        WeatherBean weather = new WeatherBean();

        weather.setLocation(infoArray[0]);
        weather.setWeatherCode(infoArray[1]);
        weather.setWeather(infoArray[2]);
        weather.setTemperature(infoArray[3]);
        weather.setTime(infoArray[4]);

        infoList.add(weather);
        weatherDB.saveWeather(weather);

        return infoList;


    }
}
