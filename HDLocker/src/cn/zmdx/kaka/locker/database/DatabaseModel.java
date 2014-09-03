
package cn.zmdx.kaka.locker.database;

import cn.zmdx.kaka.locker.HDApplication;
import cn.zmdx.kaka.locker.settings.config.PandoraConfig;

/**
 * 操作数据库方法
 */
public class DatabaseModel {
    private final static int DATABASE_VERSION = 1;

    private MySqlitDatabase mMySqlitDatabase;

    private static DatabaseModel sDatabaseModel = null;

    private DatabaseModel() {
        mMySqlitDatabase = new MySqlitDatabase(HDApplication.getInstannce(),
                PandoraConfig.DATABASE_NAME, null, DATABASE_VERSION);
    }

    public synchronized void close() {
        mMySqlitDatabase.close();
        mMySqlitDatabase = null;
        sDatabaseModel = null;
    }

    public static DatabaseModel getInstance() {
        if (sDatabaseModel == null) {
            sDatabaseModel = new DatabaseModel();
        }
        return sDatabaseModel;
    }

}
