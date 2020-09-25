package com.jayshreegopalapps.imagetopdf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.tom_roush.pdfbox.multipdf.Splitter;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
//import com.itextpdf.text.Document;
//import com.itextpdf.text.pdf.PdfCopy;
//import com.itextpdf.text.pdf.PdfImportedPage;
//import com.itextpdf.text.pdf.PdfReader;

//import org.apache.pdfbox.multipdf.Splitter;
//import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;

public class SplitByFixedRangeActivity extends AppCompatActivity {
    private static final int REQUEST_PDF = 0;
    boolean isPdfSelected = false;
    Button done, selectPdf;
    TextInputEditText editText;
    Uri pdfUri = null;
    PDFView pdfView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_by_fixed_range);
        final AdView mAdView = findViewById(R.id.ad_banner_split_by_range);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        initViews();

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isPermissionGranted()) {
                    return;
                }
                if(isPdfSelected) {
                    SplitByFixedRangeTask task = new SplitByFixedRangeTask(SplitByFixedRangeActivity.this);
                    task.execute();
                }
                else{
                    Toast.makeText(SplitByFixedRangeActivity.this, "Please Choose a pdf", Toast.LENGTH_SHORT).show();
                }
            }
        });

        selectPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isPermissionGranted()) {
                    return;
                }
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent, REQUEST_PDF);
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
            createDirs();
        }
    }

    private void createDirs() {
        isPermissionGranted();
        File f = new File(Constants.PDF_MERGE_PATH);
        File f2 = new File(Constants.PDF_PATH);
        File f3 = new File(Constants.PDF_SPLIT_PATH);
        File f4 = new File(Constants.PDF_WATERMARK_PATH);
        File f5 = new File(Constants.PDF_STORAGE_PATH);

        if(!f.exists()) {
            f.mkdirs();
        }
        if(!f2.exists()) {
            f2.mkdirs();
        }
        if(!f3.exists()) {
            f3.mkdirs();
        }
        if(!f4.exists()) {
            f4.mkdirs();
        }
        if(!f5.exists()) {
            f5.mkdirs();
        }
    }
    private boolean isPermissionGranted() {
        if ((ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED)|| (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED)|| (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},10);
            }
            return false;
        }
        else{
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_PDF) {
            if(data!=null) {
                if(data.getData()!=null) {
                    pdfUri = data.getData();
                    isPdfSelected = true;
                    pdfView.fromUri(pdfUri).pages(0).load();
                    Toast.makeText(this, "PDF selected", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("com.jayshreegopalapps.ImageToPdf", MODE_PRIVATE);
        if (prefs.getBoolean("splitfixed8", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            prefs.edit().putBoolean("splitfixed8", false).commit();

            FancyShowCaseView fancyShowCaseView1 =new FancyShowCaseView.Builder(this)
                    .focusOn(selectPdf)
                    .title(" \n           " +
                            "\n   " +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "Select a PDF")

                    .build();

            FancyShowCaseView fancyShowCaseView3 =new FancyShowCaseView.Builder(this)
                    .focusOn(editText)
                    .title(" \n           " +
                            "\n   " +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "Enter number to seprate pdf at every fixed page")
                    .build();

            FancyShowCaseView fancyShowCaseView2 =new FancyShowCaseView.Builder(this)
                    .focusOn(done)
                    .title(" \n           " +
                            "\n   " +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "Split PDF")
                    .build();

           /* new FancyShowCaseView.Builder(this)
                    .focusOn(getSupportActionBar().getCustomView().findViewById(R.menu.menu_main))
                    .title("Focus on View")
                    .build()
                    .show();*/
            new FancyShowCaseQueue()
                    .add(fancyShowCaseView1)
                    .add(fancyShowCaseView3)
                    .add(fancyShowCaseView2)
                    .show();
            File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
            if (!docsFolder.exists()) {
                docsFolder.mkdir();
            }
        }
    }

    //    private void splitPdf() {
//        if(editText.getText().toString().equals("")) {
//            editText.setError("Must be non-empty");
//        }
//        else{
//            try {
//                if (Integer.parseInt(editText.getText().toString()) > 0) {
//                    if(pdfUri!=null) {
//                        try {
//                            PdfReader reader = new PdfReader(getContentResolver().openInputStream(pdfUri));
//                            int index = 1;
//                            int totalPages = reader.getNumberOfPages();
//                            int range = Integer.parseInt(editText.getText().toString());
//
//                            while (index <= totalPages) {
//                                int x = 1;
//                                Document document = new Document();
//                                PdfCopy writer = new PdfCopy(document, new FileOutputStream(Constants.PDF_STORAGE_PATH + System.currentTimeMillis() + ".pdf"));
//                                document.open();
//                                while (x <= range && index <= totalPages) {
//                                    PdfImportedPage page = writer.getImportedPage(reader, index);
//                                    writer.addPage(page);
//                                    x++;
//                                    index++;
//                                }
//                                document.close();
//                                writer.close();
//                            }
//                            reader.close();
//                            Toast.makeText(this, "Merged PDF saved at " + Constants.PDF_STORAGE_PATH + System.currentTimeMillis() + ".pdf", Toast.LENGTH_SHORT).show();
//                        } catch (Exception e) {
//                            View parentLayout = findViewById(android.R.id.content);
//                            Snackbar.make(parentLayout, "Failed to save pdf " + e.getMessage(), Snackbar.LENGTH_LONG).show();
//                            e.printStackTrace();
//                        }
//                    }
//                    else{
//                        View parentLayout = findViewById(android.R.id.content);
//                        Snackbar.make(parentLayout, "Please Select a PDF file", Snackbar.LENGTH_LONG).show();
//                    }
//                }
//                else{
//                    Toast.makeText(this, "Must be a greater than 0", Toast.LENGTH_SHORT).show();
//                }
//            }
//            catch (Exception e) {
//                Toast.makeText(this, "Must be a number", Toast.LENGTH_SHORT).show();
//                e.printStackTrace();
//            }
//        }
//    }

    private void initViews() {
        done = findViewById(R.id.button_fixed_range_done);
        selectPdf = findViewById(R.id.button_fixed_range_select_pdf);
        editText = findViewById(R.id.fixed_range_split);
        pdfView = findViewById(R.id.pdf_preview_split);
    }
    public class SplitByFixedRangeTask extends AsyncTask<String, Void, Void> {

        Context context;
        AlertDialog dialog;

        SplitByFixedRangeTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new AlertDialog.Builder(context).setCancelable(false).setView(R.layout.layout_loading_dialog).create();
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            splitPDF();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
        }

        private void splitPDF() {

            if(editText.getText().toString().equals("")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        editText.setError("Must be non-empty");

                    }
                });
            }
            else{
                try {
                    if (Integer.parseInt(editText.getText().toString()) > 0) {
                        if(pdfUri!=null) {
                            try {

                                PDDocument doc = PDDocument.load(getContentResolver().openInputStream(pdfUri));
                                Splitter splitter = new Splitter();
                                int range = Integer.parseInt(editText.getText().toString());


                                final String foldername = DateTimeUtils.getDateTime() + System.currentTimeMillis() + "/";
                                File f = new File(Constants.PDF_SPLIT_PATH + foldername );
                                if(!f.exists()) f.mkdirs();

                                splitter.setSplitAtPage(range);
                                List<PDDocument> l = splitter.split(doc);
                                for (PDDocument d : l) {
                                    d.save(Constants.PDF_SPLIT_PATH + foldername + System.currentTimeMillis() + ".pdf");
                                }
                                doc.close();
                                final View parentLayout = findViewById(android.R.id.content);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Snackbar.make(parentLayout, "PDF saved in " + foldername , Snackbar.LENGTH_LONG).show();
                                    }
                                });
                            } catch (Exception e) {
                                View parentLayout = findViewById(android.R.id.content);
                                Snackbar.make(parentLayout, "Failed to save pdf " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                        else{
                            View parentLayout = findViewById(android.R.id.content);
                            Snackbar.make(parentLayout, "Please Select a PDF file", Snackbar.LENGTH_LONG).show();
                        }
                    }
                    else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "Must be a greater than 0", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }
                catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Must be a number", Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }
            }
        }


    }
}
