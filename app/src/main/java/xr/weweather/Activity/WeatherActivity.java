package xr.weweather.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import xr.weweather.R;
import xr.weweather.bean.FixedConstants;
import xr.weweather.bean.WeatherBean;
import xr.weweather.db.CityListDatabase;
import xr.weweather.utils.AnalysisCityListUtil;
import xr.weweather.utils.SplitWeatherStringUtil;

public class WeatherActivity extends Activity implements View.OnClickListener {

    private ImageButton locationButton, flushButton;
    private ImageView weatherImage;
    private Context thisContext = WeatherActivity.this;
    //定义解析结束标记
    private int analysisEnd = 0;
    private TextView cityNameText, tempText, timeText;
    private ProgressDialog getCityListDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        InitActivityView();

        locationButton.setOnClickListener(this);
        flushButton.setOnClickListener(this);
    }

    private void InitActivityView() {
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        weatherImage = (ImageView) findViewById(R.id.weather_image);
        locationButton = (ImageButton) findViewById(R.id.location_button);
        flushButton = (ImageButton) findViewById(R.id.flush_button);
        cityNameText = (TextView) findViewById(R.id.cityName_text);
        timeText = (TextView) findViewById(R.id.time_text);
        tempText = (TextView) findViewById(R.id.temp_text);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.location_button:
                //每次点击判断数据库是否存在
                if ((new CityListDatabase(thisContext)).DBIsExist()) {
                    //数据库已经存在，则直接跳转
                    Intent intent = new Intent();
                    intent.setClass(thisContext, ChooseCityActivity.class);
                    startActivityForResult(intent, 1);
                } else {
                    getCityListDialog = new ProgressDialog(thisContext);
                    getCityListDialog.setTitle("初始化");
                    getCityListDialog.setMessage("正在卖力加载中,请稍等...");
                    getCityListDialog.setCancelable(false);
                    getCityListDialog.setMax(100);
                    getCityListDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    getCityListDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "后台运行", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(thisContext, "后台卖力加载中,请稍等...", Toast.LENGTH_SHORT).show();
                            getCityListDialog.dismiss();
                        }
                    });

                    //开启子线程解析XML
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            analysisEnd = AnalysisCityListUtil.XMLAnalysisUtil(thisContext, getCityListDialog);
                            Message msg = Message.obtain();
                            msg.obj = analysisEnd;
                            handler.sendMessage(msg);
                        }
                    }).start();
                    getCityListDialog.show();
                }
                break;
            case R.id.flush_button:
                Toast.makeText(thisContext, "刷新功能暂未开放...", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    //接收子线程传递的值
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            analysisEnd = (int) msg.obj;
            //如果解析结束标记等于固定常量，表示解析结束
            if (analysisEnd == FixedConstants.XML_END) {
                Intent intent = new Intent();
                intent.setClass(thisContext, ChooseCityActivity.class);
                startActivityForResult(intent, 1);
                getCityListDialog.dismiss();
                Toast.makeText(thisContext, "初始化成功,谢谢等待", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(thisContext, "初始化失败，请重试", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String weatherInfo = data.getStringExtra("WEATHER_INFO");
                    ArrayList<WeatherBean> nowWeatherList = SplitWeatherStringUtil.splitWeatherInfo(weatherInfo);
                    WeatherBean nowWeather = nowWeatherList.get(0);
                    updateWeatherUI(nowWeather);
                }
                break;
            default:
        }
    }

    private void updateWeatherUI(WeatherBean nowWeather) {
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
            Toast.makeText(thisContext, nowWeather.getWeather() + " - 暂无天气图标", Toast.LENGTH_LONG).show();
        }
    }

    //再按一次退出程序
    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - FixedConstants.EXIT_TIME) > 2000) {
            Toast.makeText(thisContext, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            FixedConstants.EXIT_TIME = System.currentTimeMillis();
            return;
        }
        finish();
    }
}
