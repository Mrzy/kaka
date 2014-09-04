
package cn.zmdx.kaka.locker.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MAboutFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView asd=new TextView(getActivity());
        asd.setText("asdasd");
        return asd;
    }

    public interface onAboutCallBack {
        void gotoAbout();
    }
    
}
