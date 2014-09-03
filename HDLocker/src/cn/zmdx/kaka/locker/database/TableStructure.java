
package cn.zmdx.kaka.locker.database;

import android.database.sqlite.SQLiteDatabase;

public class TableStructure {
    public final static String TABLE_NAME_CONTENT = "content"; // content表

    // "tag1": "明星",
    // "tag2": "全部",
    // "baidu_id": "9425321612",
    // "describe": "3G刚开始用，4G还没碰过，尼玛5G来了，不带这么坑爹的啊，不知道格式怎么样的",
    // "image_url":
    // "http://e.hiphotos.baidu.com/image/pic/item/023b5bb5c9ea15ceba2d1f08b4003af33b87b2f4.jpg",
    // "image_width": 2132,
    // "image_height": 2845,
    // "thumb_large_url":
    // "http://imgt8.bdstatic.com/it/u=2,835387020&fm=19&gp=0.jpg",
    // "thumb_large_width": 310,
    // "thumb_large_height": 413,

    /* content表结构 */

    public final static String CONTENT_ID = "_id";

    public final static String CONTENT_BAIDU_ID = "baidu_id";

    public final static String CONTENT_DESCRIBE = "describe";

    public final static String CONTENT_IMAGE_URL = "image_url";

    public final static String CONTENT_IMAGE_WIDTH = "image_width";

    public final static String CONTENT_IMAGE_HEIGHT = "image_height";

    public final static String CONTENT_THUMB_LARGE_URL = "thumb_large_url";

    public final static String CONTENT_THUMB_LARGE_WIDTH = "thumb_large_width";

    public final static String CONTENT_THUMB_LARGE_HEIGHT = "thumb_large_height";

    public final static String CONTENT_TAG1 = "tag1";

    public final static String CONTENT_TAG2 = "tag2";

    public static synchronized void createContentTable(SQLiteDatabase db) {
        String str_sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_CONTENT + " (" + CONTENT_ID
                + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," + CONTENT_BAIDU_ID + " INTEGER,"
                + CONTENT_DESCRIBE + " TEXT," + CONTENT_IMAGE_URL + " TEXT," + CONTENT_IMAGE_WIDTH
                + " INTEGER," + CONTENT_IMAGE_HEIGHT + " INTEGER," + CONTENT_THUMB_LARGE_URL
                + " TEXT," + CONTENT_THUMB_LARGE_WIDTH + " INTEGER," + CONTENT_THUMB_LARGE_HEIGHT
                + " INTEGER," + CONTENT_TAG1 + " TEXT," + CONTENT_TAG2 + " TEXT)";
        db.execSQL(str_sql);
    }

}
