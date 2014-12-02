package cn.zmdx.kaka.locker.content.box;

import android.content.Context;
import cn.zmdx.kaka.locker.content.DiskImageHelper;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;

public class MultiImgBox extends HtmlBox {

    public MultiImgBox(Context context, FoldablePage page, PandoraData data) {
        super(context, page, data);
    }
    @Override
    public int getCategory() {
        return IPandoraBox.CATEGORY_MULTIIMG;
    }

    public static PandoraData convertToMultiBox(ServerImageData data) {
        PandoraData pd = new PandoraData();
        pd.setmId(data.getId());
        pd.setmFromWebSite(data.getCollectWebsite());
        pd.setmTitle(data.getTitle());
        pd.setmImage(DiskImageHelper.getBitmapByUrl(data.getUrl(), null));
        pd.setmContentUrl(data.getImageDesc());
        return pd;
    }
}
