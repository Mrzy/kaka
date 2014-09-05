
package cn.zmdx.kaka.locker.database;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.content.BaiduDataManager.BaiduData;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;

/**
 * 操作数据库方法
 */
public class DatabaseModel {
    private static final int DATABASE_VERSION = 1;

    private static final int INDEX_ONE = 1;

    private static final int INDEX_TWO = 2;

    private static final int INDEX_THREE = 3;

    private static final int INDEX_FOUR = 4;

    private static final int INDEX_FIVE = 5;

    private static final int INDEX_SIX = 6;

    private static final int INDEX_SEVEN = 7;

    private static final int INDEX_EIGHT = 8;

    private static final int INDEX_NINE = 9;

    private static final int INDEX_TEN = 10;

    private static final int INDEX_ELEVEN = 11;

    private static final int INDEX_TWELVE = 12;

    private MySqlitDatabase mMySqlitDatabase;

    private static DatabaseModel sDatabaseModel = null;

    private DatabaseModel() {
        mMySqlitDatabase = new MySqlitDatabase(HDApplication.getInstannce(),
                PandoraConfig.DATABASE_NAME, null, DATABASE_VERSION);
    }

    public synchronized void close() {
        mMySqlitDatabase.close();
        mMySqlitDatabase = null;
        sDatabaseModel = null;
    }

    public static synchronized DatabaseModel getInstance() {
        if (sDatabaseModel == null) {
            sDatabaseModel = new DatabaseModel();
        }
        return sDatabaseModel;
    }

    public synchronized void saveBaiduData(List<BaiduData> list) {
        SQLiteDatabase mysql = mMySqlitDatabase.getWritableDatabase();
        try {
            mysql.beginTransaction();
            SQLiteStatement sqLiteStatement = mysql.compileStatement("replace INTO "
                    + TableStructure.TABLE_NAME_CONTENT + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
            for (BaiduData bd : list) {
                sqLiteStatement.clearBindings();
                sqLiteStatement.bindString(INDEX_TWO, bd.getBaiduId());
                sqLiteStatement.bindString(INDEX_THREE, bd.getDescribe());
                sqLiteStatement.bindString(INDEX_FOUR, bd.getImageUrl());
                sqLiteStatement.bindLong(INDEX_FIVE, bd.getImageWidth());
                sqLiteStatement.bindLong(INDEX_SIX, bd.getImageHeight());
                sqLiteStatement.bindLong(INDEX_SEVEN, bd.isImageDownloaded());
                sqLiteStatement.bindString(INDEX_EIGHT, bd.getTthumbLargeUrl());
                sqLiteStatement.bindLong(INDEX_NINE, bd.getThumbLargeWidth());
                sqLiteStatement.bindLong(INDEX_TEN, bd.getThumbLargeHeight());
                sqLiteStatement.bindString(INDEX_ELEVEN, bd.getTag1());
                sqLiteStatement.bindString(INDEX_TWELVE, bd.getTag2());
                sqLiteStatement.executeInsert();
            }
            mysql.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mysql.endTransaction();
        }
    }

}
