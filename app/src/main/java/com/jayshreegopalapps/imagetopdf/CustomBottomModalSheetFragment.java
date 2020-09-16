package com.jayshreegopalapps.imagetopdf;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CustomBottomModalSheetFragment extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;
    String name;
    Button yes, no;
    TextView textView;
    int type;

    public CustomBottomModalSheetFragment(String name) {
        this.name = name;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_modal, container, false);
        textView = v.findViewById(R.id.confirm_save_to_dialog);
        yes = v.findViewById(R.id.yes_bottom_modal);
        no = v.findViewById(R.id.no_bottom_modal);
        textView.setText((textView.getText() + name));
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.yes(name);
            }
        });
        return v;
    }

    public interface BottomSheetListener {
        void yes(String name);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (BottomSheetListener) getContext();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
}
