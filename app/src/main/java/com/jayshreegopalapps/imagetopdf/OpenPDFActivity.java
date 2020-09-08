package com.jayshreegopalapps.imagetopdf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.view.MotionEventCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;
import com.tom_roush.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class OpenPDFActivity extends AppCompatActivity {
    PDFView pdfView;
    FloatingActionButton fab_esignature;
    Uri pdf_location;
    String esignature_location = null;
    private int REQUEST_ESIGN  = 0;

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
        fab_esignature = findViewById(R.id.open_pdf_add_esignature);
        System.out.println("OPENPDF " + pdf_location);

        if (pdf_location != null) {
            pdfView.fromUri(pdf_location).defaultPage(0).onPageError(new OnPageErrorListener() {
                @Override
                public void onPageError(int page, Throwable t) {
                    t.printStackTrace();
                }
            }).load();
            fab_esignature.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent x = new Intent(OpenPDFActivity.this, EsignatureActivity.class);
//                    startActivityForResult(x, REQUEST_ESIGN);

                    EsignatureBottomModal modal = new EsignatureBottomModal(OpenPDFActivity.this, new EsignatureBottomModal.EsignatureListener() {
                        @SuppressLint("ClickableViewAccessibility")
                        @Override
                        public String save(final String path) {
                            final ImageView imageView = findViewById(R.id.open_pdf_esign_image);
                            Bitmap bmp = BitmapFactory.decodeFile(path);
                            imageView.setImageBitmap(bmp);
                            imageView.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    try {



//                                        PDDocument document = PDDocument.load(getContentResolver().openInputStream(pdf_location));
//                                        PDImageXObject object = PDImageXObject.createFromFile(path, document);
//                                        PDPage page = document.getPage(pdfView.getCurrentPage());
//                                        PDPageContentStream contentStream = new PDPageContentStream(document,page);
//                                        contentStream.drawImage(object, 50, 50, PDRectangle.A4.getWidth() - 50, PDRectangle.A4.getHeight() - 50);
//                                        document.save(Constants.ESIGNATURE_PATH + System.currentTimeMillis() + ".pdf");
//                                        contentStream.close();
//                                        document.close();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    return true;
                                }
                            });
                            return null;
                        }

                        @Override
                        public void cancel() {

                        }
                    });
                    modal.show(getSupportFragmentManager(), "");
                }
            });

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_ESIGN && resultCode == RESULT_OK) {
            final String location = data.getStringExtra("location");
            final ImageView imageView = findViewById(R.id.open_pdf_esign_image);
            Bitmap bmp = BitmapFactory.decodeFile(location);
            imageView.setImageBitmap(bmp);
            final float[] tempX = new float[1];
            final float[] tempY = new float[1];
            final int[] mActivePointerId = new int[1];



            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    try {
                        final int action = MotionEventCompat.getActionMasked(event);

                        switch (action) {
                            case MotionEvent.ACTION_DOWN: {
                                final int pointerIndex = MotionEventCompat.getActionIndex(event);
                                final float x = MotionEventCompat.getX(event, pointerIndex);
                                final float y = MotionEventCompat.getY(event, pointerIndex);

                                // Remember where we started (for dragging)
                                tempX[0] = x;
                                tempY[0] = y;
                                // Save the ID of this pointer (for dragging)
                                mActivePointerId[0] = MotionEventCompat.getPointerId(event, 0);
                                break;
                            }

                            case MotionEvent.ACTION_MOVE: {
                                // Find the index of the active pointer and fetch its position
                                final int pointerIndex =
                                        MotionEventCompat.findPointerIndex(event, mActivePointerId[0]);

                                final float x = MotionEventCompat.getX(event, pointerIndex);
                                final float y = MotionEventCompat.getY(event, pointerIndex);

                                // Calculate the distance moved
                                final float dx = x - tempX[0];
                                final float dy = y - tempY[0];

                                imageView.setX(imageView.getX() + dx);
                                imageView.setY(imageView.getY() + dy);
//                                event.getSp
                                imageView.invalidate();

                                // Remember this touch position for the next move event
                                tempX[0]= x;
                                tempY[0] = y;

                                break;
                            }

                            case MotionEvent.ACTION_UP:

                            case MotionEvent.ACTION_CANCEL: {
                                mActivePointerId[0] = -1;
                                break;
                            }

                            case MotionEvent.ACTION_POINTER_UP: {

                                final int pointerIndex = MotionEventCompat.getActionIndex(event);
                                final int pointerId = MotionEventCompat.getPointerId(event, pointerIndex);

                                if (pointerId == mActivePointerId[0]) {
                                    // This was our active pointer going up. Choose a new
                                    // active pointer and adjust accordingly.
                                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                                    tempX[0] = MotionEventCompat.getX(event, newPointerIndex);
                                    tempY[0] = MotionEventCompat.getY(event, newPointerIndex);
                                    mActivePointerId[0] = MotionEventCompat.getPointerId(event, newPointerIndex);
                                }
                                break;
                            }
                        }
                        return true;


//                        PDDocument document = PDDocument.load(getContentResolver().openInputStream(pdf_location));
//                        PDImageXObject object = PDImageXObject.createFromFile(location, document);
//                        PDPage page = document.getPage(pdfView.getCurrentPage());
//                        PDPageContentStream contentStream = new PDPageContentStream(document,page);
//                        contentStream.drawImage(object, 50, 50, PDRectangle.A4.getWidth() - 50, PDRectangle.A4.getHeight() - 50);
//                        document.save(Constants.ESIGNATURE_PATH + System.currentTimeMillis() + ".pdf");
//                        contentStream.close();
//                        document.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }

            });
        }
    }
}
