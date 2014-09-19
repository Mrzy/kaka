
package cn.zmdx.kaka.locker.content;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.utils.FileHelper;
import cn.zmdx.kaka.locker.utils.HDBHashUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.text.TextUtils;

public class DiskImageHelper {

    private static File mStorageDir;

    static {
        mStorageDir = initStorageDir();
    }

    private static File initStorageDir() {
        File file = HDApplication.getInstannce().getExternalFilesDir(null);
        if (file == null) {
            file = HDApplication.getInstannce().getCacheDir();
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
        return BitmapFactory.decodeFile(file.getAbsolutePath(), option);
    }

    public static void remove(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        FileHelper.deleteFile(getFileByUrl(url));
    }

    public static int getFileCountOnDisk() {
        return mStorageDir.listFiles().length;
    }
    
    public static String getHash(String url) {
        return HDBHashUtils.getStringMD5(url);
    }
}
