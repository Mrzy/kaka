
package cn.zmdx.kaka.fast.locker.shortcut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import cn.zmdx.kaka.fast.locker.HDApplication;
import cn.zmdx.kaka.fast.locker.LockScreenManager;
import cn.zmdx.kaka.fast.locker.LockScreenManager.IPullDownListener;
import cn.zmdx.kaka.fast.locker.R;
import cn.zmdx.kaka.fast.locker.database.ShortcutModel;
import cn.zmdx.kaka.fast.locker.settings.ShortcutSettingsActivity;
import cn.zmdx.kaka.fast.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.fast.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.fast.locker.shortcut.sevenkey.QuickHelperItem;
import cn.zmdx.kaka.fast.locker.shortcut.sevenkey.ToolbarAdapter;
import cn.zmdx.kaka.fast.locker.shortcut.sevenkey.WidgetConfig;
import cn.zmdx.kaka.fast.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.fast.locker.widget.dragdropgridview.DragDropGrid;

public class ShortcutManager {

    private Context mContext;

    private DragDropGrid mGridView;

    private GridView mToolbarView;

    private static ShortcutManager INSTANCE;

    private LayoutInflater mInflater;

    private ToolbarAdapter mAdapter;

    private List<QuickHelperItem> mSwitchList = new ArrayList<QuickHelperItem>();

    private ShortcutManager(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        initToolbarData();
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
        ViewGroup view = (ViewGroup) mInflater.inflate(R.layout.tool_box_layout, null);
        mGridView = (DragDropGrid) view.findViewById(R.id.gridView);
        ViewGroup.LayoutParams lp = mGridView.getLayoutParams();
        lp.height = BaseInfoHelper.getRealWidth(mContext) / 3 * 2;
        List<AppInfo> data = getShortcutInfo();
        final ShortcutAppAdapter adapter = new ShortcutAppAdapter(mContext, mGridView, data);
        mGridView.setAdapter(adapter);

        initToolbar(view);

        initToolBoxGuideView(view);

        return view;
    }

    private void initToolbar(View view) {
        mToolbarView = (GridView) view.findViewById(R.id.tool_bar);
        mAdapter = new ToolbarAdapter(mContext, mSwitchList);
        mToolbarView.setAdapter(mAdapter);
        // mToolbarView.setOnItemClickListener(mToolbarItemClickListener);
    }

    private OnItemClickListener mToolbarItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            UmengCustomEventManager.statisticalClickToolIcon();
            final QuickHelperItem item = (QuickHelperItem) mAdapter.getItem(position);
            if (item == null) {
                return;
            }
            switch (item.type) {
                case QuickHelperItem.TYPE_SWITCH:
                    updateQuickSwitch(item);
                    break;
            }
        }
    };

    private View mGuideView;

    private static final int GUIDE_ANIMATOR_Y_OFFSET = BaseInfoHelper.dip2px(
            HDApplication.getContext(), 60);

    private void initToolBoxGuideView(final ViewGroup view) {
        if (PandoraConfig.newInstance(mContext).isShotcutGuided()) {
            return;
        }
        mGuideView = mInflater.inflate(R.layout.tool_box_guide_layout, null);
        mGuideView.setVisibility(View.GONE);
        view.addView(mGuideView, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,
                GUIDE_ANIMATOR_Y_OFFSET));
        mGuideView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dismissGuideWithAnimator();
                // view.removeView(mGuideView);
                PandoraConfig.newInstance(mContext).saveShotcutGuided();
            }
        });
        mGuideView.findViewById(R.id.rightNowSettings).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        LockScreenManager.getInstance().setRunnableAfterUnLock(new Runnable() {

                            @Override
                            public void run() {
                                Intent intent = new Intent(mContext, ShortcutSettingsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(intent);
                            }
                        });
                        LockScreenManager.getInstance().unLock();
                        PandoraConfig.newInstance(mContext).saveShotcutGuided();
                    }
                });
        mGuideView.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {

            @Override
            public void onViewDetachedFromWindow(View v) {
                LockScreenManager.getInstance().unRegistPullDownListener(mPullDownListener);
            }

            @Override
            public void onViewAttachedToWindow(View v) {
                LockScreenManager.getInstance().registPullDownListener(mPullDownListener);
            }
        });
    }

    private IPullDownListener mPullDownListener = new IPullDownListener() {

        @Override
        public void onStartPullDown() {

        }

        @Override
        public void onCollapsed() {
            showGuideWithAnimator();
        }
    };

    public void showGuideWithAnimator() {
        if (mGuideView == null) {
            return;
        }
        mGuideView.setTranslationY(-GUIDE_ANIMATOR_Y_OFFSET);
        mGuideView.setVisibility(View.VISIBLE);
        mGuideView.animate().translationY(0).setDuration(500)
                .setInterpolator(new AccelerateInterpolator()).start();
    }

    public void dismissGuideWithAnimator() {
        if (mGuideView == null) {
            return;
        }
        mGuideView.animate().translationY(-GUIDE_ANIMATOR_Y_OFFSET).setDuration(500)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mGuideView.setVisibility(View.GONE);
                    }
                }).start();
    }

    private void updateQuickSwitch(QuickHelperItem item) {
        item.tracker.toggleState(mContext, null, null);
        mAdapter.notifyDataSetChanged();
    }

    private void initToolbarData() {
        mSwitchList.add(new QuickHelperItem(QuickHelperItem.TYPE_SWITCH,
                WidgetConfig.SWITCH_ID_WIFI));
        // mSwitchList
        // .add(new QuickHelperItem(QuickHelperItem.TYPE_SWITCH,
        // WidgetConfig.SWITCH_ID_APN));
        mSwitchList.add(new QuickHelperItem(QuickHelperItem.TYPE_SWITCH,
                WidgetConfig.SWITCH_ID_SOUND));
        mSwitchList.add(new QuickHelperItem(QuickHelperItem.TYPE_SWITCH,
                WidgetConfig.SWITCH_ID_BRIGHTNESS));
        // mSwitchList.add(new QuickHelperItem(QuickHelperItem.TYPE_SWITCH,
        // WidgetConfig.SWITCH_ID_AIRPLANE));
        mSwitchList.add(new QuickHelperItem(QuickHelperItem.TYPE_SWITCH,
                WidgetConfig.SWITCH_ID_SETTINGS));
    }

    /**
     * 获取6个快捷入口的集合，如果入口不足6个，会用假的AppInfo对象填充，返回的集合根据AppInfo的position升序排列
     * 
     * @return
     */
    public List<AppInfo> getShortcutInfo() {
        List<AppInfo> data = ShortcutModel.getInstance(mContext).queryAll();
        if (data.size() == 0 && PandoraConfig.newInstance(mContext).isShutcutDefaultEnabled()) {
            data = ShortcutUtil.initDefaultShotrcutInfo(mContext);
            saveShortcutInfo(data);
            PandoraConfig.newInstance(mContext).saveShutcutDefaultEnabled(false);
        }
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

    public boolean delShortcutInfo(String pkgName) {
        ShortcutModel sm = ShortcutModel.getInstance(mContext);
        return sm.deleteByPkgName(pkgName) > 0 ? true : false;
    }

    public boolean isExsitShortcut(String pkgName) {
        ShortcutModel sm = ShortcutModel.getInstance(mContext);
        return sm.existByPkgName(pkgName);
    }

    private SparseIntArray mCacheSparseArray = null;

    public static int KEY_LAYOUT_SIZE = 0;

    public static int KEY_IMAGE_SIZE = 1;

    public SparseIntArray initShortcutLayoutSize() {
        if (null == mCacheSparseArray || mCacheSparseArray.size() == 0) {
            int screenWidth = BaseInfoHelper.getRealWidth(mContext);
            int parentSize = (int) (screenWidth / 3);
            int layoutSize = parentSize / 2 + 10;
            int imageSize = parentSize / 2;
            mCacheSparseArray = new SparseIntArray();
            mCacheSparseArray.put(KEY_LAYOUT_SIZE, layoutSize);
            mCacheSparseArray.put(KEY_IMAGE_SIZE, imageSize);
        }
        return mCacheSparseArray;
    }
}
