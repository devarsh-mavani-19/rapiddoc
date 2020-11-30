package com.jayshreegopalapps.imagetopdf;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.rendering.PDFRenderer;

import java.io.IOException;

public class PDFToolsActivity extends AppCompatActivity {

    private int REQUEST_PDF = 0;
    ImageView imageView;
    StickerImageView iv_sticker;
    PDDocument document;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdftools);
        FloatingActionButton fab = findViewById(R.id.fab_add_pdf_esign);
        FloatingActionButton fab2 = findViewById(R.id.fab_add_sign);
        FloatingActionButton fab3 = findViewById(R.id.fab_done_esign);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout rl = findViewById(R.id.rl_esign);
                Bitmap b = loadBitmapFromView(rl);
                imageView.setImageBitmap(b);
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EsignatureBottomModal modal = new EsignatureBottomModal(PDFToolsActivity.this, new EsignatureBottomModal.EsignatureListener() {
                    @Override
                    public String save(String path) {
                        Bitmap bmp = BitmapFactory.decodeFile(path);
                        RelativeLayout rl = findViewById(R.id.rl_esign);
                        iv_sticker = new StickerImageView(PDFToolsActivity.this);
                        iv_sticker.setImageBitmap(bmp);
                        rl.addView(iv_sticker);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PDFToolsActivity.this, "Size = " + imageView.getX(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        return null;
                    }

                    @Override
                    public void cancel() {

                    }
                });
                modal.show(getSupportFragmentManager(), "Esign");
            }
        });

        imageView = findViewById(R.id.image_esign);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("application/pdf");
                startActivityForResult(i, REQUEST_PDF);
            }
        });
    }
    public static Bitmap loadBitmapFromView(View v) {
        if (v.getMeasuredHeight() <= 0) {
            v.measure(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
            v.draw(c);
            return b;
        }
        Bitmap b = Bitmap.createBitmap( v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_PDF && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                document = PDDocument.load(getContentResolver().openInputStream(uri));
                final PDDocument finalDocument = document;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PDFToolsActivity.this, "Size = " + finalDocument.getPage(0).getCropBox().getUpperRightY(), Toast.LENGTH_SHORT).show();
                    }
                });
                PDFRenderer pdfRenderer = new PDFRenderer(document);
                Bitmap b = pdfRenderer.renderImage(0);
                imageView.setImageBitmap(b);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
