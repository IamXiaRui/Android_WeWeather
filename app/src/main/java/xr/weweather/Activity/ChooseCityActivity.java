package xr.weweather.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import xr.weweather.R;
import xr.weweather.bean.CityBean;
import xr.weweather.bean.CountyBean;
import xr.weweather.bean.FixedConstants;
import xr.weweather.bean.ProvinceBean;
import xr.weweather.bean.WeatherBean;
import xr.weweather.db.CityListDatabase;
import xr.weweather.utils.AnalysisWeatherUtil;

/**
 * 选择城市列表Activity
 */
public class ChooseCityActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private Context thisContext = ChooseCityActivity.this;
    private CityListDatabase cityListDB;

    private ListView citylistView;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> casheList = new ArrayList<String>();

    private List<ProvinceBean> provinceList;
    private List<CityBean> cityList;
    private List<CountyBean> countyList;

    private ProvinceBean selectedProvince;
    private CityBean selectedCity;
    private int currentLevel;

    private ProgressDialog getWeatherDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citylist);

        citylistView = (ListView) findViewById(R.id.city_list);

        //初始化并绑定一个列表适配器
        arrayAdapter = new ArrayAdapter<String>(thisContext, android.R.layout.simple_list_item_1, casheList);
        citylistView.setAdapter(arrayAdapter);

        //初始化数据库
        cityListDB = CityListDatabase.getInstance(thisContext);

        citylistView.setOnItemClickListener(this);

        //先初始化省级列表
        allProvinces();
    }

    //列表条目点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //如果点击的是省，得到当前省，并显示城市列表
        if (currentLevel == FixedConstants.LEVEL_PROVINCE) {
            selectedProvince = provinceList.get(position);
            allCities();
        }
        //如果点击的是市，得到当前市，并显示县区列表
        else if (currentLevel == FixedConstants.LEVEL_CITY) {
            selectedCity = cityList.get(position);
            allCounties();
        }
        //如果点击的是县区，直接拿到名称与天气ID，传给上一层Activity
        else if (currentLevel == FixedConstants.LEVEL_COUNTY) {
            //得到选取地区名称
            final String cityInfo = countyList.get(position).getCountyName();
            final String weatherCode = countyList.get(position).getWeatherCode();
            //得到地区天气代码，并封装成url
            final String weather_url = "https://api.heweather.com/x3/weather?cityid=CN" + weatherCode + "&key=573a3ba3c95a43ad94e70c34610720f9";
            getWeatherDialog = new ProgressDialog(thisContext);
            getWeatherDialog.setTitle("提示");
            getWeatherDialog.setMessage("正在卖力刷新天气数据,请稍等...");
            getWeatherDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            getWeatherDialog.setCancelable(true);
            getWeatherDialog.show();
            getWeatherThread(cityInfo, weatherCode, weather_url);

        }
    }

    public void getWeatherThread(final String cityInfo, final String weatherCode, final String weather_url) {
        //开启线程解析JSON数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                //得到解析工具类返回的工具类
                ArrayList<WeatherBean> weatherInfoList = AnalysisWeatherUtil.getWeatherForJson(thisContext, weather_url);
                //如果返回集合中有数据
                if (weatherInfoList.size() > 0) {
                    getWeatherDialog.dismiss();
                    //将数据取出，并发送给主线程
                    WeatherBean weatherInfo = weatherInfoList.get(0);
                    Message msg = Message.obtain();
                    msg.obj = cityInfo + "##" + weatherCode + "##" + weatherInfo.getWeather() + "##" + weatherInfo.getTemperature() + "##" + weatherInfo.getTime();
                    handler.sendMessage(msg);
                } else {
                    //没有的话，弹窗提示
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(thisContext, "获取天气数据失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    //接收子线程发送的数据
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String weatherInfo = (String) msg.obj;
            //Toast.makeText(thisContext, weatherInfo, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.putExtra("WEATHER_INFO", weatherInfo);
            setResult(RESULT_OK, intent);
            finish();
        }
    };

    /*得到省级列表*/
    private void allProvinces() {
        //从数据库中取出数据集合
        provinceList = cityListDB.getProvinces();
        //如果集合不为空
        if (provinceList.size() > 0) {
            //先清除缓存的列表
            casheList.clear();
            //遍历集合并添加到列表中
            for (ProvinceBean province : provinceList) {
                casheList.add(province.getProvinceName());
            }
            //强制刷新列表
            arrayAdapter.notifyDataSetChanged();
            //强制列表从头开始
            citylistView.setSelection(0);
            //设置当前菜单标记
            currentLevel = FixedConstants.LEVEL_PROVINCE;
        } else
            Toast.makeText(thisContext, "未查询到当前省份", Toast.LENGTH_SHORT).show();
    }

    /*得到市级列表*/
    private void allCities() {
        //按照省级的Code取市级数据并存入集合
        cityList = cityListDB.getCities(Integer.parseInt(selectedProvince.getProvinceCode()));
        if (cityList.size() > 0) {
            casheList.clear();
            for (CityBean city : cityList) {
                casheList.add(city.getCityName());
            }
            //强制刷新列表
            arrayAdapter.notifyDataSetChanged();
            citylistView.setSelection(0);
            currentLevel = FixedConstants.LEVEL_CITY;
        } else
            Toast.makeText(thisContext, "未查询到当前城市", Toast.LENGTH_SHORT).show();

    }

    /*得到县区级列表*/
    private void allCounties() {
        //按照市级的Code取县区级数据并存入集合
        countyList = cityListDB.getCounties(Integer.parseInt(selectedCity.getCityCode()));
        if (countyList.size() > 0) {
            casheList.clear();
            for (CountyBean county : countyList) {
                casheList.add(county.getCountyName());
            }
            //强制刷新列表
            arrayAdapter.notifyDataSetChanged();
            citylistView.setSelection(0);
            currentLevel = FixedConstants.LEVEL_COUNTY;
        } else
            Toast.makeText(thisContext, "未查询到当前县区", Toast.LENGTH_SHORT).show();
    }

    /*按下返回键的操作*/
    @Override
    public void onBackPressed() {
        //如果当前列表级别为县区，返回市级列表
        if (currentLevel == FixedConstants.LEVEL_COUNTY) {
            allCities();
        }
        //如果当前列表级别为市级，返回省级列表
        else if (currentLevel == FixedConstants.LEVEL_CITY) {
            allProvinces();
        } else {
            finish();
        }
    }
}
