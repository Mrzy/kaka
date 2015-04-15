
package cn.zmdx.kaka.locker.settings;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.LockScreenManager;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.weather.PandoraLocationManager;
import cn.zmdx.kaka.locker.weather.PandoraWeatherManager;
import cn.zmdx.kaka.locker.weather.PandoraWeatherManager.ISmartWeatherCallback;
import cn.zmdx.kaka.locker.weather.entity.CityInfo;
import cn.zmdx.kaka.locker.weather.entity.MeteorologicalCodeConstant;
import cn.zmdx.kaka.locker.weather.entity.SmartWeatherInfo;
import cn.zmdx.kaka.locker.weather.utils.XMLParserUtils;

public class ChooseCityActivity extends ActionBarActivity {

    private EditText mCityNameEditText;

    private ImageButton mSearchCityBtn;

    private GridView mHotCitiesGridView;

    private LinearLayout mHotCitiesLayout;

    private FrameLayout mPossibleCitiesLayout;

    private ListView mPossibleCitiesListView;

    private TextView mEmptyTextView;

    private MyGridViewAdapter myGridViewAdapter;

    private ArrayAdapter<String> mArrayAdapter;

    private List<String> mGVCityNameList;

    private List<String> mPossibleCityList;

    private String mSelectedCityName;

    private String mAimCityName;

    private String mCityNameReceived;

    private Handler myHandler = new Handler();

    PandoraConfig mPandoraConfig = PandoraConfig.newInstance(ChooseCityActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_city);
        getSupportActionBar().setBackgroundDrawable(
                getResources().getDrawable(R.drawable.action_bar_bg_blue));
        init();
        setHotCitiesGridView();
    }

    private void init() {
        mCityNameEditText = (EditText) this.findViewById(R.id.etcity);
        mCityNameEditText.addTextChangedListener(cnSearch_TextChanged);
        mSearchCityBtn = (ImageButton) this.findViewById(R.id.btnSearch);
        mHotCitiesGridView = (GridView) this.findViewById(R.id.gvcitynames);
        mHotCitiesLayout = (LinearLayout) this.findViewById(R.id.hot_cities_layout);
        mPossibleCitiesLayout = (FrameLayout) this.findViewById(R.id.possible_cities_layout);
        mPossibleCitiesListView = (ListView) this.findViewById(R.id.possible_cities_lv);
        mEmptyTextView = (TextView) this.findViewById(R.id.tvempty);
        mPossibleCitiesListView.setEmptyView(mEmptyTextView);
    }

    Runnable cityDataChanged = new Runnable() {

        @Override
        public void run() {
            String trim = mCityNameEditText.getText().toString().trim();
            getPossibleCityList(trim);
            setPossibleCitiesListView();
            mArrayAdapter.notifyDataSetChanged();
        }
    };

    private void setPossibleCitiesListView() {
        mArrayAdapter = new ArrayAdapter<String>(this, R.layout.choose_city_listview_item,
                R.id.tv_possible_city, mPossibleCityList);
        mPossibleCitiesListView.setAdapter(mArrayAdapter);
        mPossibleCitiesListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String theCityHasSet = mPandoraConfig.getTheCityHasSet();
                mSelectedCityName = mPossibleCityList.get(position);
                if (!TextUtils.isEmpty(mSelectedCityName)) {
                    String[] split = mSelectedCityName.split(",");
                    if (!TextUtils.isEmpty(split[0])) {
                        mAimCityName = split[0];
                        if (mCityNameEditText != null) {
                            mCityNameEditText.setText(mAimCityName);
                        }
                    }
                    mPandoraConfig.saveTheCityHasSet(mSelectedCityName);
                    if (TextUtils.isEmpty(theCityHasSet)
                            || !mSelectedCityName.contains(theCityHasSet)) {
                        processCityWeather(mSelectedCityName);
                        finishPage();
                    }
                }
            }
        });
    }

    private void setHotCitiesGridView() {
        myGridViewAdapter = new MyGridViewAdapter();
        myGridViewAdapter.bindData(MeteorologicalCodeConstant.hotCityNameStrings);
        mHotCitiesGridView.setAdapter(myGridViewAdapter);
        mHotCitiesGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String theCityHasSet = mPandoraConfig.getTheCityHasSet();
                mHotCitiesGridView.setSelection(position);
                mPandoraConfig.saveSelectedHotCityPosition(position);
                myGridViewAdapter.notifyDataSetChanged();
                if (position == 0) {
                    String cityName = PandoraLocationManager.getInstance(ChooseCityActivity.this)
                            .getCityName();
                    String lastCityName = mPandoraConfig.getLastCityName();
                    if (!TextUtils.isEmpty(lastCityName)) {
                        mSelectedCityName = lastCityName;
                    } else if (!TextUtils.isEmpty(cityName)) {
                        mSelectedCityName = cityName;
                    }
                } else {
                    mSelectedCityName = mGVCityNameList.get(position);
                }
                if (!TextUtils.isEmpty(mSelectedCityName)) {
                    String provinceByCity = XMLParserUtils.getProvinceByCity(mSelectedCityName);
                    mPandoraConfig.saveTheCityHasSet(mSelectedCityName + "," + provinceByCity);
                    if (TextUtils.isEmpty(theCityHasSet)
                            || !mSelectedCityName.equals(theCityHasSet)) {
                        mAimCityName = mSelectedCityName;
                        processCityWeather(mSelectedCityName + "," + provinceByCity);
                        finishPage();
                    }
                }
            }
        });
    }

    private TextWatcher cnSearch_TextChanged = new TextWatcher() {

        private boolean isNullBefore = true;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mHotCitiesLayout.setVisibility(View.GONE);
            mPossibleCitiesLayout.setVisibility(View.VISIBLE);
            myHandler.post(cityDataChanged);
            mCityNameReceived = s.toString().trim();
            if (mCityNameReceived.length() >= 10) {
                Toast.makeText(ChooseCityActivity.this, R.string.choose_city_max_length_tip,
                        Toast.LENGTH_SHORT).show();
            }
            if (TextUtils.isEmpty(mCityNameReceived)) {
                isNullBefore = true;
                mPossibleCityList.clear();
                mPossibleCitiesLayout.setVisibility(View.GONE);
                mHotCitiesLayout.setVisibility(View.VISIBLE);
            } else {
                isNullBefore = false;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (TextUtils.isEmpty(s)) {
                mPossibleCitiesLayout.setVisibility(View.GONE);
                isNullBefore = true;
                myHandler.post(cityDataChanged);
            } else {
                mCityNameReceived = s.toString().trim();
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("-after--cityNameReceived--->>" + mCityNameReceived);
                }
                if (!TextUtils.isEmpty(mCityNameReceived)) {
                    mPossibleCitiesLayout.setVisibility(View.VISIBLE);
                    myHandler.post(cityDataChanged);
                    List<String> cityAndProvinceNameList = XMLParserUtils
                            .getCityAndProvinceNameList();
                    if (!cityAndProvinceNameList.contains(mCityNameReceived)) {
                        if (!isNullBefore) {
                            mPossibleCitiesLayout.setVisibility(View.VISIBLE);
                            mPossibleCitiesListView.setVisibility(View.GONE);
                        } else {
                            isNullBefore = false;
                        }
                    }
                }
            }
        }
    };

    private List<String> getPossibleCityList(String cityNameStr) {
        if (!TextUtils.isEmpty(cityNameStr)) {
            List<CityInfo> locationCityInfos = XMLParserUtils.getLocationCityInfos(cityNameStr);
            mPossibleCityList = new ArrayList<String>();
            for (CityInfo cityInfo : locationCityInfos) {
                String cityName = cityInfo.getCityName();
                String cityProvince = cityInfo.getCityProvince();
                mPossibleCityList.add(cityName + "," + cityProvince);
            }
        }
        return mPossibleCityList;
    }

    /**
     * 获取指定城市天气
     * 
     * @param cityNameStr
     */
    public void processCityWeather(final String aimCityStr) {
        String[] split = aimCityStr.split(",");
        final String cityStr = split[0];
        final String provinceStr = split[1];
        PandoraWeatherManager.getInstance().getWeatherFromNetwork(new ISmartWeatherCallback() {

            @Override
            public void onSuccess(SmartWeatherInfo smartWeatherInfo) {
                if (smartWeatherInfo != null) {
                    LockScreenManager.getInstance().updateWeatherView(smartWeatherInfo);
                    // mPandoraConfig.saveLastCityName(cityStr);
                    // mPandoraConfig.saveLastCityProvinceName(provinceStr);
                }
            }

            @Override
            public void onFailure() {
            }
        });

    }

    class MyGridViewAdapter extends BaseAdapter {

        private void bindData(String[] cityNameStrings) {
            mGVCityNameList = new ArrayList<String>();
            for (String cityName : cityNameStrings) {
                if (!TextUtils.isEmpty(cityName)) {
                    mGVCityNameList.add(cityName);
                }
            }
        }

        @Override
        public int getCount() {
            return mGVCityNameList.size();
        }

        @Override
        public Object getItem(int position) {
            return mGVCityNameList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View retView = null;
            ViewHolder holder = null;
            if (convertView == null) {
                retView = LayoutInflater.from(ChooseCityActivity.this).inflate(
                        R.layout.choose_city_gridview_item, parent, false);
                holder = new ViewHolder();
                holder.hotCitiesName = (TextView) retView.findViewById(R.id.tvhotcities);
                holder.imgSelectState = (ImageView) retView.findViewById(R.id.iv_is_selected);
                retView.setTag(holder);
            } else {
                retView = convertView;
                holder = (ViewHolder) retView.getTag();
            }
            holder.hotCitiesName.setText(mGVCityNameList.get(position));
            String selectedHotCityPosition = mPandoraConfig.getSelectedHotCityPosition();
            if (Integer.parseInt(selectedHotCityPosition) == position) {
                holder.imgSelectState.setVisibility(View.VISIBLE);
            } else {
                holder.imgSelectState.setVisibility(View.GONE);
            }
            return retView;
        }

        class ViewHolder {
            private TextView hotCitiesName;

            private ImageView imgSelectState;
        }
    }

    @Override
    public void onBackPressed() {
        finishPage();
    }

    private void finishPage() {
        Intent dataIntent = new Intent();
        dataIntent.putExtra("cityNameChosen", mAimCityName);
        setResult(Activity.RESULT_OK, dataIntent);
        finish();
        overridePendingTransition(R.anim.umeng_fb_slide_in_from_left,
                R.anim.umeng_fb_slide_out_from_right);
    }
}
