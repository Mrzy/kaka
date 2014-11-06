
package cn.zmdx.kaka.locker.content.box;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.DiskImageHelper;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.content.box.IPandoraBox.PandoraData;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;

public class SingleImageBox extends BaseBox {

    private Context mContext;

    private PandoraData mData;

    private ViewGroup mEntireView;

    private ImageView mSingleImgView;

    private TextView mDescView;

    private TextView mImageNewsContent;

    private boolean mIsRendered = false;

    private boolean mIsHide = false;

    private ViewGroup mShareLayout;

    private TextView mFromPlatformText;

    public SingleImageBox(Context context, PandoraData data) {
        mData = data;
        mContext = context;
        mEntireView = (ViewGroup) LayoutInflater.from(context).inflate(
                R.layout.pandora_box_single_image, null);
        mSingleImgView = (ImageView) mEntireView.findViewById(R.id.single_img);
        setImageViewSize(mSingleImgView);
        mDescView = (TextView) mEntireView.findViewById(R.id.desc);
        mDescView.getPaint().setFakeBoldText(true);
        mFromPlatformText = (TextView) mEntireView.findViewById(R.id.from_platform_text);
        mImageNewsContent = (TextView) mEntireView.findViewById(R.id.image_news_content);
        mShareLayout = (ViewGroup) mEntireView.findViewById(R.id.share_from_platform);
        enableShare();
        mShareLayout.addView(createShareView());
    }

    private void setImageViewSize(ImageView iv) {
        Bitmap bmp = mData.getmImage();
        int bmpWidth = 0;
        int bmpHeight = 0;
        if (bmp != null) {
            bmpWidth = bmp.getWidth();
            bmpHeight = bmp.getHeight();
        }
        ViewGroup.LayoutParams lp = iv.getLayoutParams();
        lp.width = LayoutParams.MATCH_PARENT;
        int screenWidth = BaseInfoHelper.getWidth(mContext);
        try {
            float rate = (float) screenWidth / (float) bmpWidth;
            lp.height = (int) (rate * bmpHeight);
        } catch (Exception e) {
            lp.height = LayoutParams.MATCH_PARENT;
        }
        iv.setScaleType(ScaleType.FIT_XY);
        iv.setLayoutParams(lp);
    }

    @Override
    public int getCategory() {
        return IPandoraBox.CATEGORY_SINGLE_IMG;
    }

    @Override
    public PandoraData getData() {
        return mData;
    }

    @Override
    public View getContainer() {
        return mEntireView;
    }

    @Override
    public View getRenderedView() {
        if (mIsRendered) {
            return mEntireView;
        }
        boolean result = render();
        return result ? mEntireView : null;
    }

    private boolean render() {
        if (mData == null || mData.getmImage() == null) {
            return false;
        }
        mFromPlatformText.setText(mData.getmFromWebSite());
        mImageNewsContent.setText(mData.getmContent());
        mSingleImgView.setImageBitmap(mData.getmImage());
        mDescView.setText(mData.getmTitle());
        return true;
    }

    public static PandoraData convertFromServerData(ServerImageData data) {
        PandoraData pd = new PandoraData();
        pd.setmFromWebSite(data.getCollectWebsite());
        pd.setmContent(data.getImageDesc());
        pd.setmImage(DiskImageHelper.getBitmapByUrl(data.getUrl(), null));
        return pd;
    }

}
