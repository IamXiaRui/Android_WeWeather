package xr.weweather.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;

import xr.weweather.bean.WeatherBean;

/**
 * @Description:
 */
public class FlushWeatherUtil {

    public void flushWeather(final Context context, final ProgressDialog dialog, final Handler handler,final String cityInfo, final String weatherCode, final String weather_url){

        //开启线程解析JSON数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                //得到解析工具类返回的工具类
                ArrayList<WeatherBean> weatherInfoList = AnalysisWeatherUtil.getWeatherForJson(context, weather_url);
                //如果返回集合中有数据
                if (weatherInfoList.size() > 0) {
                    dialog.dismiss();
                    //将数据取出，并发送给主线程
                    WeatherBean weatherInfo = weatherInfoList.get(0);
                    Message msg = Message.obtain();
                    msg.obj = cityInfo + "##" + weatherCode + "##" + weatherInfo.getWeather() + "##" + weatherInfo.getTemperature() + "##" + weatherInfo.getTime();
                    handler.sendMessage(msg);
                }
            }
        }).start();

    }
}
