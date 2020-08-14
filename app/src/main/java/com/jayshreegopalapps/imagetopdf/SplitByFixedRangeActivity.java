package com.jayshreegopalapps.imagetopdf;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;

import java.io.FileOutputStream;

public class SplitByFixedRangeActivity extends AppCompatActivity {
    private static final int REQUEST_PDF = 0;
    boolean isPdfSelected = false;
    Button done, selectPdf;
    TextInputEditText editText;
    Uri pdfUri = null;

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
                if(isPdfSelected) {
                    splitPdf();
                }
                else{
                    Toast.makeText(SplitByFixedRangeActivity.this, "Please Choose a pdf", Toast.LENGTH_SHORT).show();
                }
            }
        });

        selectPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent, REQUEST_PDF);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_PDF) {
            if(data!=null) {
                if(data.getData()!=null) {
                    pdfUri = data.getData();
                    isPdfSelected = true;
                }
            }
        }
    }

    private void splitPdf() {
        if(editText.getText().toString().equals("")) {
            editText.setError("Must be non-empty");
        }
        else{
            try {
                if (Integer.parseInt(editText.getText().toString()) > 0) {
                    if(pdfUri!=null) {
                        try {
                            PdfReader reader = new PdfReader(getContentResolver().openInputStream(pdfUri));
                            int index = 1;
                            int totalPages = reader.getNumberOfPages();
                            int range = Integer.parseInt(editText.getText().toString());

                            while (index <= totalPages) {
                                int x = 1;
                                Document document = new Document();
                                PdfCopy writer = new PdfCopy(document, new FileOutputStream(Constants.PDF_STORAGE_PATH + System.currentTimeMillis() + ".pdf"));
                                document.open();
                                while (x <= range && index <= totalPages) {
                                    PdfImportedPage page = writer.getImportedPage(reader, index);
                                    writer.addPage(page);
                                    x++;
                                    index++;
                                }
                                document.close();
                                writer.close();
                            }
                            reader.close();
                            Toast.makeText(this, "Merged PDF saved at " + Constants.PDF_STORAGE_PATH + System.currentTimeMillis() + ".pdf", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(this, "Must be a greater than 0", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e) {
                Toast.makeText(this, "Must be a number", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private void initViews() {
        done = findViewById(R.id.button_fixed_range_done);
        selectPdf = findViewById(R.id.button_fixed_range_select_pdf);
        editText = findViewById(R.id.fixed_range_split);
    }
}
