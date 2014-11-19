
package cn.zmdx.kaka.locker.content.box;

import java.util.List;

import android.view.View;
import cn.zmdx.kaka.locker.content.ServerImageDataManager.ServerImageData;

public interface IFoldableBox {

    public int getCategory();

    public List<ServerImageData> getData();

    public View getRenderedView();

    public void setAdapter(FoldableBoxAdapter adapter);

    public void onFinish();

}
