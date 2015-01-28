
package cn.zmdx.kaka.fast.locker.settings;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import cn.zmdx.kaka.fast.locker.R;
import cn.zmdx.kaka.fast.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.fast.locker.widget.PandoraInitSettingPromptView;
import cn.zmdx.kaka.fast.locker.widget.PandoraInitSettingPromptView.IPromptViewListener;

import com.afollestad.materialdialogs.MaterialDialog;
import com.umeng.analytics.MobclickAgent;

public class InitPromptActivity extends Activity implements IPromptViewListener {

    private boolean isMIUI;

    private String mMIUIVersion;

    public static final int PROMPT_CLOSE_SYSTEM_LOCKER = 1;

    public static final int PROMPT_ALLOW_FLOAT_WINDOW = 2;

    public static final int PROMPT_TRRST = 3;

    public static final int PROMPT_READ_NOTIFICATION = 4;

    private int mPromptType;

    private MaterialDialog mPromptDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        isMIUI = getIntent().getBooleanExtra("isMIUI", false);
        mMIUIVersion = getIntent().getStringExtra("mMIUIVersion");
        mPromptType = getIntent().getIntExtra("type", PROMPT_CLOSE_SYSTEM_LOCKER);
        if (isMIUI) {
            if (PandoraUtils.MUIU_V5.equals(mMIUIVersion)) {
                getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            }
        }

        PandoraInitSettingPromptView customView = new PandoraInitSettingPromptView(
                InitPromptActivity.this);
        customView.initType(isMIUI, mMIUIVersion, mPromptType, this);
        mPromptDialog = new MaterialDialog.Builder(this).customView(customView, true)
                .dismissListener(mOnDismissListener).build();
        mPromptDialog.show();
    }

    private OnDismissListener mOnDismissListener = new OnDismissListener() {

        @Override
        public void onDismiss(DialogInterface dialog) {
            mPromptDialog.dismiss();
            mPromptDialog.cancel();
            onBackPressed();
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        onBackPressed();
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.umeng_fb_slide_in_from_left,
                R.anim.umeng_fb_slide_out_from_right);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("InitPromptActivity");
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("InitPromptActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onButtonClickListener() {
        onBackPressed();
    }
}
