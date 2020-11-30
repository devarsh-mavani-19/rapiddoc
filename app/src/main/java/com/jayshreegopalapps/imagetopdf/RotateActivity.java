package com.jayshreegopalapps.imagetopdf;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.util.Matrix;

public class RotateActivity extends AppCompatActivity {
    FloatingActionButton fab_add, fab_done;
    PDFView pdfView;
    Uri uri;
    private int REQUEST_PDF = 0;
    ImageView rotateLeft, rotateRight;
    TextView seekbarprogress;
    int degrees = 0;

    @Override
    protected void onResume() {
        super.onResume();
        LoadSettings.load(RotateActivity.this);
        LoadSettings.setViewTheme(fab_add, RotateActivity.this);
        LoadSettings.setViewTheme(fab_done, RotateActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotate);
        fab_add = findViewById(R.id.fab_add_pdf_rotate);
        fab_done = findViewById(R.id.fab_done_pdf_rotate);
        pdfView = findViewById(R.id.pdf_view_rotate);
        seekbarprogress = findViewById(R.id.seekbar_progress);
        rotateLeft = findViewById(R.id.rotate_pdf_left);
        rotateRight = findViewById(R.id.rotate_pdf_right);

        rotateLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                degrees -= 90;
                if(degrees == 360) {
                    degrees = 0;
                }
                if(degrees == -360) {
                    degrees = 0;
                }
                seekbarprogress.setText((degrees + " Deg"));
            }
        });

        rotateRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                degrees += 90;
                if(degrees == 360) {
                    degrees = 0;
                }
                if(degrees == -360) {
                    degrees = 0;
                }
                seekbarprogress.setText((degrees + " Deg"));
            }
        });

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("application/pdf");
                startActivityForResult(i, REQUEST_PDF);
            }
        });
        fab_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final AlertDialog dialog = new AlertDialog.Builder(RotateActivity.this).setView(R.layout.layout_loading_dialog).setCancelable(false).create();
                    dialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                PDDocument document = PDDocument.load(getContentResolver().openInputStream(uri));
                                for (PDPage page : document.getPages()) {
                                    page.setRotation(degrees);
                                }
                                final String fileName = System.currentTimeMillis() + ".pdf";
                                document.save(Constants.PDF_ROTATE + fileName);
                                document.close();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent i = new Intent(RotateActivity.this, OpenPDFActivity.class);
                                        i.setDataAndType(Uri.parse("file:///" + Constants.PDF_ROTATE + fileName), "application/pdf");
                                        startActivity(i);
                                        Toast.makeText(RotateActivity.this, getString(R.string.pdf_saved_at) + Constants.PDF_ROTATE + fileName, Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_PDF && resultCode == RESULT_OK) {
            if(data!=null) {
                uri = data.getData();
                pdfView.fromUri(uri).pages(0).load();
            }
        }
    }
}
