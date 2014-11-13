
package cn.zmdx.kaka.locker.content.box;

import it.gmariotti.cardslib.library.internal.CardArrayAdapter;

import java.util.List;

import android.view.View;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;

public interface IFoldableBox {

    public int getCategory();

    public List<ServerImageData> getData();

    public View getRenderedView();

    public void setAdapter(CardArrayAdapter adapter);

    public void onFinish();

}
