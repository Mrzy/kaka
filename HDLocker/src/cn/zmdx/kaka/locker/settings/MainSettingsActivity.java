
package cn.zmdx.kaka.locker.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.service.PandoraService;

import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

public class MainSettingsActivity extends FragmentActivity {

    private Intent mServiceIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mServiceIntent = new Intent(getApplicationContext(), PandoraService.class);
        startService(mServiceIntent);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        MobclickAgent.openActivityDurationTrack(false);
        UmengUpdateAgent.update(this);
        UmengUpdateAgent.silentUpdate(this);
        String manufacturer = android.os.Build.MANUFACTURER;// 获取制造商名字
        setContentView(R.layout.main_setting_activity);
//        BaiduDataManager bdm =  new BaiduDataManager(); 
//        bdm.pullAllFunnyData();
//        List<BaiduData> list=DatabaseModel.getInstance().queryNonImageData(1, 10);
//        for(int i=0;i<list.size();i++){
//            Log.i("zlflf", list.get(i).mBaiduId);
//        }
        

        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, new MainSettingsFragment()).commit();

        // DisplayMetrics metric = new DisplayMetrics();
        // getWindowManager().getDefaultDisplay().getMetrics(metric);
        // int width = metric.widthPixels; // 屏幕宽度（像素）
        // int height = metric.heightPixels; // 屏幕高度（像素）
        // float density = metric.density; // 屏幕密度（0.75 / 1.0 / 1.5）
        // int densityDpi = metric.densityDpi; // 屏幕密度DPI（120 / 160 / 240）
        // Log.d("syc",
        // "width="+width+" height="+height+" densityDpi="+densityDpi+" density="+density);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
