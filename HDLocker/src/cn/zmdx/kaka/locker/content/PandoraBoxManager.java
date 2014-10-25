
package cn.zmdx.kaka.locker.content;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.content.box.DefaultBox;
import cn.zmdx.kaka.locker.content.box.GifBox;
import cn.zmdx.kaka.locker.content.box.HtmlBox;
import cn.zmdx.kaka.locker.content.box.IPandoraBox;
import cn.zmdx.kaka.locker.content.box.IPandoraBox.PandoraData;
import cn.zmdx.kaka.locker.content.box.SingleImageBox;
import cn.zmdx.kaka.locker.database.ServerImageDataModel;
import cn.zmdx.kaka.locker.database.TableStructure;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;

public class PandoraBoxManager {

    private static PandoraBoxManager mPbManager;

    private Context mContext;

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

    private void releasePreResource(IPandoraBox box) {
        if (box == null) {
            return;
        }

        PandoraData data = box.getData();
        if (data != null) {
            String fromTable = data.getFromTable();
            if (fromTable == null) {
                return;
            } else if (fromTable.equals(TableStructure.TABLE_NAME_SERVER_IMAGE)) {
//                ServerImageDataModel.getInstance().deleteById(data.getmId());
                ServerImageDataModel.getInstance().markRead(data.getmId(), true);
//                DiskImageHelper.remove(data.getmImageUrl());
                recycleBitmap(data.getmImage());
                mPreDisplayBox = null;
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

    public IPandoraBox getNextPandoraBox() {
        releasePreResource(mPreDisplayBox);
        IPandoraBox box = getRandomBox();
        if (box == null) {
            return getDefaultData();
        }
        mPreDisplayBox = box;
        if (BuildConfig.DEBUG) {
            HDBLOG.logD("随机到一则数据，类型为：" + box.getCategory());
        }
        return box;
    }

    private IPandoraBox getRandomBox() {
        ServerImageData bd = null;
        // 只有在wifi网络下，才显示html类型数据
        if (HDBNetworkState.isNetworkAvailable() && HDBNetworkState.isWifiNetwork()) {
            bd = ServerImageDataModel.getInstance().queryOneByRandom();
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("queryOneByRandom, return data:" + bd);
            }
        } else {// 如果当前没网络或者非wifi下，只显示非html类型数据
            bd = ServerImageDataModel.getInstance().queryOneWithImage();
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("queryOneWithImage, return data:" + bd);
            }
        }
        if (bd == null) {
            return null;
        }
        IPandoraBox box = null;
        String dataType = bd.getDataType();
        if (ServerDataMapping.S_DATATYPE_GIF.equals(dataType)) {
            box = getGifBox(bd);
        } else if (ServerDataMapping.S_DATATYPE_HTML.equals(dataType)) {
            box = getHtmlBox(bd);
        } else if (ServerDataMapping.S_DATATYPE_JOKE.equals(dataType)) {
            box = getJokeBox(bd);
        } else if (ServerDataMapping.S_DATATYPE_NEWS.equals(dataType)) {
            box = getNewsBox(bd);
        } else {//对于不识别的类型数据，删除此数据
            ServerImageDataModel.getInstance().deleteById(bd.getId());
            return null;
        }
        return box;
    }

    private IPandoraBox getHtmlBox(ServerImageData bd) {
        final PandoraData pd = new PandoraData();
        pd.setmId(bd.getId());
        pd.setmContentUrl(bd.getUrl());
        pd.setFromTable(TableStructure.TABLE_NAME_SERVER_IMAGE);
        pd.setDataType(ServerDataMapping.S_DATATYPE_HTML);
        pd.setmFromWebSite(bd.getCollectWebsite());
        final IPandoraBox box = new HtmlBox(pd);
        return box;
    }

    private IPandoraBox getGifBox(ServerImageData bd) {
        final String url = bd.getUrl();
        final Bitmap bmp = DiskImageHelper.getBitmapByUrl(url, null);
        if (bmp == null) {
            ServerImageDataModel.getInstance().markRead(bd.getId(), true);
//            ServerImageDataModel.getInstance().deleteById(bd.getId());
            return null;
        }

        final PandoraData pd = new PandoraData();
        pd.setmId(bd.getId());
        pd.setFromTable(TableStructure.TABLE_NAME_SERVER_IMAGE);
        pd.setmTitle(bd.getTitle());
        pd.setDataType("TYPE_GIF");
        pd.setmImageUrl(bd.getUrl());
        pd.setmImage(bmp);
        pd.setmContent(bd.getImageDesc());
        pd.setmFromWebSite(bd.getCollectWebsite());
        IPandoraBox box = new GifBox(mContext, pd);
        return box;
    }

    private IPandoraBox getJokeBox(ServerImageData bd) {
        Bitmap bmp = DiskImageHelper.getBitmapByUrl(bd.getUrl(), null);
        try {
            bmp = DiskImageHelper.getBitmapByUrl(bd.getUrl(), null);
        } catch (Exception e) {
            return null;
        }

        if (bmp == null) {
            ServerImageDataModel.getInstance().markRead(bd.getId(), true);
//            ServerImageDataModel.getInstance().deleteById(bd.getId());
            return null;
        }
        final PandoraData pd = new PandoraData();
        pd.setmId(bd.getId());
        pd.setFromTable(TableStructure.TABLE_NAME_SERVER_IMAGE);
        pd.setmTitle(bd.getTitle());
        pd.setDataType("TYPE_MIX_JOKE");
        pd.setmImageUrl(bd.getUrl());
        pd.setmImage(bmp);
        pd.setmContent(bd.getImageDesc());
        pd.setmFromWebSite(bd.getCollectWebsite());
        IPandoraBox box = new SingleImageBox(mContext, pd);
        return box;
    }

    private IPandoraBox getNewsBox(ServerImageData bd) {
        Bitmap bmp = DiskImageHelper.getBitmapByUrl(bd.getUrl(), null);
        try {
            bmp = DiskImageHelper.getBitmapByUrl(bd.getUrl(), null);
        } catch (Exception e) {
            return null;
        }

        if (bmp == null) {
            ServerImageDataModel.getInstance().markRead(bd.getId(), true);
//            ServerImageDataModel.getInstance().deleteById(bd.getId());
            return null;
        }
        final PandoraData pd = new PandoraData();
        pd.setmId(bd.getId());
        pd.setFromTable(TableStructure.TABLE_NAME_SERVER_IMAGE);
        pd.setmTitle(bd.getTitle());
        pd.setDataType("TYPE_MIX_NEWS");
        pd.setmImageUrl(bd.getUrl());
        pd.setmImage(bmp);
        pd.setmContent(bd.getImageDesc());
        pd.setmFromWebSite(bd.getCollectWebsite());
        IPandoraBox box = new SingleImageBox(mContext, pd);
        return box;
    }

    public IPandoraBox getDefaultData() {
        if (BuildConfig.DEBUG) {
            HDBLOG.logD("获得默认页面");
        }
        PandoraData pd = new PandoraData();
//        pd.setmId(-1);
        pd.setmImage(BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.pandora_box_default));
        pd.setDataType("DEFAULT_PAGE");
        return new DefaultBox(mContext, pd);
    }
}
