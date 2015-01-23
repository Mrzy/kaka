
package cn.zmdx.kaka.fast.locker.shortcut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.zmdx.kaka.fast.locker.LockScreenManager;
import cn.zmdx.kaka.fast.locker.R;
import cn.zmdx.kaka.fast.locker.database.ShortcutModel;
import cn.zmdx.kaka.fast.locker.settings.ShortcutSettingsActivity;
import cn.zmdx.kaka.fast.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.fast.locker.widget.dragdropgridview.DragDropGrid;

public class ShortcutManager {

    private Context mContext;

    private DragDropGrid mGridView;

    private static ShortcutManager INSTANCE;

    private LayoutInflater mInflater;

    private ShortcutManager(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        // test
        // initTestData();
    }

    // test method
    private void initTestData() {
        List<AppInfo> data = new ArrayList<AppInfo>();

        AppInfo ai = new AppInfo();
        ai.setAppName("weixin");
        String pkgName = "com.tencent.mm";
        ai.setPkgName(pkgName);
        ai.setDefaultIcon(AppInfo.getIconByPkgName(mContext, pkgName));
        ai.setDisguise(false);
        ai.setPosition(1);
        ai.setShortcut(true);
        data.add(ai);

        AppInfo ai1 = new AppInfo();
        ai1.setAppName("QQ");
        String pkgName1 = "com.tencent.mobileqq";
        ai1.setPkgName(pkgName1);
        ai1.setDefaultIcon(AppInfo.getIconByPkgName(mContext, pkgName1));
        ai1.setDisguise(false);
        ai1.setPosition(4);
        ai1.setShortcut(true);
        data.add(ai1);

        AppInfo ai2 = new AppInfo();
        ai2.setAppName("腾讯新闻");
        String pkgName2 = "com.tencent.news";
        ai2.setPkgName(pkgName2);
        ai2.setDefaultIcon(AppInfo.getIconByPkgName(mContext, pkgName2));
        ai2.setDisguise(false);
        ai2.setPosition(3);
        ai2.setShortcut(true);
        data.add(ai2);

        // AppInfo ai3 = new AppInfo();
        // ai3.setAppName("拨号");
        // String pkgName3 = "com.tencent.pb";
        // ai3.setPkgName(pkgName3);
        // ai3.setDefaultIcon(AppInfo.getIconByPkgName(mContext, pkgName3));
        // ai3.setDisguise(false);
        // ai3.setPosition(2);
        // ai3.setShortcut(true);
        // data.add(ai3);

        AppInfo ai4 = new AppInfo();
        ai4.setAppName("Chrome");
        String pkgName4 = "com.android.chrome";
        ai4.setPkgName(pkgName4);
        ai4.setDefaultIcon(AppInfo.getIconByPkgName(mContext, pkgName4));
        ai4.setDisguise(false);
        ai4.setPosition(0);
        ai4.setShortcut(true);
        data.add(ai4);

        AppInfo ai5 = new AppInfo();
        ai5.setAppName("优酷");
        String pkgName5 = "com.youku.phone";
        ai5.setPkgName(pkgName5);
        ai5.setDefaultIcon(AppInfo.getIconByPkgName(mContext, pkgName5));
        ai5.setDisguise(false);
        ai5.setPosition(5);
        ai5.setShortcut(true);
        data.add(ai5);
        saveShortcutInfo(data);
    }

    public static synchronized ShortcutManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ShortcutManager(context);
        }
        return INSTANCE;
    }

    public View createShortcutAppsView() {
        View view = mInflater.inflate(R.layout.tool_box_layout, null);
        // test
        view.findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent in = new Intent(mContext, ShortcutSettingsActivity.class);
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(in);
                LockScreenManager.getInstance().unLock();
            }
        });
        mGridView = (DragDropGrid) view.findViewById(R.id.gridView);
        ViewGroup.LayoutParams lp = mGridView.getLayoutParams();
        lp.height = BaseInfoHelper.getRealWidth(mContext) / 3 * 2;
        List<AppInfo> data = getShortcutInfo();
        final ShortcutAppAdapter adapter = new ShortcutAppAdapter(mContext, mGridView, data);
        mGridView.setAdapter(adapter);
        return view;
    }

    /**
     * 获取6个快捷入口的集合，如果入口不足6个，会用假的AppInfo对象填充，返回的集合根据AppInfo的position升序排列
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<AppInfo> getShortcutInfo() {
        List<AppInfo> data = ShortcutModel.getInstance(mContext).queryAll();
        List<AppInfo> tmpList = new ArrayList<AppInfo>();
        for (int pos = 0; pos < 6; pos++) {
            boolean posExsit = false;
            for (int i = 0; i < data.size(); i++) {
                if (pos == data.get(i).getPosition()) {
                    posExsit = true;
                }
            }
            if (!posExsit) {
                AppInfo ai = new AppInfo();
                ai.setPosition(pos);
                tmpList.add(ai);
            }
        }
        data.addAll(tmpList);
        Collections.sort(data);
        return data;
    }

    /**
     * @param data
     */
    public void saveShortcutInfo(List<AppInfo> data) {
        if (data == null || data.size() <= 0) {
            return;
        }
        ShortcutModel sm = ShortcutModel.getInstance(mContext);
        sm.deleteAll();
        for (AppInfo ai : data) {
            if (!TextUtils.isEmpty(ai.getPkgName())) {
                boolean result = sm.insert(ai);
            }
        }
    }
}