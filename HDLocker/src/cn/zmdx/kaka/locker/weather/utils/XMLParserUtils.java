
package cn.zmdx.kaka.locker.weather.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.util.Xml;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.weather.entity.CityInfo;

/**
 * 解析assets/下cityInfo.xml
 */
public class XMLParserUtils {

    // 采用XmlPullParser来解析XML文件
    public static List<CityInfo> pullParseXML(InputStream xmlStream) {
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

    public static String getAreaIdByCityName(InputStream xmlStream, String cityNameStr) {
        String result = null;
        List<CityInfo> cityInfos = pullParseXML(xmlStream);
        for (CityInfo cityInfo : cityInfos) {
            String cityName = cityInfo.getCityName();
            if (BuildConfig.DEBUG) {
                Log.i("XMLParserUtils", "--------cityName--->>" + cityName);
                Log.i("XMLParserUtils", "--------cityNameStr--->>" + cityNameStr);
            }
            if (cityNameStr.contains(cityName)) {
                String areaId = cityInfo.getAreaId();
                result = areaId;
                if (BuildConfig.DEBUG) {
                    Log.i("XMLParserUtils", "--------result--->>" + result);
                }
                return result;
            }
        }
        return result;
    }
}
