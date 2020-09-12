package com.jayshreegopalapps.imagetopdf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
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
import com.tom_roush.pdfbox.rendering.PDFRenderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class OpenPDFActivity extends AppCompatActivity {
    PDFView pdfView;
    FloatingActionButton fab_esignature, fab_save;
    Uri pdf_location;
    String esignature_location = null;
    private int REQUEST_ESIGN  = 0;
    StickerImageView iv_sticker;
    ConstraintLayout parent;
    ImageView imageView;
    Button btnPrev, btnNext;
    int currentPage=0;
    PDDocument document;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_pdf);
        getSupportActionBar().hide();
        pdf_location = getIntent().getData();
        if(!getIntent().getType().equals("application/pdf")) {
            Toast.makeText(this, "Invalid File Format", Toast.LENGTH_SHORT).show();
            finish();
        }
        parent = findViewById(R.id.constraint_parent);
        pdfView = findViewById(R.id.open_pdf_view);
        btnNext = findViewById(R.id.open_pdf_next);
        btnPrev = findViewById(R.id.open_pdf_prev);
        fab_esignature = findViewById(R.id.open_pdf_add_esignature);
        fab_save = findViewById(R.id.open_pdf_add_esignature_done);
        imageView = findViewById(R.id.image_view_open_pdf);
        System.out.println("OPENPDF " + pdf_location);
        if (pdf_location != null) {
            try {
                document = PDDocument.load(getContentResolver().openInputStream(pdf_location));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //loading pdf
//            pdfView.fromUri(pdf_location).enableSwipe(true).defaultPage(0).onPageError(new OnPageErrorListener() {
//                @Override
//                public void onPageError(int page, Throwable t) {
//                    t.printStackTrace();
//                }
//            }).load();

            fab_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(iv_sticker!=null) {
                        Bitmap b = iv_sticker.getImageBitmap();
//                        imageView.setImageBitmap(b);
//                        imageView.setX(iv_sticker.getX());
//                        imageView.setY(iv_sticker.getY());
//                        imageView.setMaxWidth(iv_sticker.getWidth());
//                        imageView.setMaxHeight(iv_sticker.getHeight());

                        String fName = Constants.ESIGNATURE_PATH + System.currentTimeMillis() + ".png";
                        try {
                            b.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(fName));
                            //loading document in PDF box PDDocument object
                            PDDocument document = PDDocument.load(getContentResolver().openInputStream(pdf_location));
                            PDPage cPage = document.getPage(currentPage);
                            PDImageXObject image = PDImageXObject.createFromFile(fName, document);
                            PDPageContentStream contentStream = new PDPageContentStream(document,  cPage, true, false);

                           System.out.println("y = " + cPage.getCropBox().getWidth() + " , " + cPage.getMediaBox().getWidth());





//                            contentStream.drawImage(image,newX - (iv_sticker.getWidth() * (parent.getWidth() / PDRectangle.A4.getWidth())), newY , iv_sticker.getWidth() * (PDRectangle.A4.getWidth() / parent.getWidth()), image.getHeight() * (PDRectangle.A4.getHeight() / parent.getHeight()));
                            float width = iv_sticker.getImageDrawable().getIntrinsicWidth() * (( 72.0f / getResources().getDisplayMetrics().densityDpi));
                            float height = iv_sticker.getImageDrawable().getIntrinsicHeight()  * ((72.0f / getResources().getDisplayMetrics().densityDpi));
                            System.out.println("Width  = " + pdfView.getOptimalPageWidth());

//                            contentStream.drawImage(image,iv_sticker.getX(), ((imageView.getHeight() - (iv_sticker.getY())) - (0) - 0), width, height);
                            contentStream.drawImage(image,iv_sticker.getX() * (cPage.getMediaBox().getWidth() / imageView.getWidth()), ((imageView.getHeight() * (cPage.getMediaBox().getHeight() / imageView.getHeight())) - (iv_sticker.getY() * (cPage.getMediaBox().getHeight() / imageView.getHeight()))) - (height), width, height);
//                            contentStream.drawImage(image,((iv_sticker.getX() * 0.752929f) * 1), ((currentPage.getCropBox().getHeight()) - ((iv_sticker.getY() * 0.752929f) * 1) - (height)), width, height);
                            contentStream.close();
                            document.save(Constants.ESIGNATURE_PATH + "xyz.pdf");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            btnPrev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentPage == 0) {

                    }
                    else {
                        currentPage--;
                        renderPdf();
                    }
                }
            });
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentPage < document.getNumberOfPages() - 1) {
                        currentPage++;
                        renderPdf();
                    }
                }
            });
            renderPdf();

            fab_esignature.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent x = new Intent(OpenPDFActivity.this, EsignatureActivity.class);
//                    startActivityForResult(x, REQUEST_ESIGN);

                    EsignatureBottomModal modal = new EsignatureBottomModal(OpenPDFActivity.this, new EsignatureBottomModal.EsignatureListener() {
                        @SuppressLint("ClickableViewAccessibility")
                        @Override
                        public String save(final String path) {
//                            final ImageView imageView = findViewById(R.id.open_pdf_esign_image);
                            Bitmap bmp = BitmapFactory.decodeFile(path);
                            iv_sticker = new StickerImageView(OpenPDFActivity.this);
                            iv_sticker.setImageBitmap(bmp);

                            parent.addView(iv_sticker);
//                            imageView.setImageBitmap(bmp);
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

    private void renderPdf() {
        try {
            PDFRenderer renderer = new PDFRenderer(document);
            Bitmap bitmap = renderer.renderImageWithDPI(currentPage, getResources().getDisplayMetrics().densityDpi);
            imageView.setImageBitmap(bitmap);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_ESIGN && resultCode == RESULT_OK) {
            final String location = data.getStringExtra("location");
//            final ImageView imageView = findViewById(R.id.open_pdf_esign_image);
            Bitmap bmp = BitmapFactory.decodeFile(location);
//            imageView.setImageBitmap(bmp);
            final float[] tempX = new float[1];
            final float[] tempY = new float[1];
            final int[] mActivePointerId = new int[1];



//            imageView.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    try {
//                        final int action = MotionEventCompat.getActionMasked(event);
//
//                        switch (action) {
//                            case MotionEvent.ACTION_DOWN: {
//                                final int pointerIndex = MotionEventCompat.getActionIndex(event);
//                                final float x = MotionEventCompat.getX(event, pointerIndex);
//                                final float y = MotionEventCompat.getY(event, pointerIndex);
//
//                                // Remember where we started (for dragging)
//                                tempX[0] = x;
//                                tempY[0] = y;
//                                // Save the ID of this pointer (for dragging)
//                                mActivePointerId[0] = MotionEventCompat.getPointerId(event, 0);
//                                break;
//                            }
//
//                            case MotionEvent.ACTION_MOVE: {
//                                // Find the index of the active pointer and fetch its position
//                                final int pointerIndex =
//                                        MotionEventCompat.findPointerIndex(event, mActivePointerId[0]);
//
//                                final float x = MotionEventCompat.getX(event, pointerIndex);
//                                final float y = MotionEventCompat.getY(event, pointerIndex);
//
//                                // Calculate the distance moved
//                                final float dx = x - tempX[0];
//                                final float dy = y - tempY[0];
//
//                                imageView.setX(imageView.getX() + dx);
//                                imageView.setY(imageView.getY() + dy);
////                                event.getSp
//                                imageView.invalidate();
//
//                                // Remember this touch position for the next move event
//                                tempX[0]= x;
//                                tempY[0] = y;
//
//                                break;
//                            }
//
//                            case MotionEvent.ACTION_UP:
//
//                            case MotionEvent.ACTION_CANCEL: {
//                                mActivePointerId[0] = -1;
//                                break;
//                            }
//
//                            case MotionEvent.ACTION_POINTER_UP: {
//
//                                final int pointerIndex = MotionEventCompat.getActionIndex(event);
//                                final int pointerId = MotionEventCompat.getPointerId(event, pointerIndex);
//
//                                if (pointerId == mActivePointerId[0]) {
//                                    // This was our active pointer going up. Choose a new
//                                    // active pointer and adjust accordingly.
//                                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
//                                    tempX[0] = MotionEventCompat.getX(event, newPointerIndex);
//                                    tempY[0] = MotionEventCompat.getY(event, newPointerIndex);
//                                    mActivePointerId[0] = MotionEventCompat.getPointerId(event, newPointerIndex);
//                                }
//                                break;
//                            }
//                        }
//                        return true;
//
//
////                        PDDocument document = PDDocument.load(getContentResolver().openInputStream(pdf_location));
////                        PDImageXObject object = PDImageXObject.createFromFile(location, document);
////                        PDPage page = document.getPage(pdfView.getCurrentPage());
////                        PDPageContentStream contentStream = new PDPageContentStream(document,page);
////                        contentStream.drawImage(object, 50, 50, PDRectangle.A4.getWidth() - 50, PDRectangle.A4.getHeight() - 50);
////                        document.save(Constants.ESIGNATURE_PATH + System.currentTimeMillis() + ".pdf");
////                        contentStream.close();
////                        document.close();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    return true;
//                }
//
//            });
        }
    }
}
