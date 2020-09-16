package com.shortcontent.imagetopdf;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomModalEncrypt extends BottomSheetDialogFragment {
    Context context;
    BottomModalEncryptListener listener;
    public interface BottomModalEncryptListener{
        void onSave(String s);
        void close();
    }
    public BottomModalEncrypt(Context context, BottomModalEncryptListener listener) {
        this.context=context;
        this.listener= listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.ask_password_view,null);
        final EditText text = v.findViewById(R.id.ask_password_edit);
        ImageView save, cancel;
        save = v.findViewById(R.id.ask_password_save);
        cancel = v.findViewById(R.id.ask_password_cacel);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!text.getText().toString().equals("")) {
                    BottomModalEncrypt.this.listener.onSave(text.getText().toString());
                    dismiss();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return v;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        listener.close();
    }
}
