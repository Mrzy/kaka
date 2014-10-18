
package cn.zmdx.kaka.locker.content;

import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.BaiduDataManager.BaiduData;
import cn.zmdx.kaka.locker.content.ServerDataManager.ServerData;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.content.box.DefaultBox;
import cn.zmdx.kaka.locker.content.box.GifBox;
import cn.zmdx.kaka.locker.content.box.HtmlBox;
import cn.zmdx.kaka.locker.content.box.IPandoraBox;
import cn.zmdx.kaka.locker.content.box.PlainTextBox;
import cn.zmdx.kaka.locker.content.box.SingleImageBox;
import cn.zmdx.kaka.locker.content.box.IPandoraBox.PandoraData;
import cn.zmdx.kaka.locker.database.BaiduDataModel;
import cn.zmdx.kaka.locker.database.ServerDataModel;
import cn.zmdx.kaka.locker.database.ServerImageDataModel;
import cn.zmdx.kaka.locker.database.TableStructure;
import cn.zmdx.kaka.locker.policy.PandoraPolicy;
import cn.zmdx.kaka.locker.utils.HDBLOG;

public class PandoraBoxManager {

    private static PandoraBoxManager mPbManager;

    private Context mContext;

    private static final int TYPE_PLAIN_TEXT_JOKE = 1;

    private static final int TYPE_MIX_NEWS = 2;

    private static final int TYPE_MIX_JOKE = 3;

    private static final int TYPE_MIX_BAIDU = 5;

    private static final int TYPE_GIF = 6;

    private static final int TYPE_HTML = 7;

    private final int[] DISPLAY_TYPE = {
            TYPE_PLAIN_TEXT_JOKE, TYPE_MIX_NEWS, TYPE_MIX_JOKE, TYPE_MIX_BAIDU, TYPE_MIX_NEWS,
            TYPE_GIF, TYPE_HTML
    };

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
            } else if (fromTable.equals(TableStructure.TABLE_NAME_CONTENT)) {
                BaiduDataModel.getInstance().deleteById(data.getmId());
                DiskImageHelper.remove(data.getmImageUrl());
                recycleBitmap(data.getmImage());
                mPreDisplayBox = null;
            } else if (fromTable.equals(TableStructure.TABLE_NAME_SERVER)) {
                ServerDataModel.getInstance().deleteById(data.getmId());
                mPreDisplayBox = null;
            } else if (fromTable.equals(TableStructure.TABLE_NAME_SERVER_IMAGE)) {
                ServerImageDataModel.getInstance().deleteById(data.getmId());
                DiskImageHelper.remove(data.getmImageUrl());
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
        int random = new Random().nextInt(DISPLAY_TYPE.length) % DISPLAY_TYPE.length;
        int type = DISPLAY_TYPE[random];
        type = TYPE_HTML;
        if (type == TYPE_PLAIN_TEXT_JOKE) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("random type:TYPE_PLAIN_TEXT_JOKE");
            }
            releasePreResource(mPreDisplayBox);
            IPandoraBox box = getPlainTextJoke();
            if (box == null) {
                return getDefaultData();
            } else {
                mPreDisplayBox = box;
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("随机到文本笑话一则");
                }
                return box;
            }
        } else if (type == TYPE_MIX_NEWS) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("random type:TYPE_MIX_NEWS");
            }
            releasePreResource(mPreDisplayBox);
            IPandoraBox box = getMixNewsBox();
            if (box == null) {
                return getDefaultData();
            } else {
                mPreDisplayBox = box;
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("随机到新闻一条");
                }
                return box;
            }
        } else if (type == TYPE_MIX_JOKE) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("random type:TYPE_MIX_JOKE");
            }
            releasePreResource(mPreDisplayBox);
            IPandoraBox box = getMixJoke();
            if (box == null) {
                return getDefaultData();
            } else {
                mPreDisplayBox = box;
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("随机到图文笑话一条");
                }
                return box;
            }
        } else if (type == TYPE_MIX_BAIDU) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("random type:TYPE_MIX_BAIDU");
            }
            releasePreResource(mPreDisplayBox);
            IPandoraBox box = getMixFromBaidu();
            if (box == null) {
                return getDefaultData();
            } else {
                mPreDisplayBox = box;
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("随机到百度图片一则");
                }
                return box;
            }
        } else if (type == TYPE_GIF) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("random type: TYPE_GIF");
            }
            releasePreResource(mPreDisplayBox);
            IPandoraBox box = getGifBox();
            if (box == null) {
                return getDefaultData();
            } else {
                mPreDisplayBox = box;
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("随机到GIF图片一则");
                }
                return box;
            }
        } else if (type == TYPE_HTML) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("random type: TYPE_HTML");
            }
            releasePreResource(mPreDisplayBox);
            IPandoraBox box = getHtmlBox();
            if (box == null) {
                return getDefaultData();
            } else {
                mPreDisplayBox = box;
                if (BuildConfig.DEBUG) {
                    HDBLOG.logD("随机到HTML一则");
                }
                return box;
            }
        }

        return getDefaultData();
    }

    private IPandoraBox getHtmlBox() {
        final PandoraData pd = new PandoraData();
//        pd.setmContentUrl("http://m.toutiao.com");
//        pd.setmContentUrl("http://info.3g.qq.com/g/s?sid=AaJe3Hr7LpIjQLuGrgdSzPB8&aid=template&tid=ent_h&iarea=84&i_f=170");
//        pd.setmContentUrl("http://3g.163.com/touch/photo");
        pd.setmContentUrl("http://info.3g.qq.com/g/s?sid=AZAZ8F8np2CylIpirKn96gxQ&icfa=news_newspic&aid=image&pos=news_jxpic#pos/channel=sole&pos=news_jxpic&from=tag");
//        pd.setmContentUrl("http://www.baidu.com");
        IPandoraBox box = new HtmlBox(pd);
        return box;
    }

    private IPandoraBox getGifBox() {
        final ServerImageData bd = ServerImageDataModel.getInstance().queryByDataType(
                ServerDataMapping.S_DATATYPE_GIF);
        if (bd == null) {
            return null;
        }

        final String url = bd.getUrl();
        final Bitmap bmp = DiskImageHelper.getBitmapByUrl(url, null);
        if (bmp == null) {
            ServerImageDataModel.getInstance().deleteById(bd.getId());
            return null;
        }

        final PandoraData pd = new PandoraData();
        pd.setmId(bd.getId());
        pd.setFromTable(TableStructure.TABLE_NAME_SERVER_IMAGE);
        pd.setmTitle(bd.getTitle());
        pd.setmContent(bd.getTitle());
        pd.setDataType("TYPE_GIF");
        pd.setmImageUrl(bd.getUrl());
        pd.setmImage(bmp);
        IPandoraBox box = new GifBox(mContext, pd);
        return box;
    }

    private IPandoraBox getMixJoke() {
        final ServerImageData bd = ServerImageDataModel.getInstance().queryByDataType(
                ServerDataMapping.S_DATATYPE_JOKE);
        if (bd == null) {
            return null;
        }

        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bmp = DiskImageHelper.getBitmapByUrl(bd.getUrl(), newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        try {
            if (!PandoraPolicy.verifyImageLegal(bd.getUrl(), w, h)) {
                ServerImageDataModel.getInstance().deleteById(bd.getId());
                DiskImageHelper.remove(bd.getUrl());
                return getMixJoke();
            } else {
                bmp = DiskImageHelper.getBitmapByUrl(bd.getUrl(), null);
            }
        } catch (Exception e) {
            return null;
        }

        if (bmp == null) {
            ServerImageDataModel.getInstance().deleteById(bd.getId());
            return null;
        }
        final PandoraData pd = new PandoraData();
        pd.setmId(bd.getId());
        pd.setFromTable(TableStructure.TABLE_NAME_SERVER_IMAGE);
        pd.setmTitle(bd.getTitle());
        pd.setmContent(bd.getTitle());
        pd.setDataType("TYPE_MIX_JOKE");
        pd.setmImageUrl(bd.getUrl());
        pd.setmImage(bmp);
        IPandoraBox box = new SingleImageBox(mContext, pd);
        return box;
    }

    private IPandoraBox getMixNewsBox() {
        final ServerImageData bd = ServerImageDataModel.getInstance().queryByDataType(
                ServerDataMapping.S_DATATYPE_NEWS);
        if (bd == null) {
            return null;
        }

        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bmp = DiskImageHelper.getBitmapByUrl(bd.getUrl(), newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        try {
            if (!PandoraPolicy.verifyImageLegal(bd.getUrl(), w, h)) {
                ServerImageDataModel.getInstance().deleteById(bd.getId());
                DiskImageHelper.remove(bd.getUrl());
                return getMixNewsBox();
            } else {
                bmp = DiskImageHelper.getBitmapByUrl(bd.getUrl(), null);
            }
        } catch (Exception e) {
            return null;
        }

        if (bmp == null) {
            ServerImageDataModel.getInstance().deleteById(bd.getId());
            return null;
        }
        final PandoraData pd = new PandoraData();
        pd.setmId(bd.getId());
        pd.setFromTable(TableStructure.TABLE_NAME_SERVER_IMAGE);
        pd.setmTitle(bd.getTitle());
        pd.setmContent(bd.getTitle());
        pd.setDataType("TYPE_MIX_NEWS");
        pd.setmImageUrl(bd.getUrl());
        pd.setmImage(bmp);
        IPandoraBox box = new SingleImageBox(mContext, pd);
        return box;
    }

    public IPandoraBox getPlainTextJoke() {
        final ServerData bd = ServerDataModel.getInstance().queryByDataType(
                ServerDataMapping.S_DATATYPE_JOKE);
        if (bd == null) {
            return null;
        }
        // 若文本笑话长度超过220字，则忽略本条继续查询下一条
        if (bd.getContent() != null && bd.getContent().length() > 220) {
            if (BuildConfig.DEBUG) {
                HDBLOG.logD("文本长度超过220字，忽略，继续查询下一条...");
            }
            ServerDataModel.getInstance().deleteById(bd.getId());
            return getPlainTextJoke();
        }

        final PandoraData pd = new PandoraData();
        pd.setmId(bd.getId());
        pd.setmContent(bd.getContent());
        pd.setFromTable(TableStructure.TABLE_NAME_SERVER);
        pd.setmTitle(bd.getTitle());
        pd.setDataType("TYPE_PLAIN_TEXT_JOKE");
        IPandoraBox box = new PlainTextBox(mContext, pd);
        return box;
    }

    public IPandoraBox getMixFromBaidu() {
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
            if (!PandoraPolicy.verifyImageLegal(bd.getImageUrl(), w, h)) {
                BaiduDataModel.getInstance().deleteById(bd.getId());
                DiskImageHelper.remove(bd.getImageUrl());
                return getMixFromBaidu();
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
        pd.setmId(bd.getId());
        pd.setmImage(bmp);
        pd.setFromTable(TableStructure.TABLE_NAME_CONTENT);
        pd.setmId(bd.getId());
        pd.setmImageUrl(bd.getImageUrl());
        pd.setmContent(bd.getDescribe());
        pd.setDataType("TYPE_MIX_BAIDU");
        IPandoraBox box = new SingleImageBox(mContext, pd);
        return box;
    }

    public IPandoraBox getDefaultData() {
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
