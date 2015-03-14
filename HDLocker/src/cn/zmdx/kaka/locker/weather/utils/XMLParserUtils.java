
package cn.zmdx.kaka.locker.weather.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Xml;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.weather.entity.CityInfo;
import cn.zmdx.kaka.locker.weather.entity.MeteorologicalCodeConstant;
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
    public static String getAreaId(String cityNameStr) {
        AssetManager asset = mContext.getAssets();
        InputStream xmlStream;
        try {
            xmlStream = asset.open("CityInfo.xml");
            if (cityNameStr != null) {
                List<CityInfo> cityInfos = pullCityInfoParseXML(xmlStream);
                for (CityInfo cityInfo : cityInfos) {
                    String cityName = cityInfo.getCityName();
                    if (cityNameStr.contains(cityName)) {
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

    /**
     * 根据白天天气指数值获取对应的天气特征图片
     * 
     * @param featureNoStr
     * @return
     */
    public static int getFeatureIndexPicByNo(String featureNoStr) {
        int result = 0;
        if (featureNoStr.equals("00")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[0];
        } else if (featureNoStr.equals("01")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[1];
        } else if (featureNoStr.equals("02")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[2];
        } else if (featureNoStr.equals("04")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[3];
        } else if (featureNoStr.equals("06")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[4];
        } else if (featureNoStr.equals("07")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[5];
        } else if (featureNoStr.equals("08")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[6];
        } else if (featureNoStr.equals("09")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[7];
        } else if (featureNoStr.equals("14")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[8];
        } else if (featureNoStr.equals("15")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[9];
        } else if (featureNoStr.equals("16")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[10];
        } else if (featureNoStr.equals("18")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[11];
        } else if (featureNoStr.equals("29")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[12];
        } else if (featureNoStr.equals("53")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[13];
        } else if (featureNoStr.equals("000")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[14];
        } else if (featureNoStr.equals("001")) {
            result = MeteorologicalCodeConstant.meteorologicalCodePics[15];
        }
        return result;
    }
}
