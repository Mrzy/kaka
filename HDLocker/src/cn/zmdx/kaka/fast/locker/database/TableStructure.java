
package cn.zmdx.kaka.fast.locker.database;

import android.database.sqlite.SQLiteDatabase;

public class TableStructure {
    public final static String TABLE_NAME_SERVER_IMAGE = "server_image"; // server_image表

    /* server_image表结构 */

    public final static String SERVER_IMAGE_ID = "_id";

    public final static String SERVER_IMAGE_CLOUD_ID = "cloud_id";

    public final static String SERVER_IMAGE_TITLE = "title";

    public final static String SERVER_IMAGE_URL = "url";

    public final static String SERVER_IMAGE_DESC = "image_url";

    public final static String SERVER_IMAGE_DATA_TYPE = "data_type";

    public final static String SERVER_IMAGE_TOP = "top";

    public final static String SERVER_IMAGE_READED = "setp";

    public final static String SERVER_IMAGE_COLLECT_TIME = "collect_time";

    public final static String SERVER_IMAGE_RELEASE_TIME = "release_time";

    public final static String SERVER_IMAGE_COLLECT_WEBSITE = "collect_website";

    public final static String SERVER_IMAGE_IS_IMAGE_DOWNLOADED = "is_image_downloaded";

    public final static String SERVER_IMAGE_IS_IMAGE_FAVORITED = "favorited";

    public static final String TABLE_NAME_NOTIFICATION = "cusnoti";

    public static final String CUSNOTI_ID = "_id";

    public static final String CUSNOTI_CLOUDID = "cloudId";

    public static final String CUSNOTI_TITLE = "title";

    public static final String CUSNOTI_CONTENT = "content";

    public static final String CUSNOTI_STARTTIME = "startTime";

    public static final String CUSNOTI_ENDTIME = "endTime";

    public static final String CUSNOTI_ICON = "icon";

    public static final String CUSNOTI_TIMES = "times";

    public static final String CUSNOTI_TYPE = "type";

    public static final String CUSNOTI_TARGETURL = "targetUrl";

    public static final String CUSNOTI_TARGETAPP = "targetApp";

    public static final String CUSNOTI_LEVEL = "level";

    public static final String CUSNOTI_EXTRA = "extra";

    public static synchronized void createServerImageTable(SQLiteDatabase db) {
        String str_sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_SERVER_IMAGE + " ("
                + SERVER_IMAGE_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                + SERVER_IMAGE_CLOUD_ID + " TEXT," + SERVER_IMAGE_TITLE + " TEXT,"
                + SERVER_IMAGE_URL + " TEXT," + SERVER_IMAGE_DESC + " TEXT,"
                + SERVER_IMAGE_DATA_TYPE + " TEXT," + SERVER_IMAGE_TOP + " TEXT,"
                + SERVER_IMAGE_READED + " TEXT," + SERVER_IMAGE_COLLECT_TIME + " TEXT,"
                + SERVER_IMAGE_RELEASE_TIME + " TEXT," + SERVER_IMAGE_COLLECT_WEBSITE + " TEXT,"
                + SERVER_IMAGE_IS_IMAGE_FAVORITED + " INTEGER," + SERVER_IMAGE_IS_IMAGE_DOWNLOADED
                + " INTEGER)";
        db.execSQL(str_sql);
    }

    public static synchronized void createCustomNotificationTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_NOTIFICATION + "(" + CUSNOTI_ID
                + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," + CUSNOTI_CLOUDID + " INTEGER,"
                + CUSNOTI_TITLE + " TEXT," + CUSNOTI_CONTENT + " TEXT," + CUSNOTI_STARTTIME
                + " INTEGER," + CUSNOTI_ENDTIME + " INTEGER," + CUSNOTI_ICON + " TEXT,"
                + CUSNOTI_TIMES + " INTEGER," + CUSNOTI_TYPE + " TEXT," + CUSNOTI_TARGETURL
                + " TEXT," + CUSNOTI_TARGETAPP + " TEXT," + CUSNOTI_LEVEL + " INTEGER,"
                + CUSNOTI_EXTRA + " TEXT)";
        db.execSQL(sql);
    }
}
