
package cn.zmdx.kaka.locker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.utils.HDBLOG;

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
        TableStructure.createContentTable(db);
        TableStructure.createServerTable(db);
        TableStructure.createServerImageTable(db);
        db.execSQL("create unique index if not exists baiduId_index on "
                + TableStructure.TABLE_NAME_CONTENT + "(" + TableStructure.CONTENT_BAIDU_ID + ")");
        db.execSQL("create unique index if not exists cloudId_index on "
                + TableStructure.TABLE_NAME_SERVER + "(" + TableStructure.SERVER_CLOUD_ID + ")");
        db.execSQL("create unique index if not exists cloudId_img_index on "
                + TableStructure.TABLE_NAME_SERVER_IMAGE + "(" + TableStructure.SERVER_CLOUD_ID
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (BuildConfig.DEBUG) {
            HDBLOG.logD("oldVersion : " + oldVersion + "newVersion : " + newVersion);
        }
        switch (oldVersion) {
            case 1:

                break;

            default:
                break;
        }
    }

}
