
package cn.zmdx.kaka.fast.locker.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import cn.zmdx.kaka.fast.locker.HDApplication;
import cn.zmdx.kaka.fast.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.fast.locker.shortcut.AppInfo;
import cn.zmdx.kaka.fast.locker.utils.ImageUtils;

public class ShortcutModel {

    private MySqlitDatabase mMySqlitDatabase;

    private Context mContext;

    private static ShortcutModel INSTANCE;

    private ShortcutModel(Context context) {
        mContext = context;
        mMySqlitDatabase = MySqlitDatabase.getInstance(HDApplication.getContext(),
                PandoraConfig.DATABASE_NAME, null);
    }

    public static synchronized ShortcutModel getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ShortcutModel(context);
        }
        return INSTANCE;
    }

    public boolean insert(AppInfo ai) {
        SQLiteDatabase db = mMySqlitDatabase.getWritableDatabase();
        if (!TextUtils.isEmpty(ai.getPkgName())) {
            ContentValues cv = new ContentValues();
            cv.put(TableStructure.SCUT_PKG, ai.getPkgName());
            cv.put(TableStructure.SCUT_APPNAME, ai.getAppName());
            cv.put(TableStructure.SCUT_DISGUISE, ai.isDisguise() ? AppInfo.DISGUISE
                    : AppInfo.UNDISGUISE);
            cv.put(TableStructure.SCUT_POSITION, ai.getPosition());
            if (ai.isDisguise()) {
                cv.put(TableStructure.SCUT_DISGUISE_DRAWABLE, ImageUtils.drawable2ByteArray(
                        ai.getDisguiseDrawable(), Bitmap.CompressFormat.PNG));
            }
            return db.insert(TableStructure.TABLE_NAME_SHORTCUT, null, cv) != -1;
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    public List<AppInfo> queryAll() {
        List<AppInfo> data = new ArrayList<AppInfo>();
        SQLiteDatabase db = mMySqlitDatabase.getReadableDatabase();
        Cursor cursor = db.query(TableStructure.TABLE_NAME_SHORTCUT, new String[] {
                TableStructure.SCUT_ID, TableStructure.SCUT_APPNAME, TableStructure.SCUT_PKG,
                TableStructure.SCUT_DISGUISE, TableStructure.SCUT_DISGUISE_DRAWABLE,
                TableStructure.SCUT_POSITION
        }, null, null, null, null, null);
        while (cursor.moveToNext()) {
            AppInfo ai = new AppInfo();
            ai.setId(cursor.getInt(0));
            ai.setAppName(cursor.getString(1));
            ai.setPkgName(cursor.getString(2));
            ai.setDisguise(cursor.getInt(3) == AppInfo.DISGUISE);
            byte[] drawable = cursor.getBlob(4);
            if (drawable != null) {
                ai.setDisguiseDrawable(new BitmapDrawable(BitmapFactory.decodeByteArray(drawable,
                        0, drawable.length)));
            }
            ai.setPosition(cursor.getInt(5));
            ai.setDefaultIcon(AppInfo.getIconByPkgName(mContext, ai.getPkgName()));
            data.add(ai);
        }
        cursor.close();
        return data;
    }

    public int deleteById(int id) {
        SQLiteDatabase db = mMySqlitDatabase.getWritableDatabase();
        return db.delete(TableStructure.TABLE_NAME_SHORTCUT, TableStructure.SCUT_ID + "=?",
                new String[] {
                    String.valueOf(id)
                });
    }

    public int deleteByPkgName(String pkgName) {
        SQLiteDatabase db = mMySqlitDatabase.getWritableDatabase();
        return db.delete(TableStructure.TABLE_NAME_SHORTCUT, TableStructure.SCUT_PKG + "=?",
                new String[] {
                    pkgName
                });
    }

    public boolean existByPkgName(String pkgName) {
        SQLiteDatabase db = mMySqlitDatabase.getReadableDatabase();
        Cursor cursor = db.query(TableStructure.TABLE_NAME_SHORTCUT, new String[] {
            TableStructure.SCUT_ID
        }, TableStructure.SCUT_PKG + "=?", new String[] {
            pkgName
        }, null, null, null);
        boolean result = cursor.moveToNext();
        cursor.close();
        return result;
    }

    public int deleteAll() {
        SQLiteDatabase db = mMySqlitDatabase.getWritableDatabase();
        return db.delete(TableStructure.TABLE_NAME_SHORTCUT, null, null);
    }
}
