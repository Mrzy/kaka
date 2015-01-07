
package cn.zmdx.kaka.locker.share;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Parcel;
import android.os.Parcelable;
import cn.zmdx.kaka.locker.LockScreenManager;
import cn.zmdx.kaka.locker.content.ServerDataMapping;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.settings.ShareContentActivity;

public class PandoraShareManager {

    public static final int TYPE_SHARE_WECHAT = 0;

    public static final int TYPE_SHARE_WECHAT_CIRCLE = 1;

    public static final int TYPE_SHARE_SINA = 2;

    public static final int TYPE_SHARE_QQ = 3;

    public static final String PACKAGE_WECHAR_STRING = "com.tencent.mm";

    public static final String PACKAGE_SINA_STRING = "com.sina.weibo";

    public static final String PACKAGE_QQ_STRING = "com.tencent.mobileqq";

    public static void shareContent(final Context mContext, ServerImageData data, int shareType) {
        final PandoraShareData shareData = new PandoraShareData();
        shareData.bulidShareParam(data);
        switch (shareType) {
            case TYPE_SHARE_WECHAT_CIRCLE:
                LockScreenManager.getInstance().setRunnableAfterUnLock(new Runnable() {

                    @Override
                    public void run() {
                        PandoraWechatShareManager.getInstance().shareToWechat(mContext, true,
                                shareData);
                    }
                });
                break;
            case TYPE_SHARE_WECHAT:
                LockScreenManager.getInstance().setRunnableAfterUnLock(new Runnable() {

                    @Override
                    public void run() {
                        PandoraWechatShareManager.getInstance().shareToWechat(mContext, false,
                                shareData);
                    }
                });
                break;
            case TYPE_SHARE_SINA:
                LockScreenManager.getInstance().setRunnableAfterUnLock(new Runnable() {

                    @Override
                    public void run() {
                        Intent sinaIntent = new Intent();
                        sinaIntent.putExtra("type", PandoraShareManager.TYPE_SHARE_SINA);
                        sinaIntent.putExtra("shareData", shareData);
                        sinaIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        sinaIntent.setClass(mContext, ShareContentActivity.class);
                        mContext.startActivity(sinaIntent);
                    }
                });
                break;
            case TYPE_SHARE_QQ:
                LockScreenManager.getInstance().setRunnableAfterUnLock(new Runnable() {

                    @Override
                    public void run() {
                        Intent qqIntent = new Intent();
                        qqIntent.putExtra("type", PandoraShareManager.TYPE_SHARE_QQ);
                        qqIntent.putExtra("shareData", shareData);
                        qqIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        qqIntent.setClass(mContext, ShareContentActivity.class);
                        mContext.startActivity(qqIntent);
                    }
                });
                break;

            default:
                break;
        }
    }

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
