
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (PandoraConfig.IS_DEBUG) {
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
