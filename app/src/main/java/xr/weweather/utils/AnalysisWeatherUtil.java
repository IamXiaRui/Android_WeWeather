package xr.weweather.utils;


import android.content.Context;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import xr.weweather.bean.WeatherBean;

//解析天气JSON工具类

public class AnalysisWeatherUtil {

    public static ArrayList<WeatherBean> getWeatherForJson(Context context, String url) {

        ArrayList<WeatherBean> weatherInfoList = new ArrayList<>();

        try {
            URL weather_url = new URL(url);

            HttpURLConnection urlConnection = (HttpURLConnection) weather_url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(1000 * 10);

            if (urlConnection.getResponseCode() == 200) {
                //将API返回的JSON数据解析成字符串
                InputStream inputStream = urlConnection.getInputStream();
                String weatherInfoString = NetStreamToStringUtil.streamToString(inputStream);

                //解析JSON数据
                JSONObject weatherRoot = new JSONObject(weatherInfoString);
                JSONObject realTimeWeather = weatherRoot.getJSONObject("realtime");

                String currentTemp = realTimeWeather.getString("temp");
                String currentWeather = realTimeWeather.getString("weather");
                String currentTime = realTimeWeather.getString("time");

                //并封装到天气Bean中
                WeatherBean realtimeWeather = new WeatherBean();
                realtimeWeather.setTemperature(currentTemp);
                realtimeWeather.setWeather(currentWeather);
                realtimeWeather.setTime(currentTime);

                //最后加入集合，并返回
                weatherInfoList.add(realtimeWeather);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return weatherInfoList;
    }
}
