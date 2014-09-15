
package cn.zmdx.kaka.locker.database;

import android.database.sqlite.SQLiteDatabase;

public class TableStructure {
    public final static String TABLE_NAME_CONTENT = "content"; // content表

    public final static String TABLE_NAME_JOKE = "joke"; // joke表

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

    /* joke表结构 */

    public final static String JOKE_ID = "_id";

    public final static String JOKE_SERVER_ID = "server_id";

    public final static String JOKE_CONTENT = "joke_content";

    public final static String JOKE_TOP = "joke_top";

    public final static String JOKE_SETP = "joke_setp";

    public final static String JOKE_COLLECT_DATE = "joke_collect_date";

    public final static String JOKE_RELEASE_DATE = "joke_release_date";

    public final static String JOKE_COLLECT_WEBSITE = "joke_collect_website";

    public final static String JOKE_TITLE = "joke_title";

    public static synchronized void createJokeTable(SQLiteDatabase db) {
        String str_sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_JOKE + " (" + JOKE_ID
                + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," + JOKE_SERVER_ID + " TEXT,"
                + JOKE_CONTENT + " TEXT," + JOKE_TOP + " TEXT," + JOKE_SETP + " TEXT,"
                + JOKE_COLLECT_DATE + " TEXT," + JOKE_RELEASE_DATE + " TEXT,"
                + JOKE_COLLECT_WEBSITE + " TEXT," + JOKE_TITLE + " TEXT)";
        db.execSQL(str_sql);
    }
}
