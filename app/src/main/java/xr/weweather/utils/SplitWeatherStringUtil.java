package xr.weweather.utils;

import java.util.ArrayList;

import xr.weweather.bean.WeatherBean;

public class SplitWeatherStringUtil {

    public static ArrayList<WeatherBean> splitWeatherInfo(String info) {
        ArrayList<WeatherBean> infoList = new ArrayList<WeatherBean>();

        String[] infoArray = info.split("##");

        WeatherBean weather = new WeatherBean();

        weather.setLocation(infoArray[0]);
        weather.setWeather(infoArray[1]);
        weather.setTemperature(infoArray[2]);
        weather.setTime(infoArray[3]);

        infoList.add(weather);
        return infoList;


    }
}
