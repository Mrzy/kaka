
package cn.zmdx.kaka.locker.database;

import java.util.List;

import android.database.Cursor;
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
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_EIGHT, bd.getRead());
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

    public synchronized ServerData queryByRandom() {
        ServerData sd = null;
        SQLiteDatabase sqliteDatabase = mMySqlitDatabase.getReadableDatabase();
        Cursor cursor = sqliteDatabase.query(TableStructure.TABLE_NAME_SERVER, new String[] {
                TableStructure.SERVER_ID, TableStructure.SERVER_COLLECT_WEBSITE,
                TableStructure.SERVER_CONTENT, TableStructure.SERVER_DATA_TYPE
        }, null, null, null, null, "random()", "1");
        while (cursor.moveToNext()) {
            sd = new ServerData();
            sd.setId(cursor.getInt(0));
            sd.setCollectWebsite(cursor.getString(1));
            sd.setContent(cursor.getString(2));
            sd.setDataType(cursor.getString(3));
        }
        cursor.close();
        return sd;
    }

    public synchronized int queryTotalCount() {
        SQLiteDatabase sqliteDatabase = mMySqlitDatabase.getReadableDatabase();
        int count = 0;
        Cursor cursor = sqliteDatabase.rawQuery("select count(*) from "
                + TableStructure.TABLE_NAME_SERVER, null);
        try {
            while (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return count;
    }

    public synchronized boolean deleteById(int id) {
        SQLiteDatabase sqliteDatabase = mMySqlitDatabase.getWritableDatabase();
        int count = sqliteDatabase.delete(TableStructure.TABLE_NAME_SERVER,
                TableStructure.SERVER_ID + "=?", new String[] {
                    String.valueOf(id)
                });
        return count != 0;
    }

    public ServerData queryByWebsite(String website) {
        ServerData sd = null;
        SQLiteDatabase sqliteDatabase = mMySqlitDatabase.getReadableDatabase();
        Cursor cursor = sqliteDatabase.query(TableStructure.TABLE_NAME_SERVER, new String[] {
                TableStructure.SERVER_ID, TableStructure.SERVER_COLLECT_WEBSITE,
                TableStructure.SERVER_CONTENT, TableStructure.SERVER_DATA_TYPE,
                TableStructure.SERVER_TITLE
        }, TableStructure.SERVER_COLLECT_WEBSITE + "=?", new String[] {
            website
        }, null, null, null, "1");
        while (cursor.moveToNext()) {
            sd = new ServerData();
            sd.setId(cursor.getInt(0));
            sd.setCollectWebsite(cursor.getString(1));
            sd.setContent(cursor.getString(2));
            sd.setDataType(cursor.getString(3));
            sd.setTitle(cursor.getString(4));
        }
        cursor.close();
        return sd;
    }

    public ServerData queryByDataType(String type) {
        ServerData sd = null;
        SQLiteDatabase sqliteDatabase = mMySqlitDatabase.getReadableDatabase();
        Cursor cursor = sqliteDatabase.query(TableStructure.TABLE_NAME_SERVER, new String[] {
                TableStructure.SERVER_ID, TableStructure.SERVER_COLLECT_WEBSITE,
                TableStructure.SERVER_CONTENT, TableStructure.SERVER_DATA_TYPE,
                TableStructure.SERVER_TITLE
        }, TableStructure.SERVER_DATA_TYPE + "=?", new String[] {
            type
        }, null, null, null, "1");
        try {
            while (cursor.moveToNext()) {
                sd = new ServerData();
                sd.setId(cursor.getInt(0));
                sd.setCollectWebsite(cursor.getString(1));
                sd.setContent(cursor.getString(2));
                sd.setDataType(cursor.getString(3));
                sd.setTitle(cursor.getString(4));
            }
        } catch (Exception e) {
        } finally {
            cursor.close();
        }
        return sd;
    }
}
