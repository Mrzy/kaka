
package cn.zmdx.kaka.locker.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import cn.zmdx.kaka.locker.BuildConfig;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.content.ServerDataMapping;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.HDBLOG;

public class ServerImageDataModel {
    public static final String READ = "read";

    public static final String UN_READ = "unread";

    private MySqlitDatabase mMySqlitDatabase;

    private static ServerImageDataModel sServerImageDataModel = null;

    private ServerImageDataModel() {
        mMySqlitDatabase = MySqlitDatabase.getInstance(HDApplication.getContext(),
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
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_FIVE, bd.getImageDesc());
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_SIX, bd.getDataType());
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_SEVEN, bd.getTop());
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_EIGHT, bd.getRead());
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_NINE, bd.getCollectTime());
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_TEN, bd.getReleaseTime());
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_ELEVEN, bd.getCollectWebsite());
                if (bd.getDataType().equals(ServerDataMapping.S_DATATYPE_HTML)) {// 如果类型为html，没有图片，默认情况下，是否已下载图片字段为true
                    sqLiteStatement.bindLong(MySqlitDatabase.INDEX_TWELVE,
                            MySqlitDatabase.DOWNLOAD_TRUE);
                } else {
                    sqLiteStatement.bindLong(MySqlitDatabase.INDEX_TWELVE, bd.isImageDownloaded());
                }
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

    public void markAllNonDownloadExceptHtml() {
        SQLiteDatabase sqliteDatabase = mMySqlitDatabase.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TableStructure.SERVER_IMAGE_IS_IMAGE_DOWNLOADED, MySqlitDatabase.DOWNLOAD_FALSE);
        sqliteDatabase.update(TableStructure.TABLE_NAME_SERVER_IMAGE, values,
                TableStructure.SERVER_IMAGE_DATA_TYPE + "!=?", new String[] {
                    ServerDataMapping.S_DATATYPE_HTML
                });
    }

    /**
     * 查询已下载图片的数据条数 如果参数为null，则查询除了html类型的所有类型数据已下载图片并且未读的数量
     * 
     * @return
     */
    public synchronized int queryCountHasImageAndUnRead(String dataType) {
        String selection = null;
        String[] selectionArgu = null;
        if (!TextUtils.isEmpty(dataType)) {
            selection = TableStructure.SERVER_IMAGE_DATA_TYPE + "=? and "
                    + TableStructure.SERVER_IMAGE_IS_IMAGE_DOWNLOADED + "=? and "
                    + TableStructure.SERVER_IMAGE_SETP + "=?";
            selectionArgu = new String[] {
                    dataType, String.valueOf(MySqlitDatabase.DOWNLOAD_TRUE),
                    ServerImageDataModel.UN_READ
            };
        } else {

            selection = TableStructure.SERVER_IMAGE_DATA_TYPE + "!=? and "
                    + TableStructure.SERVER_IMAGE_IS_IMAGE_DOWNLOADED + "=? and "
                    + TableStructure.SERVER_IMAGE_SETP + "=?";
            selectionArgu = new String[] {
                    ServerDataMapping.S_DATATYPE_HTML,
                    String.valueOf(MySqlitDatabase.DOWNLOAD_TRUE), ServerImageDataModel.UN_READ
            };
        }
        SQLiteDatabase sqliteDatabase = mMySqlitDatabase.getReadableDatabase();
        int count = 0;
        Cursor cursor = sqliteDatabase.query(TableStructure.TABLE_NAME_SERVER_IMAGE, new String[] {
            TableStructure.SERVER_IMAGE_ID
        }, selection, selectionArgu, null, null, null, null);

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

    public synchronized boolean markRead(int id, boolean isRead) {
        SQLiteDatabase sqliteDatabase = mMySqlitDatabase.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TableStructure.SERVER_IMAGE_SETP, isRead ? READ : UN_READ);
        int result = sqliteDatabase.update(TableStructure.TABLE_NAME_SERVER_IMAGE, cv,
                TableStructure.SERVER_IMAGE_ID + "=?", new String[] {
                    String.valueOf(id)
                });
        return result != 0;
    }

    public List<ServerImageData> queryWithoutImg(int count) {
        SQLiteDatabase sqliteDatabase = mMySqlitDatabase.getReadableDatabase();
        List<ServerImageData> result = new ArrayList<ServerImageData>();

        Cursor cursor = sqliteDatabase.query(TableStructure.TABLE_NAME_SERVER_IMAGE, new String[] {
                TableStructure.SERVER_IMAGE_ID, TableStructure.SERVER_IMAGE_DESC,
                TableStructure.SERVER_IMAGE_TITLE, TableStructure.SERVER_IMAGE_URL,
                TableStructure.SERVER_COLLECT_WEBSITE

        }, TableStructure.SERVER_IMAGE_DATA_TYPE + "!=? and "
                + TableStructure.SERVER_IMAGE_IS_IMAGE_DOWNLOADED + "=?", new String[] {
                ServerDataMapping.S_DATATYPE_HTML, String.valueOf(MySqlitDatabase.DOWNLOAD_FALSE)
        }, null, null, null, String.valueOf(count));

        try {
            while (cursor.moveToNext()) {
                ServerImageData data = new ServerImageData();
                data.setId(cursor.getInt(0));
                data.setImageDesc(cursor.getString(1));
                data.setTitle(cursor.getString(2));
                data.setUrl(cursor.getString(3));
                data.setCollectWebsite(cursor.getString(4));
                result.add(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return result;
    }

    public void deleteAll() {
        SQLiteDatabase sqliteDatabase = mMySqlitDatabase.getWritableDatabase();
        int result = sqliteDatabase.delete(TableStructure.TABLE_NAME_SERVER_IMAGE, null, null);
        if (BuildConfig.DEBUG) {
            HDBLOG.logD("删除serverImageData表中的数据条数为：" + result);
        }
    }

    public ServerImageData queryOneByRandom(int count) {
        SQLiteDatabase sqliteDatabase = mMySqlitDatabase.getReadableDatabase();
        ServerImageData data = null;

        Cursor cursor = sqliteDatabase.query(TableStructure.TABLE_NAME_SERVER_IMAGE, new String[] {
                TableStructure.SERVER_IMAGE_ID, TableStructure.SERVER_IMAGE_URL,
                TableStructure.SERVER_IMAGE_DESC, TableStructure.SERVER_IMAGE_TITLE,
                TableStructure.SERVER_IMAGE_DATA_TYPE, TableStructure.SERVER_IMAGE_COLLECT_WEBSITE
        }, TableStructure.SERVER_IMAGE_IS_IMAGE_DOWNLOADED + "=? and "
                + TableStructure.SERVER_IMAGE_SETP + "=?", new String[] {
                String.valueOf(MySqlitDatabase.DOWNLOAD_TRUE), ServerImageDataModel.UN_READ
        }, null, null, "RANDOM()", String.valueOf(count));

        try {
            while (cursor.moveToNext()) {
                data = new ServerImageData();
                data.setId(cursor.getInt(0));
                data.setUrl(cursor.getString(1));
                data.setImageDesc(cursor.getString(2));
                data.setTitle(cursor.getString(3));
                data.setDataType(cursor.getString(4));
                data.setCollectWebsite(cursor.getString(5));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return data;
    }

    public ServerImageData queryOneWithImage() {
        SQLiteDatabase sqliteDatabase = mMySqlitDatabase.getReadableDatabase();
        ServerImageData data = null;

        Cursor cursor = sqliteDatabase.query(TableStructure.TABLE_NAME_SERVER_IMAGE, new String[] {
                TableStructure.SERVER_IMAGE_ID, TableStructure.SERVER_IMAGE_URL,
                TableStructure.SERVER_IMAGE_DESC, TableStructure.SERVER_IMAGE_TITLE,
                TableStructure.SERVER_IMAGE_DATA_TYPE, TableStructure.SERVER_COLLECT_WEBSITE
        }, TableStructure.SERVER_IMAGE_IS_IMAGE_DOWNLOADED + "=? and "
                + TableStructure.SERVER_IMAGE_DATA_TYPE + "!=? and "
                + TableStructure.SERVER_IMAGE_SETP + "=?", new String[] {
                String.valueOf(MySqlitDatabase.DOWNLOAD_TRUE), ServerDataMapping.S_DATATYPE_HTML,
                ServerImageDataModel.UN_READ
        }, null, null, "RANDOM()", "1");

        try {
            while (cursor.moveToNext()) {
                data = new ServerImageData();
                data.setId(cursor.getInt(0));
                data.setUrl(cursor.getString(1));
                data.setImageDesc(cursor.getString(2));
                data.setTitle(cursor.getString(3));
                data.setDataType(cursor.getString(4));
                data.setCollectWebsite(cursor.getString(5));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return data;
    }

    public long queryLastModifiedOfToday() {
        long lastTime = 0;
        SQLiteDatabase sqliteDatabase = mMySqlitDatabase.getReadableDatabase();
        Cursor cursor = sqliteDatabase.query(TableStructure.TABLE_NAME_SERVER_IMAGE, new String[] {
            "MAX(" + TableStructure.SERVER_IMAGE_COLLECT_TIME + ")"
        }, null, null, null, null, null);
        try {
            while (cursor.moveToNext()) {
                lastTime = cursor.getLong(0);
            }
        } catch (Exception e) {
        } finally {
            cursor.close();
        }
        return lastTime;
    }

    public List<ServerImageData> queryByRandom(int count, boolean containHtml) {
        SQLiteDatabase sqliteDatabase = mMySqlitDatabase.getReadableDatabase();
        List<ServerImageData> result = new ArrayList<ServerImageData>();

        String selection = null;
        String[] selectionArgus = null;
        if (containHtml) {
            selection = TableStructure.SERVER_IMAGE_IS_IMAGE_DOWNLOADED + "=? and "
                    + TableStructure.SERVER_IMAGE_SETP + "=?";
            selectionArgus = new String[] {
                    String.valueOf(MySqlitDatabase.DOWNLOAD_TRUE), ServerImageDataModel.UN_READ
            };
        } else {
            selection = TableStructure.SERVER_IMAGE_IS_IMAGE_DOWNLOADED + "=? and "
                    + TableStructure.SERVER_IMAGE_DATA_TYPE + "!=? and "
                    + TableStructure.SERVER_IMAGE_SETP + "=?";
            selectionArgus = new String[] {
                    String.valueOf(MySqlitDatabase.DOWNLOAD_TRUE),
                    ServerDataMapping.S_DATATYPE_HTML, ServerImageDataModel.UN_READ
            };
        }
        Cursor cursor = sqliteDatabase.query(TableStructure.TABLE_NAME_SERVER_IMAGE, new String[] {
                TableStructure.SERVER_IMAGE_ID, TableStructure.SERVER_IMAGE_URL,
                TableStructure.SERVER_IMAGE_DESC, TableStructure.SERVER_IMAGE_TITLE,
                TableStructure.SERVER_IMAGE_DATA_TYPE, TableStructure.SERVER_COLLECT_WEBSITE
        }, selection, selectionArgus, null, null, null, String.valueOf(count));

        try {
            while (cursor.moveToNext()) {
                ServerImageData data = new ServerImageData();
                data.setId(cursor.getInt(0));
                data.setUrl(cursor.getString(1));
                data.setImageDesc(cursor.getString(2));
                data.setTitle(cursor.getString(3));
                data.setDataType(cursor.getString(4));
                data.setCollectWebsite(cursor.getString(5));
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
