
package cn.zmdx.kaka.fast.locker.shortcut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.View;
import cn.zmdx.kaka.fast.locker.database.ShortcutModel;
import cn.zmdx.kaka.fast.locker.widget.dragdropgridview.DragDropGrid;

public class ShortcutManager {

    private Context mContext;

    private DragDropGrid mGridView;

    private static ShortcutManager INSTANCE;

    private ShortcutManager(Context context) {
        mContext = context;
        // test
        initTestData();
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

        AppInfo ai3 = new AppInfo();
        ai3.setAppName("拨号");
        String pkgName3 = "com.tencent.pb";
        ai3.setPkgName(pkgName3);
        ai3.setDefaultIcon(AppInfo.getIconByPkgName(mContext, pkgName3));
        ai3.setDisguise(false);
        ai3.setPosition(2);
        ai3.setShortcut(true);
        data.add(ai3);

        AppInfo ai4 = new AppInfo();
        ai4.setAppName("Chrome");
        String pkgName4 = "com.android.chrome";
        ai4.setPkgName(pkgName4);
        ai4.setDefaultIcon(AppInfo.getIconByPkgName(mContext, pkgName4));
        ai4.setDisguise(false);
        ai4.setPosition(6);
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
        mGridView = new DragDropGrid(mContext);
//        mGridView.setOnClickListener(mGridClickListener);
        final ShortcutAppAdapter adapter = new ShortcutAppAdapter(mContext, mGridView,
                getShortcutInfo());
        mGridView.setAdapter(adapter);
        return mGridView;
    }

    private View.OnClickListener mGridClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // Toast.makeText(mContext, "click,v=" + v,
            // Toast.LENGTH_SHORT).show();
        }
    };

    @SuppressWarnings("unchecked")
    public List<AppInfo> getShortcutInfo() {
        List<AppInfo> data = ShortcutModel.getInstance(mContext).queryAll();
        if (data != null && data.size() > 0) {
            Collections.sort(data);
        }
        return data;
    }

    /**
     * @param data
     */
    public void saveShortcutInfo(List<AppInfo> data) {
        ShortcutModel sm = ShortcutModel.getInstance(mContext);
        sm.deleteAll();
        for (AppInfo ai : data) {
            boolean result = sm.insert(ai);
        }
    }
}
