
package cn.zmdx.kaka.locker.guide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.settings.InitSettingActivity;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;

import com.umeng.analytics.MobclickAgent;

/**
 * 引导界面
 */
public abstract class BaseGuideActivity extends Activity {

    private boolean isMeizu = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        isMeizu = PandoraUtils.isMeizu(this);
        View contentView = getContentView();
        Button nextButton = getNextButton();
        if (contentView == null || nextButton == null) {
            throw new IllegalStateException(
                    "You must implement abstract method getContentView() and getNextButton()");
        }
        if (isMeizu) {
             nextButton.setText(getResources().getString(R.string.pandora_guide_start));
        }
        setContentView(contentView);

        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setGuided();
                goHome();
                finish();
                onFinish();
                PandoraConfig.newInstance(BaseGuideActivity.this).savePandolaLockerState(true);
            }
        });
    }

    /**
     * 返回本页的UI
     * 
     * @return
     */
    protected abstract View getContentView();

    /**
     * 需要返回一个Button对象，用于点击后进入下一页
     * 
     * @return
     */
    protected abstract Button getNextButton();

    private void goHome() {
        Intent intent = new Intent(this, InitSettingActivity.class);
        intent.putExtra("isFirst", true);
        startActivity(intent);
        finish();
        onFinish();
    }

    /**
     * 此Activity被finish后会调用该方法
     */
    protected abstract void onFinish();

    /**
     * 设置已经引导过了，下次启动不用再次引导
     */
    private void setGuided() {
        PandoraConfig.newInstance(this).saveHasGuided();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("GuideActivity"); // 统计页面
        MobclickAgent.onResume(this); // 统计时长
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("GuideActivity"); // 保证 onPageEnd 在onPause
                                                  // 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }
}
