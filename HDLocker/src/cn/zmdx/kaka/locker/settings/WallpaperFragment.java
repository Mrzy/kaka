
package cn.zmdx.kaka.locker.settings;

import it.carlom.stikkyheader.core.StikkyHeaderBuilder;
import it.carlom.stikkyheader.core.StikkyHeaderBuilder.IOnLoadMoreData;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import cn.zmdx.kaka.locker.R;
import cn.zmdx.kaka.locker.event.UmengCustomEventManager;
import cn.zmdx.kaka.locker.layout.PandoraLayoutActivity;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.settings.config.PandoraUtils;
import cn.zmdx.kaka.locker.wallpaper.OnlineWallpaperView;
import cn.zmdx.kaka.locker.wallpaper.OnlineWallpaperView.IOnlineWallpaperListener;
import cn.zmdx.kaka.locker.wallpaper.ServerOnlineWallpaperManager.ServerOnlineWallpaper;
import cn.zmdx.kaka.locker.widget.SwitchButton;
import cn.zmdx.kaka.locker.widget.TypefaceTextView;

import com.umeng.analytics.MobclickAgent;

public class WallpaperFragment extends Fragment implements OnClickListener, OnCheckedChangeListener {

    private View mEntireView;

    private SwitchButton mGravitySenorSButton;

    private SwitchButton mAutoChangeSButton;

    private LinearLayout mLocalWallpaper;

    private LinearLayout mWallpaperLayout;

    private TypefaceTextView mWallpaperLayoutPrompt;

    private boolean isShowNew;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        mEntireView = inflater.inflate(R.layout.wallpaper_fragment, container, false);
        isShowNew = PandoraConfig.newInstance(getActivity()).isShowPromptNew();
        initView();
        initSwitchButtonState();
        return mEntireView;
    }

    private void initView() {
        mGravitySenorSButton = (SwitchButton) mEntireView
                .findViewById(R.id.wallpaper_open_gravity_sensor_switch_button);
        mGravitySenorSButton.setOnCheckedChangeListener(this);
        mAutoChangeSButton = (SwitchButton) mEntireView
                .findViewById(R.id.wallpaper_auto_change_switch_button);
        mAutoChangeSButton.setOnCheckedChangeListener(this);

        mLocalWallpaper = (LinearLayout) mEntireView
                .findViewById(R.id.wallpaper_local_wallpaper_item);
        mLocalWallpaper.setOnClickListener(this);

        mWallpaperLayout = (LinearLayout) mEntireView.findViewById(R.id.wallpaper_layout_item);
        mWallpaperLayout.setOnClickListener(this);

        mWallpaperLayoutPrompt = (TypefaceTextView) mEntireView
                .findViewById(R.id.wallpaper_layout_item_new);
        if (isShowNew) {
            mWallpaperLayoutPrompt.setVisibility(View.VISIBLE);
        }

        initWallpaperListView();
    }

    private void initSwitchButtonState() {
        mGravitySenorSButton.setChecked(isGravitySenorOn());
        mAutoChangeSButton.setChecked(isAutoChangeOn());
    }

    private void initWallpaperListView() {
        LinearLayout view = (LinearLayout) mEntireView.findViewById(R.id.setting_online_wallpaper);
        final OnlineWallpaperView onlineWallpaperView = new OnlineWallpaperView(getActivity());
        onlineWallpaperView.setOnlineWallpaperListener(new IOnlineWallpaperListener() {

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
        view.addView(onlineWallpaperView, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        RecyclerView mRecyclerView = onlineWallpaperView.getRecyclerView();
        StikkyHeaderBuilder.stickTo(mRecyclerView)
                .setHeader(R.id.wallpaper_top_layout, (ViewGroup) mEntireView)
                .build(new IOnLoadMoreData() {

                    @Override
                    public void onLoadMore() {
                        onlineWallpaperView.loadMore();
                    }
                });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == mGravitySenorSButton) {
            if (isChecked) {
                enableGravitySenorState();
                UmengCustomEventManager.statisticalOpenGravitySenorTimes();
            } else {
                disableGravitySenorState();
                UmengCustomEventManager.statisticalCloseGravitySenorTimes();
            }
        } else if (buttonView == mAutoChangeSButton) {
            if (isChecked) {
                UmengCustomEventManager.statisticalOpenAutoChangeWallpaper();
            } else {
                UmengCustomEventManager.statisticalCloseAutoChangeWallpaper();
            }
            saveAutoChangeState(isChecked);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("WallpaperFragment");
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("WallpaperFragment");
    }

    @Override
    public void onClick(View v) {
        if (v == mLocalWallpaper) {
            PandoraUtils.gotoGalleryActivity(getActivity(), PandoraUtils.REQUEST_CODE_GALLERY);
        } else if (v == mWallpaperLayout) {
            Intent in = new Intent(getActivity(), PandoraLayoutActivity.class);
            getActivity().startActivity(in);
            getActivity().overridePendingTransition(R.anim.umeng_fb_slide_in_from_right,
                    R.anim.umeng_fb_slide_out_from_left);
            if (isShowNew) {
                mWallpaperLayoutPrompt.setVisibility(View.GONE);
                PandoraConfig.newInstance(getActivity()).savePromptNewState(false);
            }
        }
    }

    private boolean isGravitySenorOn() {
        return PandoraConfig.newInstance(getActivity()).isGravitySenorOn();
    }

    private void enableGravitySenorState() {
        PandoraConfig.newInstance(getActivity()).saveGravitySenorState(true);
    }

    private void disableGravitySenorState() {
        PandoraConfig.newInstance(getActivity()).saveGravitySenorState(false);
    }

    private boolean isAutoChangeOn() {
        return PandoraConfig.newInstance(getActivity()).isAutoChangeOn();
    }

    private void saveAutoChangeState(boolean isEnable) {
        PandoraConfig.newInstance(getActivity()).saveAutoChangeState(isEnable);
    }

}
