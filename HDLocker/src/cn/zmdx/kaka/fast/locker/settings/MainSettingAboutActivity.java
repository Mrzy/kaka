
package cn.zmdx.kaka.fast.locker.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.zmdx.kaka.fast.locker.R;

import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

public class MainSettingAboutActivity extends BaseActivity implements OnClickListener {

    private LinearLayout mFeedBackLayout;

    private LinearLayout mCheckNewVersionLayout;

    private LinearLayout mAboutFastLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_about);
        initView();
    }

    private void initView() {
        mFeedBackLayout = (LinearLayout) this.findViewById(R.id.fast_setting_feedback);
        mFeedBackLayout.setOnClickListener(this);
        mCheckNewVersionLayout = (LinearLayout) this.findViewById(R.id.fast_setting_update);
        mCheckNewVersionLayout.setOnClickListener(this);
        mAboutFastLayout = (LinearLayout) this.findViewById(R.id.fast_setting_about);
        mAboutFastLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        // 反馈
            case R.id.fast_setting_feedback:
                gotoFeedBack();
                break;
            // 检查更新
            case R.id.fast_setting_update:
                checkNewVersion();
                break;
            // 关于
            case R.id.fast_setting_about:
                gotoAboutFast();
                break;

            default:
                break;
        }
    }

    private void gotoFeedBack() {
        Intent intent = new Intent();
        intent.setClass(MainSettingAboutActivity.this, FeedbackActivity.class);
        startActivity(intent);
        this.overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
                R.anim.umeng_fb_slide_out_from_left);
    }

    private void gotoAboutFast() {
        Intent intent = new Intent();
        intent.setClass(MainSettingAboutActivity.this, MAboutActivity.class);
        startActivity(intent);
        this.overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
                R.anim.umeng_fb_slide_out_from_left);
    }

    private void checkNewVersion() {
        isUpdate();
    }

    private void isUpdate() {
        UmengUpdateAgent.forceUpdate(this);
        UmengUpdateAgent.setUpdateAutoPopup(false);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                switch (updateStatus) {
                    case 0: // has update
                        UmengUpdateAgent
                                .showUpdateDialog(MainSettingAboutActivity.this, updateInfo);
                        break;
                    case 1: // has no update
                        Toast.makeText(MainSettingAboutActivity.this,
                                getResources().getString(R.string.update_prompt_no_update),
                                Toast.LENGTH_LONG).show();
                        break;
                    case 2: // none wifi
                        Toast.makeText(MainSettingAboutActivity.this,
                                getResources().getString(R.string.update_prompt_no_wify),
                                Toast.LENGTH_LONG).show();
                        break;
                    case 3: // time out
                        Toast.makeText(MainSettingAboutActivity.this,
                                getResources().getString(R.string.update_prompt_no_internet),
                                Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }
}
