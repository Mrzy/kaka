
package cn.zmdx.kaka.fast.locker.shortcut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import cn.zmdx.kaka.fast.locker.BuildConfig;
import cn.zmdx.kaka.fast.locker.LockScreenManager;
import cn.zmdx.kaka.fast.locker.R;
import cn.zmdx.kaka.fast.locker.utils.HDBLOG;
import cn.zmdx.kaka.fast.locker.widget.dragdropgridview.DragDropGrid;
import cn.zmdx.kaka.fast.locker.widget.dragdropgridview.PagedDragDropGridAdapter;

public class ShortcutAppAdapter implements PagedDragDropGridAdapter {

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

    public ShortcutAppAdapter(Context context, DragDropGrid grid, List<AppInfo> data) {
        this(context, grid, data, MODE_23);
    }

    public ShortcutAppAdapter(Context context, DragDropGrid grid, List<AppInfo> data, int mode) {
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

    private void startTarget(String pkgName) {
        if (TextUtils.isEmpty(pkgName)) {
            return;
        }
        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(pkgName);
        if (intent != null) {
            mContext.startActivity(intent);
            LockScreenManager.getInstance().unLock();
        }
    }

    @Override
    public View view(int page, final int index) {
        View view = mInflater.inflate(R.layout.shortcut_item_layout, null);
        ImageView iv = (ImageView) view.findViewById(R.id.shortcut_icon);
        if (index == mData.get(index).getPosition()) {
            iv.setImageDrawable(mData.get(index).getDefaultIcon());
        }
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.getTag() instanceof AppInfo) {
                    AppInfo ai = (AppInfo) v.getTag();
                    String pkgName = ai.getPkgName();
                    startTarget(pkgName);
                }
            }
        });
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
