package com.jayshreegopalapps.imagetopdf;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

public class LoadingInBackground extends AsyncTask<Uri,Uri, Uri> {
    private Context context;
    AlertDialog dialog;
    MainActivity m;
    public LoadingInBackground(Context context, MainActivity m) {
        this.context = context;
        this.m = m;
    }

    @Override
    protected void onPreExecute() {
        //show Progress Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.activity_main, null);
        builder.setView(R.layout.layout_loading_dialog);
        dialog = builder.create();
        dialog.show();
    }

    @Override
    protected Uri doInBackground(Uri[] S) {
        PdfConverter.pdf2imageFromUri(context,S[0]);
        return null;
    }

    @Override
    protected void onPostExecute(Uri o) {
        dialog.dismiss();
        m.resetLayoutToDefault();
    }
}
