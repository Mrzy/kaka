
package cn.zmdx.kaka.locker.cache;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import cn.zmdx.kaka.locker.cache.DiskLruCache.Editor;
import cn.zmdx.kaka.locker.cache.DiskLruCache.Snapshot;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.HDBHashUtils;

import com.android.volley.toolbox.ImageCache;

public class DiskImageCache implements ImageCache {

    private DiskLruCache mDiskLruCache;

    private CompressFormat mCompressFormat = CompressFormat.JPEG;

    private static int IO_BUFFER_SIZE = 8 * 1024;

    private int mCompressQuality = 80;

    public DiskImageCache(File directory, int appVersion, int valueCount, long maxSize) {
        try {
            mDiskLruCache = DiskLruCache.open(directory, appVersion, valueCount, maxSize);
        } catch (IOException e) {
            mDiskLruCache = null;
        }
    }

    public DiskImageCache(Context context, String uniqueName, long maxSize) {
        File cacheDir = new File(context.getExternalCacheDir().getPath() + File.separator
                + uniqueName);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        try {
            mDiskLruCache = DiskLruCache.open(cacheDir, BaseInfoHelper.getPkgVersionCode(context),
                    1, maxSize);
        } catch (IOException e) {
            mDiskLruCache = null;
        }
    }

    @Override
    public Bitmap getBitmap(String url) {
        try {
            Snapshot snap = mDiskLruCache.get(md5Url(url));
            if (snap != null) {
                File file = snap.getFile(0);
                if (file != null && file.exists()) {
                    try {
                        return BitmapFactory.decodeFile(file.getAbsolutePath());
                    } catch (OutOfMemoryError error) {
                        return null;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String md5Url(String url) {
        return HDBHashUtils.getStringMD5(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        Editor editor = null;
        try {
            editor = mDiskLruCache.edit(md5Url(url));
            if (editor == null) {
                return;
            }
            if (writeBitmapToFile(bitmap, editor)) {
                mDiskLruCache.flush();
                editor.commit();
            } else {
                editor.abort();
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                if (editor != null) {
                    editor.abort();
                }
            } catch (IOException ignored) {
            }
        }
    }

    private boolean writeBitmapToFile(Bitmap bitmap, DiskLruCache.Editor editor)
            throws IOException, FileNotFoundException {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(editor.newOutputStream(0), IO_BUFFER_SIZE);
            return bitmap.compress(mCompressFormat, mCompressQuality, out);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    @Override
    public void invalidateBitmap(String url) {
        // TODO Auto-generated method stub

    }

    @Override
    public void clear() {
        if (mDiskLruCache != null) {
            try {
                mDiskLruCache.delete();
            } catch (IOException e) {
            }
        }

    }

}
