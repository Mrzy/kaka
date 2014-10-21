
package cn.zmdx.kaka.locker.database;

import android.database.sqlite.SQLiteDatabase;

public class TableStructure {
    public final static String TABLE_NAME_CONTENT = "content"; // content表

    public final static String TABLE_NAME_SERVER = "server"; // server表

    public final static String TABLE_NAME_SERVER_IMAGE = "server_image"; // server_image表

    /* content表结构 */

    public final static String CONTENT_ID = "_id";

    public final static String CONTENT_BAIDU_ID = "baidu_id";

    public final static String CONTENT_DESCRIBE = "describe";

    public final static String CONTENT_IMAGE_URL = "image_url";

    public final static String CONTENT_IMAGE_WIDTH = "image_width";

    public final static String CONTENT_IMAGE_HEIGHT = "image_height";

    public final static String CONTENT_IS_IMAGE_DOWNLOADED = "is_image_downloaded";

    public final static String CONTENT_THUMB_LARGE_URL = "thumb_large_url";

    public final static String CONTENT_THUMB_LARGE_WIDTH = "thumb_large_width";

    public final static String CONTENT_THUMB_LARGE_HEIGHT = "thumb_large_height";

    public final static String CONTENT_TAG1 = "tag1";

    public final static String CONTENT_TAG2 = "tag2";

    public static synchronized void createContentTable(SQLiteDatabase db) {
        String str_sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_CONTENT + " (" + CONTENT_ID
                + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," + CONTENT_BAIDU_ID + " TEXT,"
                + CONTENT_DESCRIBE + " TEXT," + CONTENT_IMAGE_URL + " TEXT," + CONTENT_IMAGE_WIDTH
                + " INTEGER," + CONTENT_IMAGE_HEIGHT + " INTEGER," + CONTENT_IS_IMAGE_DOWNLOADED
                + " INTEGER," + CONTENT_THUMB_LARGE_URL + " TEXT," + CONTENT_THUMB_LARGE_WIDTH
                + " INTEGER," + CONTENT_THUMB_LARGE_HEIGHT + " INTEGER," + CONTENT_TAG1 + " TEXT,"
                + CONTENT_TAG2 + " TEXT)";
        db.execSQL(str_sql);
    }

    /* server表结构 */

    public final static String SERVER_ID = "_id";

    public final static String SERVER_CLOUD_ID = "cloud_id";

    public final static String SERVER_TITLE = "title";

    public final static String SERVER_CONTENT = "content";

    public final static String SERVER_DATA_TYPE = "data_type";

    public final static String SERVER_COLLECT_WEBSITE = "collect_website";

    public final static String SERVER_TOP = "top";

    public final static String SERVER_SETP = "setp";

    public final static String SERVER_COLLECT_TIME = "collect_time";

    public final static String SERVER_RELEASE_TIME = "release_time";

    public static synchronized void createServerTable(SQLiteDatabase db) {
        String str_sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_SERVER + " (" + SERVER_ID
                + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," + SERVER_CLOUD_ID + " TEXT,"
                + SERVER_TITLE + " TEXT," + SERVER_CONTENT + " TEXT," + SERVER_DATA_TYPE + " TEXT,"
                + SERVER_COLLECT_WEBSITE + " TEXT," + SERVER_TOP + " TEXT," + SERVER_SETP
                + " TEXT," + SERVER_COLLECT_TIME + " TEXT," + SERVER_RELEASE_TIME + " TEXT)";
        db.execSQL(str_sql);
    }

    /* server_image表结构 */

    public final static String SERVER_IMAGE_ID = "_id";

    public final static String SERVER_IMAGE_CLOUD_ID = "cloud_id";

    public final static String SERVER_IMAGE_TITLE = "title";

    public final static String SERVER_IMAGE_URL = "url";

    public final static String SERVER_IMAGE_DESC = "image_url";

    public final static String SERVER_IMAGE_DATA_TYPE = "data_type";

    public final static String SERVER_IMAGE_TOP = "top";

    public final static String SERVER_IMAGE_SETP = "setp";

    public final static String SERVER_IMAGE_COLLECT_TIME = "collect_time";

    public final static String SERVER_IMAGE_RELEASE_TIME = "release_time";

    public final static String SERVER_IMAGE_COLLECT_WEBSITE = "collect_website";

    public final static String SERVER_IMAGE_IS_IMAGE_DOWNLOADED = "is_image_downloaded";

    public static synchronized void createServerImageTable(SQLiteDatabase db) {
        String str_sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_SERVER_IMAGE + " ("
                + SERVER_IMAGE_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                + SERVER_IMAGE_CLOUD_ID + " TEXT," + SERVER_IMAGE_TITLE + " TEXT,"
                + SERVER_IMAGE_URL + " TEXT," + SERVER_IMAGE_DESC + " TEXT,"
                + SERVER_IMAGE_DATA_TYPE + " TEXT," + SERVER_IMAGE_TOP + " TEXT,"
                + SERVER_IMAGE_SETP + " TEXT," + SERVER_IMAGE_COLLECT_TIME + " TEXT,"
                + SERVER_IMAGE_RELEASE_TIME + " TEXT," + SERVER_IMAGE_COLLECT_WEBSITE + " TEXT,"
                + SERVER_IMAGE_IS_IMAGE_DOWNLOADED + " INTEGER)";
        db.execSQL(str_sql);
    }
}
