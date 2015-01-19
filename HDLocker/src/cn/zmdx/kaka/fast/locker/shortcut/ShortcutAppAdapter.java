package cn.zmdx.kaka.fast.locker.shortcut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import cn.zmdx.kaka.fast.locker.widget.dragdropgridview.DragDropGrid;
import cn.zmdx.kaka.fast.locker.widget.dragdropgridview.PagedDragDropGridAdapter;

public class ShortcutAppAdapter implements PagedDragDropGridAdapter {

    private Context mContext;

    private DragDropGrid mGrid;

    private List<AppInfo> mData = new ArrayList<AppInfo>();

    public ShortcutAppAdapter(Context context, DragDropGrid grid, List<AppInfo> data) {
        mContext = context;
        mGrid = grid;
        mData = data;
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
        ImageView iv = new ImageView(mContext);
        iv.setImageDrawable(mData.get(index).getDefaultIcon());
        iv.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Log.e("zy", "mData.name:" + mData.get(index).getAppName());
            }
        });
        iv.setOnLongClickListener(new View.OnLongClickListener() {
            
            @Override
            public boolean onLongClick(View v) {
                return mGrid.onLongClick(v);
            }
        });
        return iv;
    }

    @Override
    public int rowCount() {
        return 4;
    }

    @Override
    public int columnCount() {
        return 3;
    }

    @Override
    public void printLayout() {

    }

    @Override
    public void swapItems(int pageIndex, int itemIndexA, int itemIndexB) {
        Collections.swap(mData, itemIndexA, itemIndexB);
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
}
