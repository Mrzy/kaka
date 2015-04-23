
package cn.zmdx.kaka.locker.font;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.RequestManager;
import cn.zmdx.kaka.locker.network.DownloadRequest;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.FileHelper;
import cn.zmdx.kaka.locker.utils.HDBHashUtils;
import cn.zmdx.kaka.locker.utils.HDBLOG;
import cn.zmdx.kaka.locker.utils.HDBNetworkState;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.error.VolleyError;

public class FontManager {
    private static final String FONT_FILE_EXTENSION = ".ttf";

    private static SoftReference<Typeface> sTypeface = null;

    private static File mStorageDir;

    static {
        mStorageDir = initStorageDir();
    }

    /**
     * 如果没有设置使用第三方字体，返回null；否则返回Typeface对象
     * 
     * @param context
     * @return
     */
    public static Typeface getCurrentTypeface(Context context) {
        if (null == sTypeface || sTypeface.get() == null) {
            String fontFileName = PandoraConfig.newInstance(context).getCurrentFont();
            if (TextUtils.isEmpty(fontFileName)) {
                return null;
            }
            File file = new File(fontFileName);
            if (!file.exists() || !file.isFile()) {
                return null;
            }

            sTypeface = new SoftReference<Typeface>(Typeface.createFromFile(fontFileName));
        }
        return sTypeface.get();
    }

    public void saveCurrentTypeface(Context context, String fontFilePath) {
        PandoraConfig.newInstance(context).saveCurrentFont(fontFilePath);
    }

    private static File initStorageDir() {
        File file = HDApplication.getContext().getExternalFilesDir(null);
        if (file == null) {
            file = HDApplication.getContext().getCacheDir();
        }
        file = new File(file + File.separator + "fonts");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getStorageDir() {
        return mStorageDir;
    }

    public static String getStoragePath() {
        return mStorageDir.getAbsolutePath();
    }

    public static void put(String url, byte[] data) {
        File file = new File(mStorageDir, getHash(url));
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File getFileByUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        return new File(mStorageDir, getHash(url));
    }

    /**
     * 根据网络字体的url从本地返回字体文件。如果字体文件在本地不存在会返回null
     * 
     * @param url 如果url为null会抛出空指针异常
     * @return Typeface对象或者null
     */
    public static Typeface getTypefaceByUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            throw new NullPointerException("url must not be null");
        }
        File file = getFileByUrl(url);
        if (!file.exists()) {
            return null;
        }
        Typeface typeface = Typeface.createFromFile(file);
        return typeface;
    }

    /**
     * 此方法较耗时，不要在UI线程调用该方法
     * 
     * @return
     */
    public static List<Typeface> getAllTypefaces() {
        List<Typeface> result = new ArrayList<Typeface>();
        final File[] files = mStorageDir.listFiles();
        for (File file : files) {
            if (file.getName().endsWith(FONT_FILE_EXTENSION))
                result.add(Typeface.createFromFile(file));
        }
        return result;
    }

    public interface IDownloadTypefaceListener {
        void onSuccess(File file);

        void onFailed();
    }

    public void downloadTypeface(String url, final IDownloadTypefaceListener listener) {
        String fileName = getHash(url);
        File file = new File(mStorageDir, fileName);
        if (file.exists()) {
            if (listener != null) {
                listener.onSuccess(file);
            }
            return;
        }

        if (!HDBNetworkState.isNetworkAvailable()) {
            if (listener != null) {
                listener.onFailed();
            }
            return;
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
                HDBLOG.logD("下载字体失败。由于创建字体文件时异常，中断下载");
            }
            if (listener != null) {
                listener.onFailed();
            }
            return;
        }
        DownloadRequest requset = new DownloadRequest(url, file.getAbsolutePath(),
                new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (BuildConfig.DEBUG) {
                            HDBLOG.logD("下载字体文件成功，路径：" + response);
                        }
                        if (listener != null) {
                            listener.onSuccess(new File(response));
                        }
                    }
                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (BuildConfig.DEBUG) {
                            HDBLOG.logD("下载字体文件失败，error：" + error.getMessage());
                        }
                        if (listener != null) {
                            listener.onFailed();
                        }
                    }
                });
        RequestManager.getRequestQueue().add(requset);
    }

    public static void remove(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        FileHelper.deleteFile(getFileByUrl(url));
    }

    public static int getFileCountOnDisk() {
        File[] files = mStorageDir.listFiles();
        if (files != null) {
            return files.length;
        }
        return 0;
    }

    public static Map<String, WeakReference<Typeface>> sTypeCache = new HashMap<String, WeakReference<Typeface>>();

    public static Typeface getTypeface(String path) {
        if (sTypeCache.containsKey(path)) {
            WeakReference<Typeface> typeface = sTypeCache.get(path);
            Typeface tf = typeface.get();
            if (tf != null) {
                return tf;
            }
        }
        final Typeface newTf = Typeface.createFromAsset(HDApplication.getContext().getAssets(), path);
        sTypeCache.put(path, new WeakReference<Typeface>(newTf));
        return newTf;
    }

    private static String getHash(String url) {
        return HDBHashUtils.getStringMD5(url) + FONT_FILE_EXTENSION;
    }

    public static void clear() {
        File[] files = mStorageDir.listFiles();
        for (File f : files) {
            f.delete();
        }
    }
}
