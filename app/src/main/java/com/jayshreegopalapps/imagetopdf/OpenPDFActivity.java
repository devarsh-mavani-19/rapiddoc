package com.jayshreegopalapps.imagetopdf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class OpenPDFActivity extends AppCompatActivity {
    PDFView pdfView;
    Uri pdf_location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_pdf);
        pdf_location = getIntent().getData();
        if(!getIntent().getType().equals("application/pdf")) {
            Toast.makeText(this, "Invalid File Format", Toast.LENGTH_SHORT).show();
            finish();
        }
        pdfView = findViewById(R.id.open_pdf_view);
        System.out.println("OPENPDF " + pdf_location);

        if (pdf_location != null) {
            pdfView.fromUri(pdf_location).defaultPage(0).onPageError(new OnPageErrorListener() {
                @Override
                public void onPageError(int page, Throwable t) {
                    t.printStackTrace();
                }
            }).load();
        }

    }
}
