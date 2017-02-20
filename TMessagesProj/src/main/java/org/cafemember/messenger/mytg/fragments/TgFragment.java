package org.cafemember.messenger.mytg.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by Masoud on 6/2/2016.
 */
@SuppressLint("ValidFragment")
public class TgFragment extends Fragment {
    View frameLayout;

    @SuppressLint("ValidFragment")
    public TgFragment(View frameLayout){
        this.frameLayout = frameLayout;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return frameLayout;

    }
}
