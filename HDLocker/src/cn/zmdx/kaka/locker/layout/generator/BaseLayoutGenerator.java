
package cn.zmdx.kaka.locker.layout.generator;

import android.text.TextUtils;
import android.view.View;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.policy.PandoraPolicy;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.weather.PandoraWeatherManager;
import cn.zmdx.kaka.locker.weather.PandoraWeatherManager.ISmartWeatherCallback;
import cn.zmdx.kaka.locker.weather.entity.SmartWeatherInfo;
import cn.zmdx.kaka.locker.weather.utils.ParseWeatherJsonUtils;

public abstract class BaseLayoutGenerator {

    public View getView() {
        View view = createView();
        updateWeather();
        return view;
    }

    public abstract int getLayoutId();

    protected abstract View createView();

    public void updateWeather() {
        String smartWeatherInfo = PandoraWeatherManager.getInstance().getWeatherFromCache();
        renderWeatherData(smartWeatherInfo);
        long str2TimeMillis = PandoraConfig.newInstance(HDApplication.getContext())
                .getLastCheckWeatherTime();
        if (System.currentTimeMillis() - str2TimeMillis >= PandoraPolicy.MIN_CHECK_WEATHER_DURAION) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD(HDApplication.getContext().getString(
                        R.string.enable_to_process_weather_info));
            }
            PandoraWeatherManager.getInstance().getWeatherFromNetwork(new ISmartWeatherCallback() {

                @Override
                public void onSuccess(String smartWeatherInfo) {
                    renderWeatherData(smartWeatherInfo);
                }

                @Override
                public void onFailure() {

                }
            });
        }
    }

    private void renderWeatherData(String smartWeatherInfoStr) {
        SmartWeatherInfo smartWeatherInfo = null;
        if (!TextUtils.isEmpty(smartWeatherInfoStr)) {
            smartWeatherInfo = ParseWeatherJsonUtils.parseWeatherJson(smartWeatherInfoStr);
        }
        bindWeatherData(smartWeatherInfo);
    }

    /**
     * 将天气数据与view绑定
     * 
     * @param smartWeatherInfo may be null
     */
    protected abstract void bindWeatherData(SmartWeatherInfo smartWeatherInfo);
}
