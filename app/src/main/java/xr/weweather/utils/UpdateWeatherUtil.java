package xr.weweather.utils;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import xr.weweather.R;
import xr.weweather.bean.WeatherBean;

/**
 * @Description:
 */
public class UpdateWeatherUtil {

    public static void updateWeatherUI(Context context, TextView cityNameText, TextView tempText, TextView timeText, ImageView weatherImage, WeatherBean nowWeather) {

        cityNameText.setText(nowWeather.getLocation());
        tempText.setText(nowWeather.getWeather() + "  |  " + nowWeather.getTemperature() + "℃");
        timeText.setText(nowWeather.getTime());

        if ((nowWeather.getWeather()).equals("晴")) {
            weatherImage.setImageResource(R.drawable.weather_sun);
        } else if ((nowWeather.getWeather()).equals("多云") || (nowWeather.getWeather()).equals("少云")) {
            weatherImage.setImageResource(R.drawable.weather_cloudy);
        } else if ((nowWeather.getWeather()).equals("晴间多云")) {
            weatherImage.setImageResource(R.drawable.weather_suntocloudy);
        } else if ((nowWeather.getWeather()).equals("阴")) {
            weatherImage.setImageResource(R.drawable.weather_overcast);
        } else if ((nowWeather.getWeather()).equals("阵雨")) {
            weatherImage.setImageResource(R.drawable.weather_showerrain);
        } else if ((nowWeather.getWeather()).equals("强阵雨")) {
            weatherImage.setImageResource(R.drawable.weather_heavyshower);
        } else if ((nowWeather.getWeather()).equals("雷阵雨")) {
            weatherImage.setImageResource(R.drawable.weather_thundershower);
        } else if ((nowWeather.getWeather()).equals("强雷阵雨")) {
            weatherImage.setImageResource(R.drawable.weather_thunderstorm);
        } else if ((nowWeather.getWeather()).equals("冰雹")) {
            weatherImage.setImageResource(R.drawable.weather_hail);
        } else if ((nowWeather.getWeather()).equals("小雨") || (nowWeather.getWeather()).equals("毛毛雨/细雨")) {
            weatherImage.setImageResource(R.drawable.weather_lightrain);
        } else if ((nowWeather.getWeather()).equals("中雨")) {
            weatherImage.setImageResource(R.drawable.weather_moderaterain);
        } else if ((nowWeather.getWeather()).equals("大雨") || (nowWeather.getWeather()).equals("极端降雨")) {
            weatherImage.setImageResource(R.drawable.weather_heavyrain);
        } else if ((nowWeather.getWeather()).equals("暴雨")) {
            weatherImage.setImageResource(R.drawable.weather_storm);
        } else if ((nowWeather.getWeather()).equals("大暴雨")) {
            weatherImage.setImageResource(R.drawable.weather_heavystorm);
        } else if ((nowWeather.getWeather()).equals("冻雨")) {
            weatherImage.setImageResource(R.drawable.weather_freezingrain);
        } else if ((nowWeather.getWeather()).equals("小雪")) {
            weatherImage.setImageResource(R.drawable.weather_lightsnow);
        } else if ((nowWeather.getWeather()).equals("中雪")) {
            weatherImage.setImageResource(R.drawable.weather_moderatesnow);
        } else if ((nowWeather.getWeather()).equals("大雪")) {
            weatherImage.setImageResource(R.drawable.weather_heavysnow);
        } else if ((nowWeather.getWeather()).equals("暴雪")) {
            weatherImage.setImageResource(R.drawable.weather_snowstorm);
        } else if ((nowWeather.getWeather()).equals("雨夹雪")) {
            weatherImage.setImageResource(R.drawable.weather_sleet);
        } else if ((nowWeather.getWeather()).equals("阵雨夹雪")) {
            weatherImage.setImageResource(R.drawable.weather_showersnow);
        } else if ((nowWeather.getWeather()).equals("阵雪")) {
            weatherImage.setImageResource(R.drawable.weather_snowflurry);
        } else {
            weatherImage.setImageResource(R.drawable.weather_unknow);
            Toast.makeText(context, nowWeather.getWeather() + " - 暂无天气图标", Toast.LENGTH_LONG).show();
        }
    }
}
