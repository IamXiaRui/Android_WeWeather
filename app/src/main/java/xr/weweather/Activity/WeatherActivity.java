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

import xr.weweather.R;
import xr.weweather.bean.FixedConstants;
import xr.weweather.db.CityListDatabase;
import xr.weweather.utils.AnalysisCityListUtil;

public class WeatherActivity extends Activity implements View.OnClickListener {

    private ImageButton locationButton, flushButton;
    private ImageView weatherImage;
    private Context thisContext = WeatherActivity.this;
    //定义解析结束标记
    private int analysisEnd = 0;
    private TextView cityNameText, tempText;
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
                    thisContext.startActivity(intent);
                    finish();
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
                    final String weatherInfo = data.getStringExtra("WEATHER_INFO");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (weatherInfo.equals("晴")) {
                                weatherImage.setImageResource(R.drawable.weather_sun);
                            } else if (weatherInfo.equals("阴")) {
                                weatherImage.setImageResource(R.drawable.weather_overcast);
                            } else if (weatherInfo.equals("多云")) {
                                weatherImage.setImageResource(R.drawable.weather_cloudy);
                            } else
                                Toast.makeText(thisContext, weatherInfo, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            default:
                break;
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
