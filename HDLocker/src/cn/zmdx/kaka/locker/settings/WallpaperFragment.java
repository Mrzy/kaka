
package cn.zmdx.kaka.locker.settings;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.theme.ThemeManager;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.wallpaper.OnlineWallpaperView;
import cn.zmdx.kaka.locker.wallpaper.OnlineWallpaperView.IOnlineWallpaperListener;
import cn.zmdx.kaka.locker.wallpaper.ServerOnlineWallpaperManager.ServerOnlineWallpaper;
import cn.zmdx.kaka.locker.widget.SwitchButton;

public class WallpaperFragment extends Fragment implements OnClickListener, OnCheckedChangeListener {

    private View mEntireView;

    private SwitchButton mGravitySenorSButton;

    private SwitchButton mRandomReplacementSButton;

    private LinearLayout mLocalWallpaper;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        mEntireView = inflater.inflate(R.layout.wallpaper_fragment, container, false);
        initView();
        initSwitchButtonState();
        return mEntireView;
    }

    private void initView() {
        mGravitySenorSButton = (SwitchButton) mEntireView
                .findViewById(R.id.notify_open_gravity_sensor_switch_button);
        mGravitySenorSButton.setOnCheckedChangeListener(this);
        mRandomReplacementSButton = (SwitchButton) mEntireView
                .findViewById(R.id.notify_random_replacement_wallpaper_switch_button);
        mRandomReplacementSButton.setOnCheckedChangeListener(this);

        mLocalWallpaper = (LinearLayout) mEntireView.findViewById(R.id.notify_local_wallpaper_item);
        mLocalWallpaper.setOnClickListener(this);

        initWallpaperListView();
    }

    private void initSwitchButtonState() {
        mGravitySenorSButton.setChecked(isGravitySenorOn());
        mRandomReplacementSButton.setChecked(isRandomReplacementOn());
    }

    private void initWallpaperListView() {
        LinearLayout view = (LinearLayout) mEntireView.findViewById(R.id.setting_online_wallpaper);
        final LinearLayout detailView = (LinearLayout) mEntireView
                .findViewById(R.id.setting_online_detail_wallpaper);
        OnlineWallpaperView onlineWallpaperView = new OnlineWallpaperView(getActivity(), false);
        onlineWallpaperView.setOnlineWallpaperListener(new IOnlineWallpaperListener() {

            @Override
            public void onOpenDetailPage(View view) {
                detailView.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT));
            }

            @Override
            public void onCloseDetailPage() {
                detailView.removeAllViews();
            }

            @Override
            public void onGoToDetailClick(ServerOnlineWallpaper item) {
                Intent in = new Intent();
                in.putExtra("imageUrl", item.getImageURL());
                in.putExtra("desc", item.getDesc());
                in.setClass(getActivity(), WallpaperDetailActivity.class);
                getActivity().startActivity(in);
                getActivity().overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
                        R.anim.umeng_fb_slide_out_from_left);
            }
        });
        view.addView(onlineWallpaperView);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == mGravitySenorSButton) {
            saveGravitySenorState(isChecked);
        } else if (buttonView == mRandomReplacementSButton) {
            saveRandomReplacementState(isChecked);
        }
    }

    @Override
    public void onClick(View v) {
        PandoraUtils.gotoGalleryActivity(getActivity(), PandoraUtils.REQUEST_CODE_GALLERY);
    }

    private boolean isGravitySenorOn() {
        return PandoraConfig.newInstance(getActivity()).isGravitySenorOn();
    }

    private void saveGravitySenorState(boolean isEnable) {
        PandoraConfig.newInstance(getActivity()).saveGravitySenorState(isEnable);
    }

    private boolean isRandomReplacementOn() {
        return PandoraConfig.newInstance(getActivity()).isRandomReplacementOn();
    }

    private void saveRandomReplacementState(boolean isEnable) {
        PandoraConfig.newInstance(getActivity()).saveRandomReplacementState(isEnable);
    }

}
