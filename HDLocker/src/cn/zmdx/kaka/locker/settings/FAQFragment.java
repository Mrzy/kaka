
package cn.zmdx.kaka.locker.settings;

import cn.zmdx.kaka.locker.R;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FAQFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setBackgroundColor(getResources().getColor(R.color.white));
        textView.setText("常见问题");
        return textView;
    }

}
