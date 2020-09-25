package com.jayshreegopalapps.imagetopdf;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class SplashFragment extends Fragment {


    public SplashFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new  Intent(getContext(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        }, 2000);
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

}
