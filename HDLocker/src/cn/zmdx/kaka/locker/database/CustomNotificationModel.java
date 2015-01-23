
package cn.zmdx.kaka.locker.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.notification.NotificationEntity;
import cn.zmdx.kaka.locker.notification.NotificationInfo;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;

public class CustomNotificationModel {
    private MySqlitDatabase mMySqlitDatabase;

    private static CustomNotificationModel sCusNotiModel = null;

    private CustomNotificationModel() {
        mMySqlitDatabase = MySqlitDatabase.getInstance(HDApplication.getContext(),
                PandoraConfig.DATABASE_NAME, null);
    }

    public static synchronized CustomNotificationModel getInstance() {
        if (sCusNotiModel == null) {
            sCusNotiModel = new CustomNotificationModel();
        }
        return sCusNotiModel;
    }

    /**
     * 若成功插入，则返回1，否则为0
     * 
     * @param ni
     * @return
     */
    public synchronized int insert(NotificationEntity ne) {
        SQLiteDatabase db = mMySqlitDatabase.getWritableDatabase();
        int result = 0;
        NotificationEntity entity = queryByCloudId(ne.getCloudId());
        if (entity == null) {
            ContentValues cv = new ContentValues();
            cv.put(TableStructure.CUSNOTI_CLOUDID, ne.getCloudId());
            cv.put(TableStructure.CUSNOTI_CONTENT, ne.getContent());
            cv.put(TableStructure.CUSNOTI_ENDTIME, ne.getEndTime());
            cv.put(TableStructure.CUSNOTI_EXTRA, ne.getExtra());
            cv.put(TableStructure.CUSNOTI_ICON, ne.getIcon());
            cv.put(TableStructure.CUSNOTI_LEVEL, ne.getLevel());
            cv.put(TableStructure.CUSNOTI_STARTTIME, ne.getStartTime());
            cv.put(TableStructure.CUSNOTI_TARGETAPP, ne.getTargetApp());
            cv.put(TableStructure.CUSNOTI_TARGETURL, ne.getTargetUrl());
            cv.put(TableStructure.CUSNOTI_TIMES, ne.getTimes());
            cv.put(TableStructure.CUSNOTI_TITLE, ne.getTitle());
            cv.put(TableStructure.CUSNOTI_TYPE, ne.getType());
            long rowId = db.insert(TableStructure.TABLE_NAME_NOTIFICATION, null, cv);
            if (rowId != -1) {
                result = 1;
            }
        } else {
            ContentValues cv = new ContentValues();
            cv.put(TableStructure.CUSNOTI_CLOUDID, ne.getCloudId());
            cv.put(TableStructure.CUSNOTI_CONTENT, ne.getContent());
            cv.put(TableStructure.CUSNOTI_ENDTIME, ne.getEndTime());
            cv.put(TableStructure.CUSNOTI_EXTRA, ne.getExtra());
            cv.put(TableStructure.CUSNOTI_ICON, ne.getIcon());
            cv.put(TableStructure.CUSNOTI_LEVEL, ne.getLevel());
            cv.put(TableStructure.CUSNOTI_STARTTIME, ne.getStartTime());
            cv.put(TableStructure.CUSNOTI_TARGETAPP, ne.getTargetApp());
            cv.put(TableStructure.CUSNOTI_TARGETURL, ne.getTargetUrl());
            cv.put(TableStructure.CUSNOTI_TIMES, ne.getTimes());
            cv.put(TableStructure.CUSNOTI_TITLE, ne.getTitle());
            cv.put(TableStructure.CUSNOTI_TYPE, ne.getType());
            result = db.update(TableStructure.TABLE_NAME_NOTIFICATION, cv,
                    TableStructure.CUSNOTI_CLOUDID + "=?", new String[] {
                        String.valueOf(ne.getCloudId())
                    });
        }

        return result;
    }

    public synchronized int batchInsert(List<NotificationEntity> nis) {
        int result = 0;
        for (NotificationEntity entity : nis) {
            result += insert(entity);
        }
        return result;
    }

    public synchronized int deleteByCloudId(int cloudId) {
        SQLiteDatabase db = mMySqlitDatabase.getWritableDatabase();
        return db.delete(TableStructure.TABLE_NAME_NOTIFICATION, TableStructure.CUSNOTI_CLOUDID
                + "=?", new String[] {
            String.valueOf(cloudId)
        });
    }

    public synchronized int deleteById(int id) {
        SQLiteDatabase db = mMySqlitDatabase.getWritableDatabase();
        return db.delete(TableStructure.TABLE_NAME_NOTIFICATION, TableStructure.CUSNOTI_ID + "=?",
                new String[] {
                    String.valueOf(id)
                });
    }

    public synchronized int update(NotificationInfo ni) {
        return 0;
    }

    /**
     * 返回当前有效的所有通知（当前时间在开始时间和结束时间之内）
     * 
     * @return
     */
    public synchronized List<NotificationEntity> queryValidNotification() {
        List<NotificationEntity> list = new ArrayList<NotificationEntity>();
        SQLiteDatabase db = mMySqlitDatabase.getReadableDatabase();
        String[] selection = new String[] {
                TableStructure.CUSNOTI_ID, TableStructure.CUSNOTI_CONTENT,
                TableStructure.CUSNOTI_ENDTIME, TableStructure.CUSNOTI_EXTRA,
                TableStructure.CUSNOTI_ICON, TableStructure.CUSNOTI_CLOUDID,
                TableStructure.CUSNOTI_LEVEL, TableStructure.CUSNOTI_STARTTIME,
                TableStructure.CUSNOTI_TARGETAPP, TableStructure.CUSNOTI_TARGETURL,
                TableStructure.CUSNOTI_TIMES, TableStructure.CUSNOTI_TITLE,
                TableStructure.CUSNOTI_TYPE

        };
        Cursor cursor = null;
        try {
            long current = System.currentTimeMillis();
            cursor = db.query(TableStructure.TABLE_NAME_NOTIFICATION, selection, current + ">"
                    + TableStructure.CUSNOTI_STARTTIME + " and " + current + "<"
                    + TableStructure.CUSNOTI_ENDTIME, new String[] {}, null, null, null);
            while (cursor.moveToNext()) {
                NotificationEntity entity = new NotificationEntity();
                entity.setId(cursor.getInt(0));
                entity.setContent(cursor.getString(1));
                entity.setEndTime(cursor.getLong(2));
                entity.setExtra(cursor.getString(3));
                entity.setIcon(cursor.getString(4));
                entity.setCloudId(cursor.getInt(5));
                entity.setLevel(cursor.getInt(6));
                entity.setStartTime(cursor.getLong(7));
                entity.setTargetApp(cursor.getString(8));
                entity.setTargetUrl(cursor.getString(9));
                entity.setTimes(cursor.getInt(10));
                entity.setTitle(cursor.getString(11));
                entity.setType(cursor.getInt(12));
                list.add(entity);
            }
        } catch (Exception e) {
            list = null;
        } finally {
            cursor.close();
        }
        return list;
    }

    public synchronized NotificationEntity queryByCloudId(int cloudId) {
        SQLiteDatabase db = mMySqlitDatabase.getReadableDatabase();
        NotificationEntity entity = null;
        String[] selection = new String[] {
                TableStructure.CUSNOTI_ID, TableStructure.CUSNOTI_CONTENT,
                TableStructure.CUSNOTI_ENDTIME, TableStructure.CUSNOTI_EXTRA,
                TableStructure.CUSNOTI_ICON, TableStructure.CUSNOTI_CLOUDID,
                TableStructure.CUSNOTI_LEVEL, TableStructure.CUSNOTI_STARTTIME,
                TableStructure.CUSNOTI_TARGETAPP, TableStructure.CUSNOTI_TARGETURL,
                TableStructure.CUSNOTI_TIMES, TableStructure.CUSNOTI_TITLE,
                TableStructure.CUSNOTI_TYPE

        };
        Cursor cursor = null;
        try {
            cursor = db.query(TableStructure.TABLE_NAME_NOTIFICATION, selection,
                    TableStructure.CUSNOTI_CLOUDID + "=?", new String[] {
                        String.valueOf(cloudId)
                    }, null, null, null);
            while (cursor.moveToNext()) {
                entity = new NotificationEntity();
                entity.setId(cursor.getInt(0));
                entity.setContent(cursor.getString(1));
                entity.setEndTime(cursor.getLong(2));
                entity.setExtra(cursor.getString(3));
                entity.setIcon(cursor.getString(4));
                entity.setCloudId(cursor.getInt(5));
                entity.setLevel(cursor.getInt(6));
                entity.setStartTime(cursor.getLong(7));
                entity.setTargetApp(cursor.getString(8));
                entity.setTargetUrl(cursor.getString(9));
                entity.setTimes(cursor.getInt(10));
                entity.setTitle(cursor.getString(11));
                entity.setType(cursor.getInt(12));
            }
        } catch (Exception e) {
            entity = null;
        } finally {
            cursor.close();
        }
        return entity;
    }

    public int deleteExpiredData() {
        SQLiteDatabase db = mMySqlitDatabase.getWritableDatabase();
        long current = System.currentTimeMillis();
        return db.delete(TableStructure.TABLE_NAME_NOTIFICATION, current + ">?", new String[]{TableStructure.CUSNOTI_ENDTIME});
    }
}
