
package cn.zmdx.kaka.locker.share;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.zmdx.kaka.locker.LockScreenManager;
import cn.zmdx.kaka.locker.content.PandoraBoxManager;
import cn.zmdx.kaka.locker.content.ServerDataMapping;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.utils.HDBThreadUtils;

public class PandoraShareManager {

    public static final int TYPE_SHARE_WECHAT = 0;

    public static final int TYPE_SHARE_WECHAT_CIRCLE = 1;

    public static final int TYPE_SHARE_SINA = 2;

    public static final int TYPE_SHARE_QQ = 3;

    public static final String PACKAGE_WECHAR_STRING = "com.tencent.mm";

    public static final String PACKAGE_SINA_STRING = "com.sina.weibo";

    public static final String PACKAGE_QQ_STRING = "com.tencent.mobileqq";

    private static Context mContext;

    public static void shareContent(final Context context, final ServerImageData data, int shareType) {
        mContext = context;
        switch (shareType) {
            case TYPE_SHARE_WECHAT_CIRCLE:
                LockScreenManager.getInstance().setRunnableAfterUnLock(new Runnable() {

                    @Override
                    public void run() {
                        PandoraWechatShareManager.getInstance().shareToWechat(mContext, true,
                                data, mPlatformActionListener);
                    }
                });
                break;
            case TYPE_SHARE_WECHAT:
                LockScreenManager.getInstance().setRunnableAfterUnLock(new Runnable() {

                    @Override
                    public void run() {
                        PandoraWechatShareManager.getInstance().shareToWechat(mContext, false,
                                data, mPlatformActionListener);
                    }
                });
                break;
            case TYPE_SHARE_SINA:
                LockScreenManager.getInstance().setRunnableAfterUnLock(new Runnable() {

                    @Override
                    public void run() {
                        PandoraSinaShareManager.getInstance().shareToSina(mContext, data,
                                mPlatformActionListener);
                    }
                });
                break;
            case TYPE_SHARE_QQ:
                LockScreenManager.getInstance().setRunnableAfterUnLock(new Runnable() {

                    @Override
                    public void run() {
                        PandoraQQShareManager.getInstance().shareToQzone(mContext, data,
                                mPlatformActionListener);
                    }
                });
                break;

            default:
                break;
        }
        PandoraBoxManager.newInstance(mContext).closeDetailPage(false);
        LockScreenManager.getInstance().collapseNewsPanel();
        LockScreenManager.getInstance().unLock();
    }

    private static PlatformActionListener mPlatformActionListener = new PlatformActionListener() {

        @Override
        public void onError(Platform platform, int action, Throwable t) {
            HDBThreadUtils.runOnUi(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(mContext, "分享失败，请重试!", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onComplete(Platform platform, int action, HashMap<String, Object> arg2) {
            HDBThreadUtils.runOnUi(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(mContext, "分享成功", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onCancel(Platform platform, int action) {
            HDBThreadUtils.runOnUi(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(mContext, "已取消分享", Toast.LENGTH_LONG).show();
                }
            });

        }
    };

    public static boolean isAvilible(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        List<String> pName = new ArrayList<String>();
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);
    }

    public static final class PandoraShareData implements Parcelable {
        public String mTitle;

        public String mDesc;

        public String mImageUrl;

        public String mWebUrl;

        public void bulidShareParam(ServerImageData data) {
            String type = data.getDataType();
            if (type.equals(ServerDataMapping.S_DATATYPE_HTML)) {
                mTitle = data.getTitle();
                mDesc = data.getTitle();
                mImageUrl = data.getUrl();
                mWebUrl = data.getUrl();
            } else if (type.equals(ServerDataMapping.S_DATATYPE_GIF)) {
                mTitle = data.getTitle();
                mDesc = data.getTitle();
                mImageUrl = data.getUrl();
                mWebUrl = data.getUrl();
            } else if (type.equals(ServerDataMapping.S_DATATYPE_NEWS)
                    || type.equals(ServerDataMapping.S_DATATYPE_JOKE)
                    || type.equals(ServerDataMapping.S_DATATYPE_SINGLEIMG)) {
                mTitle = data.getTitle();
                mDesc = data.getImageDesc().substring(0, 18) + "...";
                mImageUrl = data.getUrl();
                mWebUrl = data.getUrl();
            } else if (type.equals(ServerDataMapping.S_DATATYPE_MULTIIMG)) {
                mTitle = data.getTitle();
                mDesc = data.getTitle();
                mImageUrl = data.getUrl();
                mWebUrl = data.getImageDesc();
            }
        }

        public PandoraShareData() {
        }

        private PandoraShareData(Parcel in) {
            this.mTitle = in.readString();
            this.mDesc = in.readString();
            this.mImageUrl = in.readString();
            this.mWebUrl = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.mTitle);
            dest.writeString(this.mDesc);
            dest.writeString(this.mImageUrl);
            dest.writeString(this.mWebUrl);
        }

        public static final Parcelable.Creator<PandoraShareData> CREATOR = new Parcelable.Creator<PandoraShareData>() {
            public PandoraShareData createFromParcel(Parcel source) {
                return new PandoraShareData(source);
            }

            public PandoraShareData[] newArray(int size) {
                return new PandoraShareData[size];
            }
        };
    }
}
