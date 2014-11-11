
package cn.zmdx.kaka.locker.content.box;

import java.io.File;
import java.io.IOException;

import pl.droidsonroids.gif.GifImageView;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.content.DiskImageHelper;
import cn.zmdx.kaka.locker.content.ServerDataMapping;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.utils.HDBLOG;

public class GifBox implements IPandoraBox {
    private View mEntireView;

    private PandoraData mData;

    private GifImageView mImageView;

    private TextView mFromPlatformText;

    private ImageButton mImageButton;

    private TextView mTextView;

    private boolean mIsRendered = false;

    private FoldablePage mPage;

    public GifBox(Context context, FoldablePage page, PandoraData data) {
        mData = data;
        mPage = page;
        mEntireView = LayoutInflater.from(context).inflate(R.layout.pandora_box_gif_image, null);
        mImageView = (GifImageView) mEntireView.findViewById(R.id.gif_image);
        mTextView = (TextView) mEntireView.findViewById(R.id.gif_desc);
        mFromPlatformText = (TextView) mEntireView.findViewById(R.id.gif_from_platform_text);
        mImageButton = (ImageButton) mEntireView.findViewById(R.id.pandora_box_gif_single_back_btn);
    }

    @Override
    public int getCategory() {

        return IPandoraBox.CATEGORY_GIF;
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
        if (mData == null || TextUtils.isEmpty(mData.getmImageUrl())) {
            return false;
        }
        File file = DiskImageHelper.getFileByUrl(mData.getmImageUrl());
        try {
            mImageView.setBackgroundDrawable(true, file);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        if (TextUtils.isEmpty(mData.getmTitle())) {
            mTextView.setVisibility(View.GONE);
            return true;
        }
        mFromPlatformText.setText(mData.getmFromWebSite());
        mTextView.setText(mData.getmTitle());
        mImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                stopGif();
                mPage.foldBack();
            }
        });
        mImageButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                stopGif();
                mPage.foldBack();
            }
        });

        mImageView.stopGif();
        mIsRendered = true;
        return true;
    }

    public void startGif() {
        if (BuildConfig.DEBUG) {
            HDBLOG.logD("开始播放gif动画");
        }
        mImageView.startGif();
    }

    public void stopGif() {
        if (BuildConfig.DEBUG) {
            HDBLOG.logD("停止播放gif动画");
        }
        mImageView.stopGif();
    }

    public static PandoraData convertFormServerImageData(ServerImageData data) {
        PandoraData pd = new PandoraData();
        pd.setmId(data.getId());
        pd.setmImageUrl(data.getUrl());
        pd.setmTitle(data.getTitle());
        pd.setDataType(ServerDataMapping.S_DATATYPE_GIF);
        pd.setmFromWebSite(data.getCollectWebsite());
        pd.setmContent(data.getImageDesc());
        return pd;
    }
}
