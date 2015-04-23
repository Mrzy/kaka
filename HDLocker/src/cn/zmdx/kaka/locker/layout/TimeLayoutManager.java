
package cn.zmdx.kaka.locker.layout;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.layout.generator.BaseLayoutGenerator;
import cn.zmdx.kaka.locker.layout.generator.LayoutGenerator1;
import cn.zmdx.kaka.locker.layout.generator.LayoutGenerator2;
import cn.zmdx.kaka.locker.layout.generator.LayoutGenerator3;
import cn.zmdx.kaka.locker.layout.generator.LayoutGenerator4;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;

public class TimeLayoutManager {

    private static TimeLayoutManager INSTANCE;

    public static final int LAYOUT_ID1 = 1;

    public static final int LAYOUT_ID2 = 2;

    public static final int LAYOUT_ID3 = 3;

    public static final int LAYOUT_ID4 = 4;

    public static final int LAYOUT_POSITION1 = 1;

    public static final int LAYOUT_POSITION2 = 2;

    public static final int LAYOUT_POSITION3 = 3;

    public static final int LAYOUT_POSITION4 = 4;

    private String[] mFontUrls;

    private Context mContext;

    private TimeLayoutManager(Context context) {
        mContext = context;
        mFontUrls = new String[] {
                "http://cos.myqcloud.com/11000436/font/TTF/HelveticaNeue-Thin.otf",
                "http://cos.myqcloud.com/11000436/font/TTF/Eurostile.otf",
                "http://cos.myqcloud.com/11000436/font/TTF/CenturyGothic.ttf",
                "http://cos.myqcloud.com/11000436/font/TTF/Haettenschweiler.ttf"
        };
    }

    public static TimeLayoutManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new TimeLayoutManager(context);
        }
        return INSTANCE;
    }

    public int getCurrentLayout() {
        return PandoraConfig.newInstance(mContext).getCurrentLayout();
    }

    public void saveCurrentLayout(int layoutId) {
        PandoraConfig.newInstance(mContext).saveCurrentLayout(layoutId);
    }

    private BaseLayoutGenerator mLayoutGenerator;

    public View createLayoutViewByID(int layoutId) {
        switch (layoutId) {
            case LAYOUT_ID1:
                mLayoutGenerator = new LayoutGenerator1(mContext);
                break;
            case LAYOUT_ID2:
                mLayoutGenerator = new LayoutGenerator2(mContext);
                break;
            case LAYOUT_ID3:
                mLayoutGenerator = new LayoutGenerator3(mContext);
                break;
            case LAYOUT_ID4:
                mLayoutGenerator = new LayoutGenerator4(mContext);
                break;
        }
        return mLayoutGenerator != null ? mLayoutGenerator.getView() : null;
    }

    public void updateWeather() {
        mLayoutGenerator.updateWeather();
    }

    public List<LayoutInfo> getAllLayout() {
        List<LayoutInfo> list = new ArrayList<LayoutInfo>();
        LayoutInfo li;
        li = new LayoutInfo();
        li.setLayoutId(LAYOUT_ID1);
        li.setCoverResId(R.drawable.layoutpage_time1);
        li.setPosition(LAYOUT_POSITION1);
        li.setFontUrl(mFontUrls[0]);
        list.add(li);

        li = new LayoutInfo();
        li.setLayoutId(LAYOUT_ID2);
        li.setCoverResId(R.drawable.layoutpage_time2);
        li.setPosition(LAYOUT_POSITION2);
        li.setFontUrl(mFontUrls[1]);
        list.add(li);

        li = new LayoutInfo();
        li.setLayoutId(LAYOUT_ID3);
        li.setCoverResId(R.drawable.layoutpage_time3);
        li.setPosition(LAYOUT_POSITION3);
        li.setFontUrl(mFontUrls[2]);
        list.add(li);

        li = new LayoutInfo();
        li.setLayoutId(LAYOUT_ID4);
        li.setCoverResId(R.drawable.layoutpage_time4);
        li.setPosition(LAYOUT_POSITION4);
        li.setFontUrl(mFontUrls[3]);
        list.add(li);
        return list;
    }
}
