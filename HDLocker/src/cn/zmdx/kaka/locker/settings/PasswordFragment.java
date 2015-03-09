
package cn.zmdx.kaka.locker.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PasswordFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText("密码");
        return textView;
    }
}
