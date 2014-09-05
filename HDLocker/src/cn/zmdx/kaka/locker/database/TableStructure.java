
package cn.zmdx.kaka.locker.database;

import android.database.sqlite.SQLiteDatabase;

public class TableStructure {
    public final static String TABLE_NAME_CONTENT = "content"; // content表

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

}
