package cn.zmdx.kaka.fast.locker.shortcut.sevenkey;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;


public class SettingsSystemObserver extends ContentObserver {
    public interface Listener {
        public void onSettingsSystemChanged(Context cxt);
    }

    private Context mContext;
    private ContentResolver mContentResolver;
    private Listener mCb;

    public SettingsSystemObserver(Context cxt, Handler handler) {
        super(handler);
        mContext = cxt;
        mContentResolver = cxt.getContentResolver();
    }

    public void observe(Listener cb, String[] keys) {
        mCb = cb;
        for (String key : keys) {
            Uri uri = Settings.System.getUriFor(key);
            mContentResolver.registerContentObserver(uri, false, this);
        }
    }

    public void unobserve() {
        mContentResolver.unregisterContentObserver(this);
    }

    @Override
    public void onChange(boolean selfChange) {
        if (mCb != null) {
            mCb.onSettingsSystemChanged(mContext);
        }
    }

}
