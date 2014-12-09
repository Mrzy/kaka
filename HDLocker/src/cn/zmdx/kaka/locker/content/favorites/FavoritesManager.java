
package cn.zmdx.kaka.locker.content.favorites;

import android.content.Context;
import android.database.Cursor;

public class FavoritesManager implements IFavoritesManager {

    private Context mContext;

    public FavoritesManager(Context context) {
        mContext = context;
    }

    @Override
    public Cursor getFavoritesInfo() {
        return null;
    }

    @Override
    public boolean addFavorite(String infoId) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean removeFavorite(String infoId) {
        // TODO Auto-generated method stub
        return false;
    }

}
