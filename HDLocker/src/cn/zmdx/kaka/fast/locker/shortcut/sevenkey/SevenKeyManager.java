
package cn.zmdx.kaka.fast.locker.shortcut.sevenkey;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;

public class SevenKeyManager {

    private List<QuickHelperItem> mSwitchList = new ArrayList<QuickHelperItem>();

    private ToolbarAdapter mAdapter;

    private Context mContext;
    public SevenKeyManager(Context context) {
        initSwitchData();
        mContext = context;
    }

    public View createSevenKeyView() {
        ToolbarView tlv = new ToolbarView(mContext);
        return null;
    }

    private void initSwitchData() {
        mSwitchList.add(new QuickHelperItem(QuickHelperItem.TYPE_SWITCH,
                WidgetConfig.SWITCH_ID_WIFI));
        mSwitchList
                .add(new QuickHelperItem(QuickHelperItem.TYPE_SWITCH, WidgetConfig.SWITCH_ID_APN));
        mSwitchList.add(new QuickHelperItem(QuickHelperItem.TYPE_SWITCH,
                WidgetConfig.SWITCH_ID_SOUND));
        mSwitchList.add(new QuickHelperItem(QuickHelperItem.TYPE_SWITCH,
                WidgetConfig.SWITCH_ID_BRIGHTNESS));
    }
}
