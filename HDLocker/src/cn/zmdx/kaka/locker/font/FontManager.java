
package cn.zmdx.kaka.locker.font;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.text.TextUtils;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.FileHelper;
import cn.zmdx.kaka.locker.utils.HDBHashUtils;

public class FontManager {
    private static final String FONT_FILE_EXTENSION = ".ttf";

    private static SoftReference<Typeface> sTypeface = null;

    private static File mStorageDir;

    private Context mContext;

    static {
        mStorageDir = initStorageDir();
    }

    public static Typeface getChineseTypeface(Context context) {
        AssetManager mgr = context.getResources().getAssets();
        if (null == sTypeface || sTypeface.get() == null) {
            sTypeface = new SoftReference<Typeface>(Typeface.createFromAsset(mgr,
                    "fonts/ltxh_GBK_Mobil.TTF"));
        }
        return sTypeface.get();
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

            sTypeface = new SoftReference<Typeface>(Typeface.createFromFile(fontFileName));
        }
        return sTypeface.get();
    }

    private static File initStorageDir() {
        File file = HDApplication.getInstannce().getExternalFilesDir(null);
        if (file == null) {
            file = HDApplication.getInstannce().getCacheDir();
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

    public void downloadTypeface(String url) {
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
