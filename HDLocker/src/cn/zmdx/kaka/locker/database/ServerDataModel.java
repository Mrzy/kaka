
package cn.zmdx.kaka.locker.database;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.content.ServerDataManager.ServerData;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;

public class ServerDataModel {
    private MySqlitDatabase mMySqlitDatabase;

    private static ServerDataModel sServerDataModel = null;

    private ServerDataModel() {
        mMySqlitDatabase = MySqlitDatabase.getInstance(HDApplication.getInstannce(),
                PandoraConfig.DATABASE_NAME, null);
    }

    public synchronized void close() {
        mMySqlitDatabase.close();
        mMySqlitDatabase = null;
        sServerDataModel = null;
    }

    public static synchronized ServerDataModel getInstance() {
        if (sServerDataModel == null) {
            sServerDataModel = new ServerDataModel();
        }
        return sServerDataModel;
    }

    public void saveServerData(List<ServerData> list) {
        SQLiteDatabase mysql = mMySqlitDatabase.getWritableDatabase();
        try {
            mysql.beginTransaction();
            SQLiteStatement sqLiteStatement = mysql.compileStatement("replace INTO "
                    + TableStructure.TABLE_NAME_SERVER + " VALUES(?,?,?,?,?,?,?,?,?,?)");
            for (ServerData bd : list) {
                sqLiteStatement.clearBindings();
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_TWO, bd.getCloudId());
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_THREE, bd.getTitle());
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_FOUR, bd.getContent());
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_FIVE, bd.getDataType());
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_SIX, bd.getCollectWebsite());
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_SEVEN, bd.getTop());
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_EIGHT, bd.getSetp());
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_NINE, bd.getCollectTime());
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_TEN, bd.getReleaseTime());
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
