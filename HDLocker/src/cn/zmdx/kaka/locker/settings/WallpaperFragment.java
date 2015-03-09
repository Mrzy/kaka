
package cn.zmdx.kaka.locker.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.zmdx.kaka.locker.R;

public class WallpaperFragment extends Fragment {
    private View mEntireView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        mEntireView = inflater.inflate(R.layout.wallpaper_fragment, container, false);
        initView();
        return mEntireView;
    }

    private void initView() {

    }
}
