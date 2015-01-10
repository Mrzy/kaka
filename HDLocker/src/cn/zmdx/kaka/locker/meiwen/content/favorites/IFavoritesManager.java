
package cn.zmdx.kaka.locker.meiwen.content.favorites;

import android.database.Cursor;

public interface IFavoritesManager {

    public Cursor getFavoritesInfo();

    public boolean addFavorite(String infoId);

    public boolean removeFavorite(String infoId);
    
    public boolean isFavorited(String infoId);
}
