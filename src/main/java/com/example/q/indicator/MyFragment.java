package com.example.q.indicator;

import android.os.Bundle;
import android.sax.RootElement;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by YQ on 2016/10/12.
 */

public class MyFragment extends Fragment {
    private String title;
    private static final String BUNDLE_TITLE = "title";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            title = bundle.getString(BUNDLE_TITLE);
        }
        TextView textView=new TextView(getActivity());
        textView.setText(title);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    public static MyFragment newInstance(String title) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_TITLE, title);
        MyFragment myFragment = new MyFragment();
        myFragment.setArguments(bundle);
        return myFragment;
    }
}
