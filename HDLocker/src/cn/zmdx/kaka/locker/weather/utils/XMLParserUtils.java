
package cn.zmdx.kaka.locker.weather.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Xml;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.weather.entity.CityInfo;
import cn.zmdx.kaka.locker.weather.entity.MeteorologicalInfo;

/**
 * 解析assets/下xml,获取相关属性值。
 */
public class XMLParserUtils {
    private static Context mContext = HDApplication.getContext();

    private static String areaIdInXml = null;

    /**
     * 解析CityInfo.xml
     * 
     * @param xmlStream
     * @return
     */
    public static List<CityInfo> pullCityInfoParseXML(InputStream xmlStream) {
        List<CityInfo> cityInfos = null;
        CityInfo cityInfo = null;
        XmlPullParser parser = Xml.newPullParser();
        if (parser != null) {
            try {
                parser.setInput(xmlStream, "UTF-8");
                int eventType = parser.getEventType();
                cityInfos = new ArrayList<CityInfo>();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String nodeName = parser.getName();
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:
                            break;
                        case XmlPullParser.START_TAG:
                            if ("d".equals(nodeName)) {
                                cityInfo = new CityInfo();
                                cityInfo.setAreaId(parser.getAttributeValue(0));
                                cityInfo.setCityName(parser.getAttributeValue(1));
                                cityInfo.setCityProvince(parser.getAttributeValue(3));
                                cityInfos.add(cityInfo);
                                cityInfo = null;
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            break;
                    }
                    eventType = parser.next();
                }
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
        return cityInfos;
    }

    /**
     * 根据城市名得到区域id
     * 
     * @param xmlStream
     * @param cityNameStr
     * @return
     */
    public static String getAreaId(String cityNameStr, String provinceNameStr) {
        AssetManager asset = mContext.getAssets();
        InputStream xmlStream;
        try {
            xmlStream = asset.open("cityInfo.xml");
            if (!TextUtils.isEmpty(cityNameStr) && !TextUtils.isEmpty(provinceNameStr)) {
                List<CityInfo> cityInfos = pullCityInfoParseXML(xmlStream);
                for (CityInfo cityInfo : cityInfos) {
                    String cityName = cityInfo.getCityName();
                    String cityProvince = cityInfo.getCityProvince();
                    if (cityNameStr.contains(cityName) && provinceNameStr.contains(cityProvince)) {
                        areaIdInXml = cityInfo.getAreaId();
                        return areaIdInXml;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return areaIdInXml;
    }

    /**
     * 解析MeteorologicalInfo.xml文件
     * 
     * @param xmlStream
     * @return
     */
    public static List<MeteorologicalInfo> pullMeteorologicalInfoParseXML(InputStream xmlStream) {
        List<MeteorologicalInfo> meteorologicalInfos = null;
        MeteorologicalInfo meteorologicalInfo = null;
        XmlPullParser parser = Xml.newPullParser();
        if (parser != null) {
            try {
                parser.setInput(xmlStream, "UTF-8");
                int eventType = parser.getEventType();
                meteorologicalInfos = new ArrayList<MeteorologicalInfo>();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    String nodeName = parser.getName();
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:
                            break;
                        case XmlPullParser.START_TAG:
                            if ("d".equals(nodeName)) {
                                meteorologicalInfo = new MeteorologicalInfo();
                                meteorologicalInfo.setDaytimeFeatureNo(parser.getAttributeValue(0));
                                meteorologicalInfo.setWeatherFeatureIndexNameCH(parser
                                        .getAttributeValue(1));
                                meteorologicalInfos.add(meteorologicalInfo);
                                meteorologicalInfo = null;
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            break;
                    }
                    eventType = parser.next();
                }
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
        return meteorologicalInfos;
    }

    /**
     * 获取天气特征名，如晴，多云等
     * 
     * @param xmlStream
     * @param featureNoStr
     * @return
     */
    public static String getFeatureNameByNo(String featureNoStr) {
        String featureName = null;
        AssetManager asset = mContext.getAssets();
        InputStream xmlStream;
        try {
            xmlStream = asset.open("MeteorologicalCode.xml");
            List<MeteorologicalInfo> meteorologicalInfos = pullMeteorologicalInfoParseXML(xmlStream);
            for (MeteorologicalInfo meteorologicalInfo : meteorologicalInfos) {
                String daytimeFeatureNo = meteorologicalInfo.getDaytimeFeatureNo();
                if (featureNoStr.equals(daytimeFeatureNo)) {
                    String weatherFeatureIndexNameCH = meteorologicalInfo
                            .getWeatherFeatureIndexNameCH();
                    featureName = weatherFeatureIndexNameCH;
                    return featureName;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return featureName;
    }
}
