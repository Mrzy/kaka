
package cn.zmdx.kaka.locker.content;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.BaiduDataManager.BaiduData;
import cn.zmdx.kaka.locker.content.IPandoraBox.PandoraData;
import cn.zmdx.kaka.locker.database.BaiduDataModel;

public class PandoraBoxManager {

    public static final int DATA_FROM_DEFAULT = 1;

    public static final int DATA_FROM_BAIDU = 2;

    public static final int DATA_FROM_CAODAN = 3;

    public static final int DATA_FROM_QIUBAI = 4;

    public static final int DATA_FROM_JOKE = 5;

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

    public IPandoraBox getNextPandoraData() {
        final PandoraData pd = new PandoraData();
        final List<BaiduData> list = BaiduDataModel.getInstance().queryWithImgByTag1(
                BaiduTagMapping.S_TAG1_GAOXIAO, 1);
        if (list.size() <= 0)
            return getDefaultData();
        final BaiduData bd = list.get(0);
        final Bitmap bmp = DiskImageHelper.getBitmapByUrl(bd.mImageUrl);
        if (bmp == null) {
            return getDefaultData();
        }
        pd.setmImage(bmp);
        pd.setmId(bd.getId());
        pd.setFrom(DATA_FROM_BAIDU);
        pd.setmImageUrl(bd.getImageUrl());
        pd.setmDesc(bd.getDescribe());
        IPandoraBox box = new SingleImageBox(mContext, pd);
        return box;
    }

    public IPandoraBox getDefaultData() {
        PandoraData pd = new PandoraData();
        pd.setmImage(BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.locker_screen_nondata_default));
        pd.setFrom(DATA_FROM_DEFAULT);
        return new SingleImageBox(mContext, pd);
    }
}
