
package cn.zmdx.kaka.locker.content;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.widget.TextView;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.content.box.DefaultBox;
import cn.zmdx.kaka.locker.content.box.FoldableBoxAdapter;
import cn.zmdx.kaka.locker.content.box.FoldablePage;
import cn.zmdx.kaka.locker.content.box.IFoldablePage;
import cn.zmdx.kaka.locker.content.box.IPandoraBox;
import cn.zmdx.kaka.locker.content.box.IPandoraBox.PandoraData;
import cn.zmdx.kaka.locker.content.favorites.FavoritesManager;
import cn.zmdx.kaka.locker.database.ServerImageDataModel;
import cn.zmdx.kaka.locker.policy.PandoraPolicy;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;

public class PandoraBoxManager {

    private static PandoraBoxManager mPbManager;

    private Context mContext;

    private TextView tvDefaultView;

    private PandoraBoxManager(Context context) {
        mContext = context;
    }

    public synchronized static PandoraBoxManager newInstance(Context context) {
        if (mPbManager == null) {
            mPbManager = new PandoraBoxManager(context);
        }
        return mPbManager;
    }

    public IFoldablePage getFoldablePage() {
        List<ServerImageData> data = getDataFormLocalDB(PandoraPolicy.MIN_COUNT_FOLDABLE_BOX);
        if (BuildConfig.DEBUG) {
            HDBLOG.logD("从本地取出数据条数：" + data.size());
        }
        FoldablePage box = new FoldablePage(mContext, data);
        FoldableBoxAdapter adapter = new FoldableBoxAdapter(mContext, box.makeCardList(data));
        box.setAdapter(adapter);
        return box;
    }

    public IFoldablePage getFavoriteFoldablePage() {
        FavoritesManager manager = new FavoritesManager(mContext);
        List<ServerImageData> listData = new ArrayList<ServerImageData>();
        Cursor cursor = manager.getFavoritesInfo();
        if (cursor == null) {
            return null;
        }
        List<ServerImageData> cursorToList = cursorToList(cursor, listData);
        if (BuildConfig.DEBUG) {
            HDBLOG.logD("从本地取出收藏条数：" + cursorToList.size());
        }
        FoldablePage foldablePage = null;
        if (null != cursorToList) {
            foldablePage = new FoldablePage(mContext, cursorToList);
            FoldableBoxAdapter adapter = new FoldableBoxAdapter(HDApplication.getContext(),
                    foldablePage.makeCardList(cursorToList));
            foldablePage.setAdapter(adapter);
        }
        return foldablePage;
    }

    public List<ServerImageData> cursorToList(Cursor cursor, List<ServerImageData> list) {
        if (null != cursor) {
            try {
                cursor.moveToFirst();
                while (cursor.moveToNext()) {
                    ServerImageData imgData = new ServerImageData();
                    imgData.setId(cursor.getInt(0));
                    imgData.setImageDesc(cursor.getString(1));
                    imgData.setTitle(cursor.getString(2));
                    imgData.setUrl(cursor.getString(3));
                    imgData.setCollectTime(cursor.getString(4));
                    imgData.setCollectWebsite(cursor.getString(5));
                    imgData.setReleaseTime(cursor.getString(6));
                    imgData.setCloudId(cursor.getString(7));
                    imgData.setDataType(cursor.getString(8));
                    list.add(imgData);
                }
            } catch (Exception e) {
                // TODO: handle exception
            } finally {
                cursor.close();
            }
        }
        return list;
    }

    public List<ServerImageData> getDataFormLocalDB(int count) {
        boolean containHtml = HDBNetworkState.isNetworkAvailable()
                && HDBNetworkState.isWifiNetwork();
        return ServerImageDataModel.getInstance().queryByRandom(count, containHtml);
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
