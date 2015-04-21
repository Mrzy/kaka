
package cn.zmdx.kaka.locker.layout;

import android.view.View;

public interface LayoutGenerator {
    public int getLayoutId();
    public View createView();
}
