
package cn.zmdx.kaka.locker.content;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.text.TextUtils;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.utils.BaseInfoHelper;
import cn.zmdx.kaka.locker.utils.FileHelper;
import cn.zmdx.kaka.locker.utils.HDBHashUtils;
import cn.zmdx.kaka.locker.utils.ImageUtils;

public class DiskImageHelper {

    private static File mStorageDir;

    static {
        mStorageDir = initStorageDir();
    }

    private static File initStorageDir() {
        File file = HDApplication.getContext().getExternalFilesDir(null);
        if (file == null) {
            file = HDApplication.getContext().getCacheDir();
        }
        file = new File(file + File.separator + "picture");
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

    public static Bitmap getBitmapByUrl(String url, Options option) {
        File file = getFileByUrl(url);
        if (file == null) {
            return null;
        }
        if (option == null) {
            option = new Options();
            option.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getAbsolutePath(), option);
            option.inSampleSize = ImageUtils.computeSampleSize(option,
                    BaseInfoHelper.getWidth(HDApplication.getContext()));
            option.inJustDecodeBounds = false;
            option.inPreferredConfig = Bitmap.Config.RGB_565;
        }
        try {
            return BitmapFactory.decodeFile(file.getAbsolutePath(), option);
        } catch (OutOfMemoryError error) {
            return null;
        }
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

    public static String getHash(String url) {
        return HDBHashUtils.getStringMD5(url);
    }

    public static void clear() {
        File[] files = mStorageDir.listFiles();
        for (File f : files) {
            f.delete();
        }
    }
}
