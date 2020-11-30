package com.jayshreegopalapps.imagetopdf;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class PasswordFragment extends Fragment {


    public PasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_password, container, false);
        final TextInputEditText password= v.findViewById(R.id.password_splash);
        Button next = v.findViewById(R.id.button_password_next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password.getText().toString().equals("")) {
                    password.setError(getString(R.string.please_enter_password));
                } else {
                    SharedPreferences preferences = getContext().getSharedPreferences("com.jayshreegopalapps.ImageToPdf", Context.MODE_PRIVATE);
                    String mPassword=  preferences.getString("password", "");
                    if(mPassword.equals("")) {
                        FragmentManager manager = getActivity().getSupportFragmentManager();
                        manager.beginTransaction().replace(R.id.splash_fragment, new SplashFragment(), "splash_screen").commit();

                    }
                    else {
                        if(mPassword.equals(password.getText().toString())) {
                            FragmentManager manager = getActivity().getSupportFragmentManager();
                            manager.beginTransaction().replace(R.id.splash_fragment, new SplashFragment(), "splash_screen").commit();
                        }
                        else {
                            password.setError(getString(R.string.invalid_password));
                        }
                    }
                }
            }
        });

        return v;
    }

}
