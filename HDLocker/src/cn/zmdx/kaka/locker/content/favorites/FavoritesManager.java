
package cn.zmdx.kaka.locker.content.favorites;

import cn.zmdx.kaka.locker.database.ServerImageDataModel;
import android.content.Context;
import android.database.Cursor;

public class FavoritesManager implements IFavoritesManager {

    private Context mContext;

    public FavoritesManager(Context context) {
        mContext = context;
    }

    @Override
    public Cursor getFavoritesInfo() {
        Cursor queryAllFavoritedCards = ServerImageDataModel.getInstance().queryAllFavoritedCards();
        return queryAllFavoritedCards;
    }

    @Override
    public boolean addFavorite(String infoId) {
        boolean markIsFavorited = ServerImageDataModel.getInstance().markIsFavorited(
                Integer.parseInt(infoId), true);
        return markIsFavorited;
    }

    @Override
    public boolean removeFavorite(String infoId) {
        boolean markIsFavorited = ServerImageDataModel.getInstance().markIsFavorited(
                Integer.parseInt(infoId), false);
        return markIsFavorited;
    }

}
