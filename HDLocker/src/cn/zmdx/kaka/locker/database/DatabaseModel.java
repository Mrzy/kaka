
package cn.zmdx.kaka.locker.database;

import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;

/**
 * 操作数据库方法
 */
public class DatabaseModel {
    private final static int DATABASE_VERSION = 1;

    MySqlitDatabase mMySqlitDatabase;

    private static DatabaseModel databaseModel = null;

    private DatabaseModel() {
        mMySqlitDatabase = new MySqlitDatabase(HDApplication.getInstannce(),
                PandoraConfig.DATABASE_NAME, null, DATABASE_VERSION);
    }

    public synchronized void close() {
        mMySqlitDatabase.close();
        mMySqlitDatabase = null;
        databaseModel = null;
    }

    public static DatabaseModel getInstance() {
        if (databaseModel == null) {
            databaseModel = new DatabaseModel();
        }
        return databaseModel;
    }

    public void createTable() {
        mMySqlitDatabase.getWritableDatabase();
    }
}
