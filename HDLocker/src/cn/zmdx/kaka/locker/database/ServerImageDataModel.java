
package cn.zmdx.kaka.locker.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
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

    public synchronized void saveServerImageData(List<ServerImageData> list) {
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

    public synchronized int queryTotalCount() {
        SQLiteDatabase sqliteDatabase = mMySqlitDatabase.getReadableDatabase();
        int count = 0;
        Cursor cursor = sqliteDatabase.rawQuery("select count(*) from "
                + TableStructure.TABLE_NAME_SERVER_IMAGE, null);
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

    public synchronized int queryCountByType(String dataType) {
        SQLiteDatabase sqliteDatabase = mMySqlitDatabase.getReadableDatabase();
        int count = 0;
        Cursor cursor = sqliteDatabase.query(TableStructure.TABLE_NAME_SERVER_IMAGE, new String[] {
            "count(*)"
        }, TableStructure.SERVER_IMAGE_DATA_TYPE + "=?", new String[] {
            dataType
        }, null, null, null);
        try {
            while (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
        } finally {
            cursor.close();
        }
        return count;
    }

    public void markAllNonDownload() {
        SQLiteDatabase sqliteDatabase = mMySqlitDatabase.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TableStructure.SERVER_IMAGE_IS_IMAGE_DOWNLOADED, MySqlitDatabase.DOWNLOAD_FALSE);
        sqliteDatabase.update(TableStructure.TABLE_NAME_SERVER_IMAGE, values, null, null);
    }

    /**
     * 查询已下载图片的数据条数
     * 
     * @return
     */
    public synchronized int queryCountHasImage(String dataType) {
        SQLiteDatabase sqliteDatabase = mMySqlitDatabase.getReadableDatabase();
        int count = 0;
        Cursor cursor = sqliteDatabase.query(TableStructure.TABLE_NAME_SERVER_IMAGE, new String[] {
            TableStructure.SERVER_IMAGE_ID
        }, TableStructure.SERVER_IMAGE_DATA_TYPE + "=? and "
                + TableStructure.SERVER_IMAGE_IS_IMAGE_DOWNLOADED + "=?", new String[] {
                dataType, String.valueOf(MySqlitDatabase.DOWNLOAD_TRUE)
        }, null, null, null, null);

        try {
            count = cursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return count;
    }

    /**
     * 将一条数据标记为图片已下载到本地
     * 
     * @param id
     */
    public synchronized boolean markAlreadyDownload(int id) {
        SQLiteDatabase sqliteDatabase = mMySqlitDatabase.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(TableStructure.SERVER_IMAGE_IS_IMAGE_DOWNLOADED, MySqlitDatabase.DOWNLOAD_TRUE);
        int count = sqliteDatabase.update(TableStructure.TABLE_NAME_SERVER_IMAGE, values,
                TableStructure.SERVER_IMAGE_ID + "=?", new String[] {
                    String.valueOf(id)
                });
        return count != 0;
    }

    public synchronized boolean deleteById(int id) {
        SQLiteDatabase sqliteDatabase = mMySqlitDatabase.getWritableDatabase();
        int count = sqliteDatabase.delete(TableStructure.TABLE_NAME_SERVER_IMAGE,
                TableStructure.SERVER_IMAGE_ID + "=?", new String[] {
                    String.valueOf(id)
                });
        return count != 0;
    }

    public ServerImageData queryByDataType(String datatypeNews) {
        SQLiteDatabase sqliteDatabase = mMySqlitDatabase.getReadableDatabase();
        ServerImageData data = null;

        Cursor cursor = sqliteDatabase.query(TableStructure.TABLE_NAME_SERVER_IMAGE, new String[] {
                TableStructure.SERVER_IMAGE_ID, TableStructure.SERVER_IMAGE_URL,
                TableStructure.SERVER_IMAGE_IMAGE_URL, TableStructure.SERVER_IMAGE_TITLE
        }, TableStructure.SERVER_IMAGE_DATA_TYPE + "=? and "
                + TableStructure.SERVER_IMAGE_IS_IMAGE_DOWNLOADED + "=?", new String[] {
                datatypeNews, String.valueOf(MySqlitDatabase.DOWNLOAD_TRUE)
        }, null, null, null, "1");

        try {
            while (cursor.moveToNext()) {
                data = new ServerImageData();
                data.setId(cursor.getInt(0));
                data.setUrl(cursor.getString(1));
                data.setImageUrl(cursor.getString(2));
                data.setTitle(cursor.getString(3));
                data.setDataType(datatypeNews);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return data;
    }

    public ServerImageData queryByWebsite(String websiteQiubai) {
        SQLiteDatabase sqliteDatabase = mMySqlitDatabase.getReadableDatabase();
        ServerImageData data = null;

        Cursor cursor = sqliteDatabase.query(TableStructure.TABLE_NAME_SERVER_IMAGE, new String[] {
                TableStructure.SERVER_IMAGE_ID, TableStructure.SERVER_IMAGE_URL,
                TableStructure.SERVER_IMAGE_IMAGE_URL, TableStructure.SERVER_IMAGE_TITLE
        }, TableStructure.SERVER_IMAGE_COLLECT_WEBSITE + "=? and "
                + TableStructure.SERVER_IMAGE_IS_IMAGE_DOWNLOADED + "=?", new String[] {
                websiteQiubai, String.valueOf(MySqlitDatabase.DOWNLOAD_TRUE)
        }, null, null, null, "1");

        try {
            while (cursor.moveToNext()) {
                data = new ServerImageData();
                data.setId(cursor.getInt(0));
                data.setUrl(cursor.getString(1));
                data.setImageUrl(cursor.getString(2));
                data.setTitle(cursor.getString(3));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return data;
    }

    public List<ServerImageData> queryWithoutImgByDataType(int count, String type) {
        SQLiteDatabase sqliteDatabase = mMySqlitDatabase.getReadableDatabase();
        List<ServerImageData> result = new ArrayList<ServerImageData>();

        Cursor cursor = sqliteDatabase.query(TableStructure.TABLE_NAME_SERVER_IMAGE, new String[] {
                TableStructure.SERVER_IMAGE_ID, TableStructure.SERVER_IMAGE_IMAGE_URL,
                TableStructure.SERVER_IMAGE_TITLE, TableStructure.SERVER_IMAGE_URL,

        }, TableStructure.SERVER_IMAGE_DATA_TYPE + "=? and "
                + TableStructure.SERVER_IMAGE_IS_IMAGE_DOWNLOADED + "= ?", new String[] {
                type, String.valueOf(MySqlitDatabase.DOWNLOAD_FALSE)
        }, null, null, null, String.valueOf(count));

        try {
            while (cursor.moveToNext()) {
                ServerImageData data = new ServerImageData();
                data.setId(cursor.getInt(0));
                data.setImageUrl(cursor.getString(1));
                data.setTitle(cursor.getString(2));
                data.setUrl(cursor.getString(3));
                result.add(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return result;
    }
}
