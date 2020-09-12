package com.jayshreegopalapps.imagetopdf;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
public class BottomModalQr extends BottomSheetDialogFragment {
    String text;
    Context context;
    BottomModalQrListener listener;
    public interface BottomModalQrListener{
        void close();
    }
    public BottomModalQr(String text,Context context, BottomModalQrListener listener) {
        this.text=text;
        this.context=context;
        this.listener= listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.qr_bottom_modal,null);
        final TextView text=v.findViewById(R.id.qrtxtcopy);
        ImageView img=v.findViewById(R.id.qrbtncopy);
        text.setText(this.text  );
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("copied qr code", text.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "copied", Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        listener.close();
    }
}
