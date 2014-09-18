
package cn.zmdx.kaka.locker.database;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;

public class ServerImageDataModel {
    private MySqlitDatabase mMySqlitDatabase;

    private static ServerImageDataModel sServerImageDataModel = null;

    private ServerImageDataModel() {
        mMySqlitDatabase = MySqlitDatabase.getInstance(HDApplication.getInstannce(),
                PandoraConfig.DATABASE_NAME, null);
    }

    public synchronized void close() {
        mMySqlitDatabase.close();
        mMySqlitDatabase = null;
        sServerImageDataModel = null;
    }

    public static synchronized ServerImageDataModel getInstance() {
        if (sServerImageDataModel == null) {
            sServerImageDataModel = new ServerImageDataModel();
        }
        return sServerImageDataModel;
    }

    public void saveServerImageData(List<ServerImageData> list) {
        SQLiteDatabase mysql = mMySqlitDatabase.getWritableDatabase();
        try {
            mysql.beginTransaction();
            SQLiteStatement sqLiteStatement = mysql.compileStatement("replace INTO "
                    + TableStructure.TABLE_NAME_SERVER_IMAGE + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
            for (ServerImageData bd : list) {
                sqLiteStatement.clearBindings();
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_TWO, bd.getCloudId());
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_THREE, bd.getTitle());
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_FOUR, bd.getUrl());
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_FIVE, bd.getImageUrl());
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_SIX, bd.getDataType());
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_SEVEN, bd.getTop());
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_EIGHT, bd.getSetp());
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_NINE, bd.getCollectTime());
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_TEN, bd.getReleaseTime());
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_ELEVEN, bd.getCollectWebsite());
                sqLiteStatement.bindLong(MySqlitDatabase.INDEX_TWELVE, bd.isImageDownloaded());
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
