
package cn.zmdx.kaka.fast.locker.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class HDBHashUtils {
    public interface IHashState {
        public void onProgress(String file, long processed, long total);

        public void onFinish(String file, String hash);
    }

    public static String getStringMD5(String input) {
        return getBytesHash("MD5", input.getBytes());
    }

    public static String getStringSHA1(String input) {
        return getBytesHash("SHA1", input.getBytes());
    }

    public static String getStringUTF8MD5(String input) {
        try {
            return getBytesHash("MD5", input.getBytes("utf8"));
        } catch (UnsupportedEncodingException e) {
            return getBytesHash("MD5", input.getBytes());
        }
    }

    public static String getStringUTF8SHA1(String input) {
        try {
            return getBytesHash("SHA1", input.getBytes("utf8"));
        } catch (UnsupportedEncodingException e) {
            return getBytesHash("SHA1", input.getBytes());
        }
    }

    public static String getBytesMD5(byte[] input) {
        return getBytesHash("MD5", input);
    }

    public static String getBytesSHA1(byte[] input) {
        return getBytesHash("SHA1", input);
    }

    public static void updateDigest(MessageDigest md, long val) {
        byte[] vals = new byte[8]; // 8 bytes for long
        for (int i = 0; i < vals.length; i++) {
            vals[i] = (byte) ((val >> (4 * i)) & 0xff);
        }
        md.update(vals);
    }

    public static void updateDigest(MessageDigest md, int val) {
        byte[] vals = new byte[4]; // 4 bytes for int
        for (int i = 0; i < vals.length; i++) {
            vals[i] = (byte) ((val >> (4 * i)) & 0xff);
        }
        md.update(vals);
    }

    private static String getBytesHash(String algo, byte[] input) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(algo);
            md.reset();
            md.update(input);
            byte b[] = md.digest();

            return binaryToHexString(b);
        } catch (NoSuchAlgorithmException e) {
            return String.valueOf(input.hashCode());
        }
    }

    public static void getFileMD5(String file, IHashState cb) {
        getFileHash("MD5", file, cb);
    }

    public static void getFileSHA1(String file, IHashState cb) {
        getFileHash("SHA1", file, cb);
    }

    private static void getFileHash(final String algo, final String file, final IHashState cb) {
        if (cb == null) {
            return;
        }

        final File f = new File(file);
        if (!f.exists() || !f.canRead()) {
            cb.onFinish(file, null);
            return;
        }

        HDBThreadUtils.runOnWorker(new Runnable() {
            @Override
            public void run() {
                MessageDigest md;
                try {
                    md = MessageDigest.getInstance(algo);
                    md.reset();

                    InputStream is = new FileInputStream(f);
                    byte[] buffer = new byte[4 * 1024]; // optimal size is 4K
                    long available = is.available();
                    long processed = 0;

                    int read = 0;
                    do {
                        read = is.read(buffer);
                        if (read > 0) {
                            md.update(buffer, 0, read);
                            processed += read;
                            cb.onProgress(file, processed, available);
                        }
                    } while (read > 0);

                    is.close();

                    byte[] b = md.digest();
                    cb.onFinish(file, binaryToHexString(b));
                } catch (Exception e) {
                    cb.onFinish(file, null);
                }
            }
        });
    }

    public static String binaryToHexString(byte[] messageDigest) {
        // Create Hex String
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < messageDigest.length; i++) {
            int by = 0xFF & messageDigest[i];
            if (by < 0x10) {
                hexString.append("0").append(Integer.toHexString(by));
            } else if (by >= 0x10) {
                hexString.append(Integer.toHexString(by));
            }
        }
        return hexString.toString();
    }

    private static PackageManager sPM = null;

    public static String getApkPublicKey(Context ctx, String algo, String pkgName, int sigID) {
        if (sPM == null) {
            sPM = ctx.getPackageManager();
        }

        try {
            PackageInfo pi = sPM.getPackageInfo(pkgName, PackageManager.GET_SIGNATURES);
            return getBytesHash(algo, pi.signatures[sigID].toByteArray());
        } catch (Exception e) {
            return null;
        }
    }

    public static String getApkPublicKeyMD5(Context ctx, String pkgName) {
        return getApkPublicKey(ctx, "MD5", pkgName, 0);
    }

    public static String getApkPublicKeySHA1(Context ctx, String pkgName) {
        return getApkPublicKey(ctx, "SHA1", pkgName, 0);
    }
}
