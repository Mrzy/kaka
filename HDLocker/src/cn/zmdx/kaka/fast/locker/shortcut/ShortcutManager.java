
package cn.zmdx.kaka.fast.locker.shortcut;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.Toast;
import cn.zmdx.kaka.fast.locker.R;
import cn.zmdx.kaka.fast.locker.widget.dragdropgridview.DragDropGrid;

public class ShortcutManager {

    private Context mContext;

    private DragDropGrid mGridView;

    private static ShortcutManager INSTANCE;

    private ShortcutManager(Context context) {
        mContext = context;
    }

    public static ShortcutManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ShortcutManager(context);
        }
        return INSTANCE;
    }

    public View createShortcutAppsView() {
        mGridView = new DragDropGrid(mContext);
        mGridView.setOnClickListener(mGridClickListener);
        final ShortcutAppAdapter adapter = new ShortcutAppAdapter(mContext, mGridView,
                getShortcutInfo());
        mGridView.setAdapter(adapter);
        return mGridView;
    }

    private View.OnClickListener mGridClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Toast.makeText(mContext, "click,v=" + v, Toast.LENGTH_SHORT).show();
        }
    };

    public List<AppInfo> getShortcutInfo() {
        List<AppInfo> data = new ArrayList<AppInfo>();
        for (int i = 0; i < 6; i++) {
            AppInfo ai = new AppInfo();
            ai.setAppName("微信" + i);
            ai.setPkgName("weixin");
            ai.setDefaultIcon(mContext.getResources().getDrawable(R.drawable.ic_launcher));
            ai.setShortcut(true);
            ai.setDisguise(false);
            data.add(ai);
        }
        return data;
    }
}
