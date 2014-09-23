
package cn.zmdx.kaka.locker.content;

import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.R.layout;
import cn.zmdx.kaka.locker.content.BaiduDataManager.BaiduData;
import cn.zmdx.kaka.locker.content.IPandoraBox.PandoraData;
import cn.zmdx.kaka.locker.content.ServerDataManager.ServerData;
import cn.zmdx.kaka.locker.database.BaiduDataModel;
import cn.zmdx.kaka.locker.database.ServerDataModel;
import cn.zmdx.kaka.locker.policy.PandoraPolicy;

public class PandoraBoxManager {

    /**
     * 没有数据时默认显示的内容
     */
    public static final int DATA_FROM_DEFAULT = 1;

    /**
     * 标识数据类型为百度图片的数据
     */
    public static final int DATA_FROM_BAIDU = 2;

    /**
     * 标识数据来自自己的服务器，类型为纯文本
     */
    public static final int DATA_FROM_SERVER_TEXT = 4;

    /**
     * 标识数据来自自己的服务器，类型为图文混排
     */
    public static final int DATA_FROM_SERVER_IMG = 5;

    private static PandoraBoxManager mPbManager;

    private Context mContext;

    private static final int TYPE_PLAIN_TEXT = 1;

    private static final int TYPE_MIX_IMG_TEXT = 2;

    private final int[] DISPLAY_TYPE = {
            TYPE_PLAIN_TEXT, TYPE_MIX_IMG_TEXT, TYPE_MIX_IMG_TEXT, TYPE_MIX_IMG_TEXT, TYPE_MIX_IMG_TEXT, TYPE_MIX_IMG_TEXT, TYPE_MIX_IMG_TEXT
    };

    private static long mPreDisplayTime;

    private IPandoraBox mPreDisplayBox;

    private PandoraBoxManager(Context context) {
        mContext = context;
    }

    public synchronized static PandoraBoxManager newInstance(Context context) {
        if (mPbManager == null) {
            mPbManager = new PandoraBoxManager(context);
        }
        return mPbManager;
    }

    private void releaseResource(IPandoraBox box) {
        if (box == null) {
            return;
        }

        PandoraData data = box.getData();
        if (data != null) {
            switch (data.getFrom()) {
                case DATA_FROM_DEFAULT:
                    break;
                case DATA_FROM_BAIDU:
                    BaiduDataModel.getInstance().deleteById(data.getmId());
                    DiskImageHelper.remove(data.getmImageUrl());
                    recycleBitmap(data.getmImage());
                    mPreDisplayBox = null;
                    break;
                case DATA_FROM_SERVER_TEXT:
                    ServerDataModel.getInstance().deleteById(data.getmId());
                    mPreDisplayBox = null;
                    break;
                case DATA_FROM_SERVER_IMG:
                    BaiduDataModel.getInstance().deleteById(data.getmId());
                    DiskImageHelper.remove(data.getmImageUrl());
                    recycleBitmap(data.getmImage());
                    mPreDisplayBox = null;
                    break;
            }
        }
    }

    private void recycleBitmap(Bitmap bmp) {
        if (bmp != null && !bmp.isRecycled()) {
            bmp.recycle();
            bmp = null;
            System.gc();
        }
    }

    public IPandoraBox getNextPandoraData() {
        final long curTime = System.currentTimeMillis();
        if (curTime - mPreDisplayTime < PandoraPolicy.MIN_INTERVAL_SAME_BOX) {
            return mPreDisplayBox == null ? getDefaultData() : mPreDisplayBox;
        }
        int random = new Random().nextInt(10) % DISPLAY_TYPE.length;
        int type = DISPLAY_TYPE[random];
        if (type == TYPE_PLAIN_TEXT) {
            IPandoraBox box = getPlainTextBox();
            if (box == null) {
                return getDefaultData();
            } else {
                releaseResource(mPreDisplayBox);
                mPreDisplayTime = System.currentTimeMillis();
                mPreDisplayBox = box;
                return box;
            }
        } else if (type == TYPE_MIX_IMG_TEXT) {
            IPandoraBox box = getMixImgTextBox();
            if (box == null) {
                return getDefaultData();
            } else {
                releaseResource(mPreDisplayBox);
                mPreDisplayTime = System.currentTimeMillis();
                mPreDisplayBox = box;
                return box;
            }
        }
        return getDefaultData();
    }

    public IPandoraBox getPlainTextBox() {
        final ServerData bd = ServerDataModel.getInstance().queryByRandom();
        if (bd == null) {
            return null;
        }

        final PandoraData pd = new PandoraData();
        pd.setmContent(bd.getContent());
        pd.setFrom(DATA_FROM_SERVER_TEXT);
        pd.setmTitle(bd.getTitle());
        IPandoraBox box = new PlainTextBox(mContext, pd);
        return box;
    }

    public IPandoraBox getMixImgTextBox() {
        final List<BaiduData> list = BaiduDataModel.getInstance().queryWithImgByTag1(
                BaiduTagMapping.S_TAG1_GAOXIAO, 1);
        if (list.size() <= 0) {
            return null;
        }
        final BaiduData bd = list.get(0);
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bmp = DiskImageHelper.getBitmapByUrl(bd.mImageUrl, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        try {
            if (w / h > 2.5 || h / w > 2.5 || w > 1500 || h > 2000) {
                BaiduDataModel.getInstance().deleteById(bd.getId());
                return getMixImgTextBox();
            } else {
                bmp = DiskImageHelper.getBitmapByUrl(bd.mImageUrl, null);
            }
        } catch (Exception e) {
            return null;
        }

        if (bmp == null) {
            BaiduDataModel.getInstance().deleteById(bd.getId());
            return null;
        }

        final PandoraData pd = new PandoraData();
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
                R.drawable.pandora_box_default));
        pd.setFrom(DATA_FROM_DEFAULT);
        return new DefaultBox(mContext);
    }
}
