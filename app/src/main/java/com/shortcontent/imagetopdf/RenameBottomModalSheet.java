package com.shortcontent.imagetopdf;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

public class RenameBottomModalSheet extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;
    String name;
    Button save, cancel;
    TextInputEditText editText;
    int type;

    public RenameBottomModalSheet(String name) {
        this.name = name;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.rename_bottom_modal, container, false);
        editText = v.findViewById(R.id.rename_file_rename_bottom_modal);
        save = v.findViewById(R.id.save_rename_bottom_modal);
        cancel = v.findViewById(R.id.cancel_rename_bottom_modal);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.save(name, editText.getText().toString());
                dismiss();
            }
        });
        return v;
    }

    public interface BottomSheetListener {
        void save(String name, String newName);
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
