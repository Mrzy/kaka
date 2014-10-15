
package cn.zmdx.kaka.locker.share;

import java.util.Map;
import java.util.Set;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.umeng.socialize.bean.MultiStatus;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.MulStatusListener;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.media.TencentWbShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMusic;
import com.umeng.socialize.sso.RenrenSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.utils.OauthHelper;

public class PandoraShareManager extends Fragment {

//    private Context mContext = null;
//
//    // sdk controller
//    private UMSocialService mController = null;
//
//    // 布局view
//    private View mMainView = null;
//
//    // 要分享的文字内容
//    private String mShareContent = "";
//
//    private final SHARE_MEDIA mTestMedia = SHARE_MEDIA.SINA;
//
//    // 要分享的图片
//    private UMImage mUMImgBitmap = null;
//
//    // 分享(先选择平台)
//    private Button mShareBtn = null;
//
//    private final String TAG = "TestData";
//
//    /**
//     * @功能描述 : create View for the fragment
//     * @param inflater
//     * @param container
//     * @param savedInstanceState
//     * @return
//     */
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//        mMainView = inflater.inflate(cn.zmdx.kaka.locker.R.id.shareBtn, container, false);
//
//        // 初始化与SDK相关的成员变量
//        initConfig();
//        initViews();
//
//        com.umeng.socialize.utils.Log.LOG = true;
//        mMainView.setTag("分享接口");
//        return mMainView;
//    }
//
//    /**
//     * @功能描述 : 初始化与SDK相关的成员变量
//     */
//    private void initConfig() {
//
//        mContext = getActivity();
//
//        // 要分享的文字内容
//        mController.setShareContent("友盟社会化组件还不错，让移动应用快速整合社交分享功能。www.umeng.com/social");
//
//        mUMImgBitmap = new UMImage(getActivity(),
//                "http://www.umeng.com/images/pic/banner_module_social.png");
//
//        // 添加新浪和QQ空间的SSO授权支持
//        mController.getConfig().setSsoHandler(new SinaSsoHandler());
//        // 添加腾讯微博SSO支持
//        mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
//
//    }
//
//    /**
//     * @功能描述 : 初始化视图控件，比如Button
//     */
//    private void initViews() {
//
//        // 分享(先选择平台)
//        mShareBtn = (Button) mMainView.findViewById(cn.zmdx.kaka.locker.R.id.shareBtn);
//        mShareBtn.setOnClickListener((OnClickListener) this);
//
//    }
//
//    /**
//     * @功能描述 : 点击事件
//     * @param v
//     */
////    @Override
////    public void onClick(View v) {
////        if (v == mShareBtn) {
////            openShareBoard();
////        }
////    }
//
//    /**
//     * @功能描述 : 分享(先选择平台)
//     */
//    private void openShareBoard() {
//        mController.setShareContent("友盟社会化组件还不错，让移动应用快速整合社交分享功能。http://www.umeng.com/social");
//        mController.setShareMedia(mUMImgBitmap);
//        mController.openShare(getActivity(), false);
//    }
//
//    
//    /**
//     * 打开新浪和腾讯微薄的SSO授权
//     */
//    private void openSSO() {
//        mController.getConfig().setSsoHandler(new SinaSsoHandler());
//        mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
//        mController.getConfig().setSsoHandler(
//                new RenrenSsoHandler(getActivity(), "201874", "28401c0964f04a72a14c812d6132fcef",
//                        "3bf66e42db1e4fa9829b955cc300b737"));
//    }
//
//    /**
//     * 关闭sina微博SSO，QQ zone SSO，腾讯微博SSO，
//     */
//    private void closeSSO() {
//        mController.getConfig().removeSsoHandler(SHARE_MEDIA.SINA);
//        mController.getConfig().removeSsoHandler(SHARE_MEDIA.TENCENT);
//        mController.getConfig().removeSsoHandler(SHARE_MEDIA.RENREN);
//    }
//
//    /**
//     * @功能描述 :
//     */
//    public void removePlatform() {
//        mController.getConfig().removePlatform(SHARE_MEDIA.RENREN, SHARE_MEDIA.DOUBAN);
//        mController.openShare(getActivity(), false);
//    }

}
