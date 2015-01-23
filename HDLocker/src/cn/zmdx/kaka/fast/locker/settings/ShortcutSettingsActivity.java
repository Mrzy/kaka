
package cn.zmdx.kaka.fast.locker.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import cn.zmdx.kaka.fast.locker.BuildConfig;
import cn.zmdx.kaka.fast.locker.R;
import cn.zmdx.kaka.fast.locker.shortcut.AppInfo;
import cn.zmdx.kaka.fast.locker.shortcut.ShortcutManager;
import cn.zmdx.kaka.fast.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.fast.locker.utils.HDBLOG;
import cn.zmdx.kaka.fast.locker.widget.RippleView;
import cn.zmdx.kaka.fast.locker.widget.RippleView.Callback;
import cn.zmdx.kaka.fast.locker.widget.dragdropgridview.DragDropGrid;
import cn.zmdx.kaka.fast.locker.widget.dragdropgridview.DragDropGrid.OnItemClickListener;
import cn.zmdx.kaka.fast.locker.widget.dragdropgridview.PagedDragDropGridAdapter;

public class ShortcutSettingsActivity extends BaseActivity {

    private DragDropGrid mGridView;

    private List<AppInfo> mData;

    private ShortcutSettingsAdapter mAdapter;

    private ViewGroup mOuterView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shortcut_settings_layout);
        initShortcutView();
    }

    private void initShortcutView() {
        mOuterView = (ViewGroup) findViewById(R.id.outerView);
        if (mGridView != null) {
            mOuterView.removeView(mGridView);
        }
        mGridView = new DragDropGrid(this);
        mGridView.setAllowLongClick(false);
        mOuterView.addView(mGridView);
        ViewGroup.LayoutParams lp = mGridView.getLayoutParams();
        lp.height = BaseInfoHelper.getRealWidth(this) / 3 * 2;
        mData = ShortcutManager.getInstance(this).getShortcutInfo();
        mAdapter = new ShortcutSettingsAdapter(this, mGridView, mData);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(View clickedView, final int index, MotionEvent event) {
                    if (clickedView instanceof RippleView) {
                        RippleView rv = (RippleView) clickedView;
                        rv.animateRipple(event, new Callback() {

                            @Override
                            public void onFinish(View v) {
                                startSettingsActivity(index);
                            }
                        });
                        return;
                    }
                    startSettingsActivity(index);
            }
        });
    }

    private void invalidate() {
        initShortcutView();
    }

    private void startSettingsActivity(int position) {
         Intent intent = new Intent(this, NotifyFilterActivity.class);
         intent.putExtra("position", position);
         intent.putExtra("type", NotifyFilterActivity.TYPE_SELECT);
         startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                int position = bundle.getInt("position");
                String pkgName = bundle.getString("pkgName");
                addShortcut(pkgName, position);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addShortcut(String pkgName, int position) {
        if (TextUtils.isEmpty(pkgName)) {
            return;
        }
        AppInfo ai = AppInfo.createAppInfo(this, pkgName, false, null, position);
        mData.add(ai);
        ShortcutManager.getInstance(this).saveShortcutInfo(mData);
        invalidate();
    }

    static class ShortcutSettingsAdapter implements PagedDragDropGridAdapter {

        private Context mContext;

        private DragDropGrid mGrid;

        private List<AppInfo> mData = new ArrayList<AppInfo>();

        private boolean mOrderChanged = false;

        public static final int MODE_33 = 1;

        public static final int MODE_24 = 2;

        public static final int MODE_23 = 3;

        public static final int MODE_22 = 4;

        private int mCurMode = MODE_23;

        private LayoutInflater mInflater;

        public ShortcutSettingsAdapter(Context context, DragDropGrid grid, List<AppInfo> data) {
            this(context, grid, data, MODE_23);
        }

        public ShortcutSettingsAdapter(Context context, DragDropGrid grid, List<AppInfo> data,
                int mode) {
            mContext = context;
            mGrid = grid;
            mData = data;
            mCurMode = mode;
            mInflater = LayoutInflater.from(context);
        }

        public List<AppInfo> getData() {
            return mData;
        }

        private int getRowCountByMode(int mode) {
            if (mode == MODE_33) {
                return 3;
            } else if (mode == MODE_24 || mode == MODE_23 || mode == MODE_22) {
                return 2;
            } else {
                // default
                return 2;
            }
        }

        public void setMode(int mode) {
            if (mode >= 1 && mode <= 4) {
                mCurMode = mode;
            } else {
                if (BuildConfig.DEBUG) {
                    HDBLOG.logE("error mode，use default mode MODE_33");
                }
            }
        }

        public int getMode() {
            return mCurMode;
        }

        private int getColumnCountByMode(int mode) {
            if (mode == MODE_33 || mode == MODE_23) {
                return 3;
            } else if (mode == MODE_24) {
                return 4;
            } else if (mode == MODE_22) {
                return 2;
            } else {
                // default
                return 3;
            }
        }

        @Override
        public int pageCount() {
            return 1;
        }

        @Override
        public int itemCountInPage(int page) {
            return mData.size();
        }

        @Override
        public View view(int page, final int index) {
            View view = mInflater.inflate(R.layout.shortcut_item_layout, null);
            ImageView iv = (ImageView) view.findViewById(R.id.shortcut_icon);
            AppInfo ai = mData.get(index);
            if (index == mData.get(index).getPosition()) {
                iv.setImageDrawable(mData.get(index).getDefaultIcon());
            }
            return view;
        }

        @Override
        public int rowCount() {
            return getRowCountByMode(mCurMode);
        }

        @Override
        public int columnCount() {
            return getColumnCountByMode(mCurMode);
        }

        @Override
        public void printLayout() {

        }

        @Override
        public void swapItems(int pageIndex, int itemIndexA, int itemIndexB) {
            // swap position
            int posiA = mData.get(itemIndexA).getPosition();
            mData.get(itemIndexA).setPosition(mData.get(itemIndexB).getPosition());
            mData.get(itemIndexB).setPosition(posiA);

            Collections.swap(mData, itemIndexA, itemIndexB);
            mOrderChanged = true;
        }

        @Override
        public void moveItemToPreviousPage(int pageIndex, int itemIndex) {

        }

        @Override
        public void moveItemToNextPage(int pageIndex, int itemIndex) {

        }

        @Override
        public void deleteItem(int pageIndex, int itemIndex) {
            mData.remove(itemIndex);

        }

        @Override
        public int deleteDropZoneLocation() {
            return BOTTOM;
        }

        @Override
        public boolean showRemoveDropZone() {
            return false;
        }

        @Override
        public int getPageWidth(int page) {
            return 0;
        }

        @Override
        public Object getItemAt(int page, int index) {
            return mData.get(index);
        }

        @Override
        public boolean disableZoomAnimationsOnChangePage() {
            return true;
        }

        @Override
        public void onFinish() {
            // 如果应用顺序被改变，需要将其保存
            if (mOrderChanged) {
                ShortcutManager.getInstance(mContext).saveShortcutInfo(mData);
            }
        }
    }
}