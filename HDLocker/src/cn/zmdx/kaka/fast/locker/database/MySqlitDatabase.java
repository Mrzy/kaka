
package cn.zmdx.kaka.fast.locker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import cn.zmdx.kaka.fast.locker.utils.HDBLOG;
import cn.zmdx.kaka.fast.locker.BuildConfig;

public class MySqlitDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    public static final int INDEX_ONE = 1;

    public static final int INDEX_TWO = 2;

    public static final int INDEX_THREE = 3;

    public static final int INDEX_FOUR = 4;

    public static final int INDEX_FIVE = 5;

    public static final int INDEX_SIX = 6;

    public static final int INDEX_SEVEN = 7;

    public static final int INDEX_EIGHT = 8;

    public static final int INDEX_NINE = 9;

    public static final int INDEX_TEN = 10;

    public static final int INDEX_ELEVEN = 11;

    public static final int INDEX_TWELVE = 12;

    public static final int DOWNLOAD_FALSE = 0;

    public static final int DOWNLOAD_TRUE = 1;

    public static final int FAVORITE_FALSE = 0;

    public static final int FAVORITE_TRUE = 1;

    private static MySqlitDatabase sMySqlitDatabase = null;

    public MySqlitDatabase(Context context, String name, CursorFactory factory) {
        super(context, name, factory, DATABASE_VERSION);
    }

    public static synchronized MySqlitDatabase getInstance(Context context, String name,
            CursorFactory factory) {
        if (sMySqlitDatabase == null) {
            sMySqlitDatabase = new MySqlitDatabase(context, name, factory);
        }
        return sMySqlitDatabase;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        TableStructure.createServerImageTable(db);
        TableStructure.createCustomNotificationTable(db);
        TableStructure.createShortcutTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
