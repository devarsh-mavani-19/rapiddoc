package com.shortcontent.imagetopdf;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddWatermarkTask extends AsyncTask<String, Void, String> {
    AlertDialog dialog;
    AppCompatActivity context;
    AddWatermarkTask(AppCompatActivity context) {
        this.context = context;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new AlertDialog.Builder(context).setCancelable(false).setView(R.layout.layout_loading_dialog).create();
        dialog.show();
    }

    @Override
    protected String doInBackground(String... strings) {
        return PdfConverterBox.addWatermark(context, Uri.parse(strings[1]), strings[0], strings[2], strings[3], strings[4], strings[5]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        String text = s == null ? "Failed To Add Watermark" : ("PDF Saved at " + s);
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
        dialog.dismiss();
        if(s!=null) {
            Intent i = new Intent(context, OpenPDFActivity.class);
            i.setDataAndType(Uri.parse("file:///" + s), "application/pdf");
            context.startActivity(i);
        }
    }
}
