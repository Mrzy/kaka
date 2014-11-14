
package cn.zmdx.kaka.locker.content;

import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.content.box.DefaultBox;
import cn.zmdx.kaka.locker.content.box.FoldableBoxAdapter;
import cn.zmdx.kaka.locker.content.box.FoldablePage;
import cn.zmdx.kaka.locker.content.box.IFoldableBox;
import cn.zmdx.kaka.locker.content.box.IPandoraBox;
import cn.zmdx.kaka.locker.content.box.IPandoraBox.PandoraData;
import cn.zmdx.kaka.locker.database.ServerImageDataModel;
import cn.zmdx.kaka.locker.policy.PandoraPolicy;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;

public class PandoraBoxManager {

    private static PandoraBoxManager mPbManager;

    private Context mContext;

    private PandoraBoxManager(Context context) {
        mContext = context;
    }

    public synchronized static PandoraBoxManager newInstance(Context context) {
        if (mPbManager == null) {
            mPbManager = new PandoraBoxManager(context);
        }
        return mPbManager;
    }

    public IFoldableBox getFoldableBox() {
        boolean containHtml = HDBNetworkState.isNetworkAvailable()
                && HDBNetworkState.isWifiNetwork();
        List<ServerImageData> data = ServerImageDataModel.getInstance().queryByRandom(
                PandoraPolicy.MIN_COUNT_FOLDABLE_BOX, containHtml);
        if (BuildConfig.DEBUG) {
            HDBLOG.logD("从本地取出数据条数：" + data.size());
        }
        FoldablePage box = new FoldablePage(mContext, data);
        FoldableBoxAdapter adapter = new FoldableBoxAdapter(mContext, box.makeCardList(data));
        box.setAdapter(adapter);
        return box;
    }

    public IPandoraBox getDefaultBox() {
        if (BuildConfig.DEBUG) {
            HDBLOG.logD("获得默认页面");
        }
        PandoraData pd = new PandoraData();
        pd.setmImage(BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.pandora_box_default));
        pd.setDataType("DEFAULT_PAGE");
        return new DefaultBox(mContext, pd);
    }
}
