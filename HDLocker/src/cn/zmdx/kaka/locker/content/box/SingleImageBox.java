
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
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.widget.BaseScrollView;

public class SingleImageBox implements IPandoraBox, View.OnClickListener {

    private Context mContext;

    private PandoraData mData;

    private ViewGroup mEntireView;

    private ImageView mSingleImgView;

    private TextView mDescView;

    private TextView mImageNewsContent;

    private boolean mIsRendered = false;

    private TextView mFromPlatformText;

    private BaseScrollView mScrollView;

    private FoldablePage mPage;

    private View mBackBtn;

    public SingleImageBox(Context context, FoldablePage page, PandoraData data) {
        mData = data;
        mPage = page;
        mContext = context;
    }

    public boolean isAtTop() {
        return mScrollView.isAtTop();
    }

    private void initViews() {
        mEntireView = (ViewGroup) LayoutInflater.from(mContext).inflate(
                R.layout.pandora_box_single_image, null);
        mScrollView = (BaseScrollView) mEntireView.findViewById(R.id.scrollView);
        // mScrollView.setOnScrollListener(listener)
        // mScrollView.setOnTouchListener(new
        // ScrollTouchListener(mScrollListener));
        mSingleImgView = (ImageView) mEntireView.findViewById(R.id.single_img);
        setImageViewSize(mSingleImgView);
        mDescView = (TextView) mEntireView.findViewById(R.id.desc);
        mDescView.getPaint().setFakeBoldText(true);
        mFromPlatformText = (TextView) mEntireView.findViewById(R.id.from_platform_text);
        mImageNewsContent = (TextView) mEntireView.findViewById(R.id.image_news_content);
        // mBackBtn =
        // mEntireView.findViewById(R.id.pandora_box_single_back_btn);
        // mBackBtn.setOnClickListener(this);
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
        int screenWidth = BaseInfoHelper.getRealWidth(mContext);
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
        initViews();
        mFromPlatformText.setText(mData.getmFromWebSite());
        mImageNewsContent.setText(mData.getmContent());
        mSingleImgView.setImageBitmap(mData.getmImage());
        mSingleImgView.setOnClickListener(this);
        mDescView.setText(mData.getmTitle());
        return true;
    }

    public static PandoraData convertFromServerData(ServerImageData data) {
        PandoraData pd = new PandoraData();
        pd.setmId(data.getId());
        pd.setmFromWebSite(data.getCollectWebsite());
        pd.setmTitle(data.getTitle());
        pd.setmContent(data.getImageDesc());
        pd.setmImage(DiskImageHelper.getBitmapByUrl(data.getUrl(), null));
        return pd;
    }

    @Override
    public void onClick(View v) {
        if (v == mBackBtn) {
            mPage.foldBack();
        } else if (v == mSingleImgView) {
            mPage.foldBack();
        }
    }
}
