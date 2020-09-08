package com.jayshreegopalapps.imagetopdf;

import android.content.Context;
import android.content.Intent;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class EsignatureBottomModal extends BottomSheetDialogFragment {
    Context context;
    EsignatureListener mListener;
    public interface EsignatureListener{
        String save(String path);
        void cancel();
    }

    public EsignatureBottomModal(Context context, EsignatureListener listener) {
        this.context = context;
        this.mListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.esignature_bottom_modal, container, false);

        final SignaturePad signaturePad;
        final Button saveButton, clearButton;
        signaturePad = (SignaturePad)v.findViewById(R.id.signaturePad);
        saveButton = (Button)v.findViewById(R.id.saveButton);
        clearButton = (Button)v.findViewById(R.id.clearButton);
        signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }


            @Override
            public void onSigned() {
                saveButton.setEnabled(true);
                clearButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                saveButton.setEnabled(false);
                clearButton.setEnabled(false);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //write code for saving the signature here
                Bitmap b = signaturePad.getTransparentSignatureBitmap(true);
                try {
                    String p = Constants.ESIGNATURE_PATH + System.currentTimeMillis() + ".png";
                    b.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(p));
                    mListener.save(p);
                    dismiss();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Toast.makeText(context, "Signature Saved", Toast.LENGTH_SHORT).show();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signaturePad.clear();
            }
        });
        return v;
    }
}
