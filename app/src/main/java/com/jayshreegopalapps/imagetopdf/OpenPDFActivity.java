package com.jayshreegopalapps.imagetopdf;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.tom_roush.pdfbox.contentstream.PDContentStream;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.PDResources;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.util.Matrix;

import java.io.IOException;
import java.io.InputStream;

public class OpenPDFActivity extends AppCompatActivity {
    PDFView pdfView;
    Uri pdf_location;
    PDDocument document;

    @Override
    protected void onResume() {
        super.onResume();
        LoadSettings.load(OpenPDFActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_pdf);
        getSupportActionBar().hide();
        pdf_location = getIntent().getData();
        if (!getIntent().getType().equals("application/pdf")) {
            Toast.makeText(this, getString(R.string.invalid_format), Toast.LENGTH_SHORT).show();
            finish();
        }
        pdfView = findViewById(R.id.open_pdf_view);
        System.out.println("OPENPDF " + pdf_location);
        if (pdf_location != null) {
            try {
                document = PDDocument.load(getContentResolver().openInputStream(pdf_location));
            } catch (IOException e) {
                e.printStackTrace();
            }
//            loading pdf
            pdfView.fromUri(pdf_location).enableSwipe(true).defaultPage(0).onPageError(new OnPageErrorListener() {
                @Override
                public void onPageError(int page, Throwable t) {
                    t.printStackTrace();
                }
            }).load();

//            fab_save.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(iv_sticker!=null) {
//                        Bitmap b = iv_sticker.getImageBitmap();
////                        imageView.setImageBitmap(b);
////                        imageView.setX(iv_sticker.getX());
////                        imageView.setY(iv_sticker.getY());
////                        imageView.setMaxWidth(iv_sticker.getWidth());
////                        imageView.setMaxHeight(iv_sticker.getHeight());
//
//                        String fName = Constants.ESIGNATURE_PATH + System.currentTimeMillis() + ".png";
//                        try {
//                            b.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(fName));
//                            //loading document in PDF box PDDocument object
//                            PDDocument document = PDDocument.load(getContentResolver().openInputStream(pdf_location));
//                            PDPage cPage = document.getPage(currentPage);
//                            PDImageXObject image = PDImageXObject.createFromFile(fName, document);
//                            PDPageContentStream contentStream = new PDPageContentStream(document,  cPage, true, false);
//
//                            System.out.println("height = " + imageView.getHeight());
//
//
//
//
////                            contentStream.drawImage(image,newX - (iv_sticker.getWidth() * (parent.getWidth() / PDRectangle.A4.getWidth())), newY , iv_sticker.getWidth() * (PDRectangle.A4.getWidth() / parent.getWidth()), image.getHeight() * (PDRectangle.A4.getHeight() / parent.getHeight()));
//                            float width = iv_sticker.getImageDrawable().getIntrinsicWidth() * (( 72.0f / getResources().getDisplayMetrics().densityDpi));
//                            float height = iv_sticker.getImageDrawable().getIntrinsicHeight()  * ((72.0f / getResources().getDisplayMetrics().densityDpi));
//                            System.out.println("Width  = " + pdfView.getOptimalPageWidth());
//
////                            contentStream.drawImage(image, iv_sticker.getX(),imageView.getHeight() - iv_sticker.getY() - height, width, height);
////                            contentStream.drawImage(image,iv_sticker.getX(), ((imageView.getHeight() - (iv_sticker.getY())) - (0) - 0), width, height);
//                            contentStream.drawImage(image,iv_sticker.getX() * ( 72.0f / getResources().getDisplayMetrics().densityDpi), ((cPage.getMediaBox().getHeight() - (iv_sticker.getY() * ( 72.0f / getResources().getDisplayMetrics().densityDpi))) - (height)), width, height);
////                            contentStream.drawImage(image,((iv_sticker.getX() * 0.752929f) * 1), ((currentPage.getCropBox().getHeight()) - ((iv_sticker.getY() * 0.752929f) * 1) - (height)), width, height);
//                            contentStream.close();
//                            document.save(Constants.ESIGNATURE_PATH + "xyz.pdf");
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            });


//            fab_esignature.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    Intent x = new Intent(OpenPDFActivity.this, EsignatureActivity.class);
////                    startActivityForResult(x, REQUEST_ESIGN);
//
//                    EsignatureBottomModal modal = new EsignatureBottomModal(OpenPDFActivity.this, new EsignatureBottomModal.EsignatureListener() {
//                        @SuppressLint("ClickableViewAccessibility")
//                        @Override
//                        public String save(final String path) {
////                            final ImageView imageView = findViewById(R.id.open_pdf_esign_image);
//                            Bitmap bmp = BitmapFactory.decodeFile(path);
//                            iv_sticker = new StickerImageView(OpenPDFActivity.this);
//                            iv_sticker.setImageBitmap(bmp);
//
//                            parent.addView(iv_sticker);
//                            iv_sticker.bringToFront();
//                            iv_sticker.setX(0);
//                            iv_sticker.setY(0);
////                            imageView.setImageBitmap(bmp);
//                            imageView.setOnTouchListener(new View.OnTouchListener() {
//                                @Override
//                                public boolean onTouch(View v, MotionEvent event) {
//                                    try {
//
//
//
////                                        PDDocument document = PDDocument.load(getContentResolver().openInputStream(pdf_location));
////                                        PDImageXObject object = PDImageXObject.createFromFile(path, document);
////                                        PDPage page = document.getPage(pdfView.getCurrentPage());
////                                        PDPageContentStream contentStream = new PDPageContentStream(document,page);
////                                        contentStream.drawImage(object, 50, 50, PDRectangle.A4.getWidth() - 50, PDRectangle.A4.getHeight() - 50);
////                                        document.save(Constants.ESIGNATURE_PATH + System.currentTimeMillis() + ".pdf");
////                                        contentStream.close();
////                                        document.close();
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                    return true;
//                                }
//                            });
//                            return null;
//                        }
//
//                        @Override
//                        public void cancel() {
//
//                        }
//                    });
//                    modal.show(getSupportFragmentManager(), "");
//                }
//            });

        }
    }
}
