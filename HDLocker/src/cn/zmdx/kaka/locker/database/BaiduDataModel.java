
package cn.zmdx.kaka.locker.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.content.BaiduDataManager.BaiduData;
import cn.zmdx.kaka.locker.content.BaiduTagMapping;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;

/**
 * 操作数据库方法
 */
public class BaiduDataModel {

    private MySqlitDatabase mMySqlitDatabase;

    private static BaiduDataModel sBaiduDataModel = null;

    private BaiduDataModel() {
        mMySqlitDatabase = MySqlitDatabase.getInstance(HDApplication.getInstannce(),
                PandoraConfig.DATABASE_NAME, null);
    }

    public synchronized void close() {
        mMySqlitDatabase.close();
        mMySqlitDatabase = null;
        sBaiduDataModel = null;
    }

    public static synchronized BaiduDataModel getInstance() {
        if (sBaiduDataModel == null) {
            sBaiduDataModel = new BaiduDataModel();
        }
        return sBaiduDataModel;
    }

    public synchronized void saveBaiduData(List<BaiduData> list) {
        SQLiteDatabase mysql = mMySqlitDatabase.getWritableDatabase();
        try {
            mysql.beginTransaction();
            SQLiteStatement sqLiteStatement = mysql.compileStatement("replace INTO "
                    + TableStructure.TABLE_NAME_CONTENT + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
            for (BaiduData bd : list) {
                sqLiteStatement.clearBindings();
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_TWO, bd.getBaiduId());
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_THREE, bd.getDescribe());
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_FOUR, bd.getImageUrl());
                sqLiteStatement.bindLong(MySqlitDatabase.INDEX_FIVE, bd.getImageWidth());
                sqLiteStatement.bindLong(MySqlitDatabase.INDEX_SIX, bd.getImageHeight());
                sqLiteStatement.bindLong(MySqlitDatabase.INDEX_SEVEN, bd.isImageDownloaded());
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_EIGHT, bd.getTthumbLargeUrl());
                sqLiteStatement.bindLong(MySqlitDatabase.INDEX_NINE, bd.getThumbLargeWidth());
                sqLiteStatement.bindLong(MySqlitDatabase.INDEX_TEN, bd.getThumbLargeHeight());
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_ELEVEN, bd.getTag1());
                sqLiteStatement.bindString(MySqlitDatabase.INDEX_TWELVE, bd.getTag2());
                sqLiteStatement.executeInsert();
            }
            mysql.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mysql.endTransaction();
        }
    }

    /**
     * 从本地数据库查询一定数量的数据。这里查询的条件是，是否下载图片字段必须为0
     * 
     * @param tag1 大分类
     * @param count 取的数量
     * @return
     */
    public synchronized List<BaiduData> queryNonImageData(int tag1, int count) {
        SQLiteDatabase sqliteDatabase = mMySqlitDatabase.getReadableDatabase();
        List<BaiduData> list = new ArrayList<BaiduData>();
        Cursor cursor = sqliteDatabase.rawQuery("select * from "
                + TableStructure.TABLE_NAME_CONTENT + " where " + TableStructure.CONTENT_TAG1
                + " = '" + BaiduTagMapping.getStringTag1(tag1) + "' and "
                + TableStructure.CONTENT_IS_IMAGE_DOWNLOADED + " = "
                + MySqlitDatabase.DOWNLOAD_FALSE + " ORDER BY random() limit " + count, null);

        try {
            while (cursor.moveToNext()) {
                BaiduData bd = new BaiduData();
                int id = cursor.getInt(cursor.getColumnIndex(TableStructure.CONTENT_ID));
                String baiduId = cursor.getString(cursor
                        .getColumnIndex(TableStructure.CONTENT_BAIDU_ID));
                String describe = cursor.getString(cursor
                        .getColumnIndex(TableStructure.CONTENT_DESCRIBE));
                String imageurl = cursor.getString(cursor
                        .getColumnIndex(TableStructure.CONTENT_IMAGE_URL));
                int imagewidth = cursor.getInt(cursor
                        .getColumnIndex(TableStructure.CONTENT_IMAGE_WIDTH));
                int imageheight = cursor.getInt(cursor
                        .getColumnIndex(TableStructure.CONTENT_IMAGE_HEIGHT));
                int isimagedownloaded = cursor.getInt(cursor
                        .getColumnIndex(TableStructure.CONTENT_IS_IMAGE_DOWNLOADED));
                String thumblargeurl = cursor.getString(cursor
                        .getColumnIndex(TableStructure.CONTENT_THUMB_LARGE_URL));
                int thumblargewidth = cursor.getInt(cursor
                        .getColumnIndex(TableStructure.CONTENT_THUMB_LARGE_WIDTH));
                int thumblargeheight = cursor.getInt(cursor
                        .getColumnIndex(TableStructure.CONTENT_THUMB_LARGE_HEIGHT));

                bd.setId(id);
                bd.setBaiduId(baiduId);
                bd.setDescribe(describe);
                bd.setImageUrl(imageurl);
                bd.setImageWidth(imagewidth);
                bd.setImageHeight(imageheight);
                bd.setIsImageDownloaded(isimagedownloaded);
                bd.setTthumbLargeUrl(thumblargeurl);
                bd.setThumbLargeWidth(thumblargewidth);
                bd.setThumbLargeHeight(thumblargeheight);
                list.add(bd);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return list;
    }

    /**
     * 判断一条数据的图片是否已下载到本地磁盘
     * 
     * @param id
     * @return
     */
    public synchronized boolean isImageDownloaded(String id) {
        boolean isDownloaded = false;
        SQLiteDatabase sqliteDatabase = mMySqlitDatabase.getReadableDatabase();
        Cursor cursor = sqliteDatabase.rawQuery("select "
                + TableStructure.CONTENT_IS_IMAGE_DOWNLOADED + " from "
                + TableStructure.TABLE_NAME_CONTENT + " where " + TableStructure.CONTENT_ID + " = "
                + id, null);
        try {
            while (cursor.moveToNext()) {
                int isimagedownloaded = cursor.getInt(cursor
                        .getColumnIndex(TableStructure.CONTENT_IS_IMAGE_DOWNLOADED));
                isDownloaded = isimagedownloaded == MySqlitDatabase.DOWNLOAD_TRUE;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return isDownloaded;
    }

    /**
     * 将一条数据标记为图片已下载到本地
     * 
     * @param id
     */
    public synchronized boolean markAlreadyDownload(int id) {
        SQLiteDatabase sqliteDatabase = mMySqlitDatabase.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(TableStructure.CONTENT_IS_IMAGE_DOWNLOADED, MySqlitDatabase.DOWNLOAD_TRUE);
        int count = sqliteDatabase.update(TableStructure.TABLE_NAME_CONTENT, values,
                TableStructure.CONTENT_ID + "=?", new String[] {
                    String.valueOf(id)
                });
        return count != 0;
    }

    public synchronized boolean deleteById(int id) {
        SQLiteDatabase sqliteDatabase = mMySqlitDatabase.getWritableDatabase();
        int count = sqliteDatabase.delete(TableStructure.TABLE_NAME_CONTENT,
                TableStructure.CONTENT_ID + "=?", new String[] {
                    String.valueOf(id)
                });
        return count != 0;
    }

    /**
     * 查询数据总数
     * 
     * @return
     */
    public synchronized int queryTotalCount() {
        SQLiteDatabase sqliteDatabase = mMySqlitDatabase.getReadableDatabase();
        int count = 0;
        Cursor cursor = sqliteDatabase.rawQuery("select count(*) from "
                + TableStructure.TABLE_NAME_CONTENT, null);
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

    /**
     * 查询已下载图片的数据条数
     * 
     * @return
     */
    public synchronized int queryCountHasImage() {
        SQLiteDatabase sqliteDatabase = mMySqlitDatabase.getReadableDatabase();
        int count = 0;
        Cursor cursor = sqliteDatabase.rawQuery("select count(*) from "
                + TableStructure.TABLE_NAME_CONTENT + " where "
                + TableStructure.CONTENT_IS_IMAGE_DOWNLOADED + " = "
                + MySqlitDatabase.DOWNLOAD_TRUE, null);
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

    /**
     * 标记所有数据的是否下载字段为否
     */
    public synchronized void markAllNonDownload() {
        SQLiteDatabase sqliteDatabase = mMySqlitDatabase.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TableStructure.CONTENT_IS_IMAGE_DOWNLOADED, MySqlitDatabase.DOWNLOAD_FALSE);
        sqliteDatabase.update(TableStructure.TABLE_NAME_CONTENT, values, null, null);
    }

    /**
     * 根据tag1类型查询数据
     * 
     * @param tag1
     * @param count 需要的条数
     * @return
     */
    public synchronized List<BaiduData> queryWithImgByTag1(String tag1, int count) {
        List<BaiduData> list = new ArrayList<BaiduData>();
        SQLiteDatabase sqliteDatabase = mMySqlitDatabase.getReadableDatabase();
        Cursor cursor = sqliteDatabase.query(TableStructure.TABLE_NAME_CONTENT, new String[] {
                TableStructure.CONTENT_ID, TableStructure.CONTENT_IMAGE_URL,
                TableStructure.CONTENT_DESCRIBE
        }, TableStructure.CONTENT_TAG1 + "=? and " + TableStructure.CONTENT_IS_IMAGE_DOWNLOADED
                + "=?", new String[] {
                tag1, String.valueOf(MySqlitDatabase.DOWNLOAD_TRUE)
        }, null, null, null, String.valueOf(count));

        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String url = cursor.getString(1);
                BaiduData bd = new BaiduData();
                bd.setId(id);
                bd.setImageUrl(url);
                bd.setDescribe(cursor.getString(2));
                list.add(bd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }

        return list;
    }
}
