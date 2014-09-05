
package cn.zmdx.kaka.locker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;
import cn.zmdx.kaka.locker.utils.HDBLOG;

public class MySqlitDatabase extends SQLiteOpenHelper {

    public MySqlitDatabase(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        TableStructure.createContentTable(db);
        db.execSQL("create unique index if not exists baiduId_index on "
                + TableStructure.TABLE_NAME_CONTENT + "(" + TableStructure.CONTENT_BAIDU_ID + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (PandoraConfig.sDebug) {
            HDBLOG.logD("oldVersion : " + oldVersion + "newVersion : " + newVersion);
        }
        switch (oldVersion) {
            case 1:

                break;

            default:
                break;
        }
    }

}
