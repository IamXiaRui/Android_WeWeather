package xr.weweather.utils;

import android.app.ProgressDialog;
import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;

import xr.weweather.R;
import xr.weweather.bean.CityBean;
import xr.weweather.bean.CountyBean;
import xr.weweather.bean.FixedConstants;
import xr.weweather.bean.ProvinceBean;
import xr.weweather.db.CityListDatabase;


/**
 * 解析XML中的城市列表数据
 */
public class AnalysisCityListUtil {

    private static ProvinceBean mProvince;
    private static CityBean mCity;
    private static CountyBean mCounty;

    private static CityListDatabase cityListDB;

    //已经解析的省的数量
    public static int CURRENT_PROVINCE_COUNT = 0;

    /**
     * 解析XML工具类
     */
    public static int XMLAnalysisUtil(Context context, ProgressDialog progressBar) {
        //初始化数据库
        cityListDB = cityListDB.getInstance(context);

        try {
            //构建XmlPullParserFactory解析XML
            XmlPullParserFactory pullParserFactory = XmlPullParserFactory.newInstance();
            //获取XmlPullParser的实例
            XmlPullParser xmlPullParser = pullParserFactory.newPullParser();
            //设置输入流  XML文件
            xmlPullParser.setInput(context.getResources().openRawResource(R.raw.city_id_list), "UTF-8");
            //得到当前节点的类型
            int eventType = xmlPullParser.getEventType();
            try {
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    //得到当前节点的名称
                    String nodeName = xmlPullParser.getName();
                    switch (eventType) {
                        //文档开始
                        case XmlPullParser.START_DOCUMENT:
                            break;
                        //开始节点
                        case XmlPullParser.START_TAG:
                            //如果开始节点是省，实例化省对象，并添加属性
                            if ("province".equals(nodeName)) {
                                mProvince = new ProvinceBean();
                                mProvince.setProvinceName(xmlPullParser.getAttributeValue(0));
                                mProvince.setProvinceCode(xmlPullParser.getAttributeValue(1));
                            }
                            //如果开始节点是市，实例化省对象，并添加属性
                            else if ("city".equals(nodeName)) {
                                mCity = new CityBean();
                                mCity.setProvinceId(Integer.parseInt(mProvince.getProvinceCode()));
                                mCity.setCityName(xmlPullParser.getAttributeValue(0));
                                mCity.setCityCode(xmlPullParser.getAttributeValue(1));
                            }
                            //如果开始节点是县，实例化省对象，并添加属性
                            else if ("county".equals(nodeName)) {
                                mCounty = new CountyBean();
                                mCounty.setCityId(Integer.parseInt(mCity.getCityCode()));
                                mCounty.setCountyName(xmlPullParser.getAttributeValue(0));
                                mCounty.setCountyCode(xmlPullParser.getAttributeValue(1));
                                mCounty.setWeatherCode(xmlPullParser.getAttributeValue(2));
                            }
                            break;
                        //结束节点
                        case XmlPullParser.END_TAG:
                            //如果结束节点是省，保存省对象到数据库
                            if ("province".equals(nodeName)) {
                                cityListDB.saveProvince(mProvince);
                                //添加省数量的三倍 让进度条更新
                                progressBar.setProgress(CURRENT_PROVINCE_COUNT);
                                //更新完成后加3
                                CURRENT_PROVINCE_COUNT = CURRENT_PROVINCE_COUNT + 3;
                            }
                            //如果结束节点是市，保存市对象到数据库
                            else if ("city".equals(nodeName)) {
                                cityListDB.saveCity(mCity);
                            }
                            //如果结束节点是县，保存县对象到数据库
                            else if ("county".equals(nodeName)) {
                                cityListDB.saveCounty(mCounty);
                            }
                            break;
                        default:
                            break;
                    }
                    eventType = xmlPullParser.next();
                    //循环之前判断是否是文档结束节点，如果是，返回解析完成标记
                    if (eventType == XmlPullParser.END_DOCUMENT) {
                        //清楚进度条状态
                        CURRENT_PROVINCE_COUNT = 0;
                        return FixedConstants.XML_END;
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //在此返回，表示解析出错，返回解析失败标记
        return FixedConstants.XML_ERROR;
    }
}
