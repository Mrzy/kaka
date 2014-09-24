
package cn.zmdx.kaka.locker.settings;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import cn.zmdx.kaka.locker.R;

import com.umeng.analytics.MobclickAgent;

public class InitPromptActivity extends Activity {

    private LinearLayout mCloseSystemLockerView;

    private LinearLayout mCloseSystemLockerMIUIView;

    private LinearLayout mAllowFloatWindowView;

    private LinearLayout mTrustView;

    private boolean isMIUI;

    public static final int PROMPT_CLOSE_SYSTEM_LOCKER = 1;

    public static final int PROMPT_ALLOW_FLOAT_WINDOW = 2;

    public static final int PROMPT_TRRST = 3;

    private int mPromptType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init_prompt_activity);
        isMIUI = getIntent().getBooleanExtra("isMIUI", false);
        mPromptType = getIntent().getIntExtra("type", PROMPT_CLOSE_SYSTEM_LOCKER);
        initView();
        showView();
    }

    private void initView() {
        mCloseSystemLockerView = (LinearLayout) findViewById(R.id.init_setting_close_systemlocker_prompt);
        mCloseSystemLockerMIUIView = (LinearLayout) findViewById(R.id.init_setting_close_systemlocker_prompt_miui);
        mAllowFloatWindowView = (LinearLayout) findViewById(R.id.init_setting_allow_floating_window_prompt);
        mTrustView = (LinearLayout) findViewById(R.id.init_setting_trust_prompt);
    }

    private void showView() {
        switch (mPromptType) {
            case PROMPT_CLOSE_SYSTEM_LOCKER:
                if (isMIUI) {
                    mCloseSystemLockerMIUIView.setVisibility(View.VISIBLE);
                } else {
                    mCloseSystemLockerView.setVisibility(View.VISIBLE);
                }
                break;
            case PROMPT_ALLOW_FLOAT_WINDOW:
                mAllowFloatWindowView.setVisibility(View.VISIBLE);
                break;
            case PROMPT_TRRST:
                mTrustView.setVisibility(View.VISIBLE);
                break;

            default:
                break;
        }
    }

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
        MobclickAgent.onPageStart("InitPromptActivity"); // 统计页面
        MobclickAgent.onResume(this); // 统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("InitPromptActivity"); // 保证 onPageEnd 在onPause之前调用,因为
                                                 // onPause 中会保存信息
        MobclickAgent.onPause(this);
    }
}
