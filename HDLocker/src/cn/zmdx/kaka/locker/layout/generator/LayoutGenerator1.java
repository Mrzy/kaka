package cn.zmdx.kaka.locker.layout.generator;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.LockScreenManager;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.font.FontManager;
import cn.zmdx.kaka.locker.layout.TimeLayoutManager;
import cn.zmdx.kaka.locker.settings.ChooseCityActivity;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;
import cn.zmdx.kaka.locker.weather.PandoraLocationManager;
import cn.zmdx.kaka.locker.weather.entity.MeteorologicalCodeConstant;
import cn.zmdx.kaka.locker.weather.entity.SmartWeatherFeatureIndexInfo;
import cn.zmdx.kaka.locker.weather.entity.SmartWeatherFeatureInfo;
import cn.zmdx.kaka.locker.weather.entity.SmartWeatherInfo;
import cn.zmdx.kaka.locker.weather.utils.SmartWeatherUtils;
import cn.zmdx.kaka.locker.weather.utils.XMLParserUtils;
import cn.zmdx.kaka.locker.widget.TextClockCompat;

public class LayoutGenerator1 extends BaseLayoutGenerator {

    private Context mContext;

    private PandoraConfig mPandoraConfig;

    private TextClockCompat mClock, mDate;

    private View mWeatherInfoLayout;

    private View mWeatherFeatureLayout;

    private FrameLayout mNoWeatherLayout;

    private TextView mLunarCalendar;

    private TextView mWeatherCentTemp;

    private TextView mCityName;

    private TextView mNoWeather;

    private ImageView mWeatherFeaturePic;

    private int featureIndexPicResId;

    private String featureNameByNo;

    private String centTempDay;

    private String centTempNight;

    private String daytimeFeatureNo;

    private String nightFeatureNo;

    public LayoutGenerator1(Context context) {
        mContext = context;
        mPandoraConfig = PandoraConfig.newInstance(context);
    }

    @Override
    public int getLayoutId() {
        return TimeLayoutManager.LAYOUT_ID1;
    }

    @Override
    public View createView() {
        View view = LayoutInflater.from(HDApplication.getContext()).inflate(R.layout.date_weather_widget_layout1, null);
        mDate = (TextClockCompat) view.findViewById(R.id.lock_date);
        mDate.setFormat24Hour("MM月dd日 E");
        mDate.setFormat12Hour("MM月dd日 E");
        mClock = (TextClockCompat) view.findViewById(R.id.clock);
        mClock.setTypeface(FontManager.getTypeface("fonts/Roboto-Thin.ttf"));
        mClock.force24Format();

        mWeatherInfoLayout = view.findViewById(R.id.ll_weather_info);
        boolean isShowWeather = mPandoraConfig.isShowWeather();
        if (isShowWeather) {
            mWeatherInfoLayout.setVisibility(View.VISIBLE);
        } else {
            mWeatherInfoLayout.setVisibility(View.GONE);
        }
        mWeatherFeatureLayout = view.findViewById(R.id.rl_weather_feature);
        mNoWeatherLayout = (FrameLayout) view.findViewById(R.id.fl_no_weather);
        mNoWeather = (TextView) view.findViewById(R.id.tv_no_weather);
        mLunarCalendar = (TextView) view.findViewById(R.id.tv_lunar_calendar);
        setLunarCalendar();
        mWeatherFeaturePic = (ImageView) view.findViewById(R.id.iv_weather_feature_pic);
        mWeatherCentTemp = (TextView) view.findViewById(R.id.tv_weather_centtemp);
        mCityName = (TextView) view.findViewById(R.id.tv_city_name);
        return view;
    }

    @Override
    protected void bindWeatherData(SmartWeatherInfo smartWeatherInfo) {
        if (smartWeatherInfo == null) {
            if (mWeatherInfoLayout != null) {
                mWeatherFeatureLayout.setVisibility(View.GONE);
                mNoWeatherLayout.setVisibility(View.VISIBLE);
                if (mNoWeather != null) {
                    if (!HDBNetworkState.isNetworkAvailable()) {
                        mNoWeather.setText(R.string.tip_no_news);
                    } else if (TextUtils.isEmpty(mPandoraConfig.getLastCityName())
                            && TextUtils.isEmpty(mPandoraConfig.getTheCityHasSet())) {
                        mNoWeather.setText(R.string.guide_to_choose_city);
                        mNoWeather.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LockScreenManager.getInstance().setRunnableAfterUnLock(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent in = new Intent(mContext, ChooseCityActivity.class);
                                        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        mContext.startActivity(in);
                                    }
                                });
                                LockScreenManager.getInstance().unLock();
                            }
                        });
                    } else {
                        mWeatherInfoLayout.setVisibility(View.GONE);
                    }
                }
            }
            return;
        }

        setCityName();

        SmartWeatherFeatureInfo smartWeatherFeatureInfo = smartWeatherInfo
                .getSmartWeatherFeatureInfo();
        List<SmartWeatherFeatureIndexInfo> smartWeatherFeatureIndexInfoList = smartWeatherFeatureInfo
                .getSmartWeatherFeatureIndexInfoList();

        SmartWeatherFeatureIndexInfo smartWeatherFeatureIndexInfo = smartWeatherFeatureIndexInfoList
                .get(0);
        if (smartWeatherFeatureIndexInfo != null) {
            nightFeatureNo = smartWeatherFeatureIndexInfo.getNightFeatureNo();
            centTempNight = smartWeatherFeatureIndexInfo.getNightCentTemp();
            centTempDay = smartWeatherFeatureIndexInfo.getDaytimeCentTemp();
            daytimeFeatureNo = smartWeatherFeatureIndexInfo.getDaytimeFeatureNo();
        }
        if (!TextUtils.isEmpty(daytimeFeatureNo) && !TextUtils.isEmpty(centTempDay)) {
            featureIndexPicResId = SmartWeatherUtils.getFeatureIndexPicByNo(daytimeFeatureNo);
            featureNameByNo = XMLParserUtils.getFeatureNameByNo(daytimeFeatureNo);
            if (mWeatherFeaturePic != null) {
                mWeatherFeaturePic.setBackgroundResource(featureIndexPicResId);
            }
            if (mWeatherCentTemp != null) {
                if (!TextUtils.isEmpty(centTempNight)) {
                    mWeatherCentTemp.setText((centTempNight + "°") + "~" + (centTempDay + "°"));
                } else {
                    mWeatherCentTemp.setText((centTempDay + "°"));
                }
            }
        } else if (!TextUtils.isEmpty(nightFeatureNo) && !TextUtils.isEmpty(centTempNight)) {
            featureIndexPicResId = SmartWeatherUtils.getFeatureIndexPicByNo(nightFeatureNo);
            featureNameByNo = XMLParserUtils.getFeatureNameByNo(nightFeatureNo);
            if (featureNameByNo.equals(MeteorologicalCodeConstant.meterologicalNames[0])) {
                featureIndexPicResId = MeteorologicalCodeConstant.meteorologicalCodePics[16];
            }
            if (mWeatherFeaturePic != null) {
                mWeatherFeaturePic.setBackgroundResource(featureIndexPicResId);
            }
            if (mWeatherCentTemp != null) {
                mWeatherCentTemp.setText((centTempNight + "°"));
            }
        } else {
            if (mWeatherInfoLayout != null) {
                mWeatherInfoLayout.setVisibility(View.GONE);
            }
        }
    }

    private void setCityName() {
        String cityNameStr = PandoraLocationManager.getInstance(mContext).getCityName();
        String theCityHasSet = mPandoraConfig.getTheCityHasSet();
        String cityName = "";
        if (!TextUtils.isEmpty(theCityHasSet)) {
            String[] split = theCityHasSet.split(",");
            if (!TextUtils.isEmpty(split[0])) {
                cityName = split[0];
            } else {
                cityName = mPandoraConfig.getLastCityName();
            }
        } else {
            if (!TextUtils.isEmpty(cityNameStr)) {
                cityName = cityNameStr;
            } else {
                cityName = mPandoraConfig.getLastCityName();
            }
        }
        if (mCityName != null) {
            if (!TextUtils.isEmpty(cityName)) {
                mCityName.setText(cityName);
            } else {
                mCityName.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setLunarCalendar() {
        String lunarCal = SmartWeatherUtils.getLunarCal();
        boolean isLunarCalendarOn = mPandoraConfig.isLunarCalendarOn();
        if (mLunarCalendar != null && !TextUtils.isEmpty(lunarCal)) {
            if (isLunarCalendarOn) {
                mLunarCalendar.setText(lunarCal);
            } else {
                mLunarCalendar.setVisibility(View.GONE);
            }
        }
    }
}
