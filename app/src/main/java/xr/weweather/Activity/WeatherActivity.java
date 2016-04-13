package xr.weweather.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import xr.weweather.db.WeatherDatabase;
import xr.weweather.utils.AnalysisCityListUtil;
import xr.weweather.utils.FlushWeatherUtil;
import xr.weweather.utils.SplitWeatherStringUtil;
import xr.weweather.utils.UpdateWeatherUtil;

public class WeatherActivity extends Activity implements View.OnClickListener {

    private ImageButton locationButton, flushButton;
    private ImageView weatherImage;
    private Context thisContext = WeatherActivity.this;
    //定义解析结束标记
    private int analysisEnd = 0;
    private TextView cityNameText, tempText, timeText;
    private ProgressDialog getCityListDialog, flushWeatherDialog;
    private WeatherDatabase weatherDB;

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
        weatherDB = new WeatherDatabase(thisContext);
        locationButton = (ImageButton) findViewById(R.id.location_button);
        flushButton = (ImageButton) findViewById(R.id.flush_button);
        cityNameText = (TextView) findViewById(R.id.cityName_text);
        weatherImage = (ImageView) findViewById(R.id.weather_image);
        timeText = (TextView) findViewById(R.id.time_text);
        tempText = (TextView) findViewById(R.id.temp_text);

        if (weatherDB.DBIsExist()) {
            ArrayList<WeatherBean> oldWeatherList = weatherDB.getOldWeather();
            UpdateWeatherUtil.updateWeatherUI(thisContext, cityNameText, tempText, timeText, weatherImage, oldWeatherList.get(oldWeatherList.size() - 1));
        } else
            Toast.makeText(thisContext, "请选择城市", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.location_button:
                //每次点击判断数据库是否存在
                if (weatherDB.DBIsExist()) {
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

                    //开启子线程解析XML
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            analysisEnd = AnalysisCityListUtil.XMLAnalysisUtil(thisContext, getCityListDialog);
                            Message msg = Message.obtain();
                            msg.arg1 = analysisEnd;
                            handler.sendMessage(msg);
                        }
                    }).start();
                    getCityListDialog.show();
                }
                break;
            case R.id.flush_button:
                if (new WeatherDatabase(thisContext).DBIsExist()) {
                    final String currentLocation = cityNameText.getText().toString().trim();
                    final String currentCode = new WeatherDatabase(thisContext).getWeatherCode(currentLocation);
                    if (!currentCode.equals("")) {
                        final String weather_url = "https://api.heweather.com/x3/weather?cityid=CN" + currentCode + "&key=573a3ba3c95a43ad94e70c34610720f9";
                        flushWeatherDialog = new ProgressDialog(thisContext);
                        flushWeatherDialog.setTitle("提示");
                        flushWeatherDialog.setMessage("正在卖力刷新天气数据,请稍等...");
                        flushWeatherDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        flushWeatherDialog.setCancelable(true);
                        flushWeatherDialog.show();
                        new FlushWeatherUtil().flushWeather(thisContext,flushWeatherDialog,handler,currentLocation,currentCode,weather_url);
                    } else
                        Toast.makeText(thisContext, "刷新失败", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(thisContext, "请先选择城市", Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                break;
        }
    }

    //接收子线程传递的值
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            String weatherInfo = (String)msg.obj;
            ArrayList<WeatherBean> nowWeatherList = SplitWeatherStringUtil.splitWeatherInfo(thisContext, weatherInfo);
            UpdateWeatherUtil.updateWeatherUI(thisContext, cityNameText, tempText, timeText, weatherImage, nowWeatherList.get(0));

            analysisEnd = (int) msg.arg1;
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
                    ArrayList<WeatherBean> nowWeatherList = SplitWeatherStringUtil.splitWeatherInfo(thisContext, weatherInfo);
                    UpdateWeatherUtil.updateWeatherUI(thisContext, cityNameText, tempText, timeText, weatherImage, nowWeatherList.get(0));
                }
                break;
            default:
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
