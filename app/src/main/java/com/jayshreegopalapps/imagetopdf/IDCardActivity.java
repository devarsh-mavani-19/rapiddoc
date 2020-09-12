package com.jayshreegopalapps.imagetopdf;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jcmore2.collage.CollageView;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IDCardActivity extends AppCompatActivity {
    ImageView imageView1, imageView2;
    FloatingActionButton fabDone;
    private int REQUEST_CUSTOM_CAMERA = 0;
    List<Bitmap> listRes = new ArrayList<>();
    boolean isFirstImageSelected = false;
    boolean isSecondImageSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idcard);
        imageView1 = findViewById(R.id.idcard_image1);
        imageView2 = findViewById(R.id.idcard_image2);
        fabDone = findViewById(R.id.fab_done_idcard);

        Intent customCamera = new Intent(IDCardActivity.this, CustomCameraActivity.class);
        startActivityForResult(customCamera, REQUEST_CUSTOM_CAMERA);

        fabDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = System.currentTimeMillis() + ".pdf";
                File f = new File(Constants.PDF_2SIDE);
                if(!f.exists()) {
                    f.mkdirs();
                }
                f = new File(Constants.PDF_2SIDE + filename);
                try {
                    long fosPath =  System.currentTimeMillis();
                    String p1 = getExternalFilesDir(null) + "/" +fosPath + ".png";
                    String p2 = getExternalFilesDir(null) + "/" +(fosPath + 1) + ".png";

                    listRes.get(0).compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(p1));
                    listRes.get(1).compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(p2));
                    PDDocument document = new PDDocument();
                    PDPage page=new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    PDImageXObject pdImage = PDImageXObject.createFromFile(p1, document);
                    PDImageXObject pdImage2 = PDImageXObject.createFromFile(p2, document);
                    PDPageContentStream contentStream = new PDPageContentStream(document, page);
                    contentStream.drawImage(pdImage, 297.72f - 118.908f, PDRectangle.A4.getHeight() - 50 - 147.672f, 237.816f, 147.672f);
                    contentStream.drawImage(pdImage2, 297.72f - 118.908f, PDRectangle.A4.getHeight() - 50 - 147.672f - 147.672f - 50, 237.816f, 147.672f);
                    contentStream.close();
                    document.save(f);
                    document.close();


                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Bitmap getBitmapOfImageViewsTable(View imageViewsParent){
        imageViewsParent.setDrawingCacheEnabled(true);

        imageViewsParent.buildDrawingCache();
        Bitmap b = imageViewsParent.getDrawingCache();
        if(b == null){
            b = getViewBitmapAlt(imageViewsParent);
        }
        return getCopyOfBitmap(b); // because android automatically recycles the bitmap after some time
    }

    //https://stackoverflow.com/a/11937411/1463931
    public Bitmap getViewBitmapAlt(View v)
    {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
        v.draw(c);
        return b;
    }
    public Bitmap getCopyOfBitmap(Bitmap bm){
        //then create a copy of bitmap bmp1 into bmp2
        return bm.copy(bm.getConfig(), true);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CUSTOM_CAMERA && resultCode == RESULT_OK) {
            if(data!=null) {
                String location = data.getStringExtra("location");
                int REQUEST_CODE = 1;
                int preference = ScanConstants.OPEN_CAMERA;
                Intent intent = new Intent(IDCardActivity.this, ScanActivity.class);
                intent.putExtra("location" , location);
                intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
                startActivityForResult(intent, 1);
            }
            else {
                Toast.makeText(this, "Couldn't get Image", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == 1 && resultCode == RESULT_OK) {
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            try{
                Bitmap b = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                if(!isFirstImageSelected) isFirstImageSelected = true;
                else isSecondImageSelected = true;
                listRes.add(b);
                if(!isSecondImageSelected) {
                    Intent customCamera = new Intent(IDCardActivity.this, CustomCameraActivity.class);
                    startActivityForResult(customCamera, REQUEST_CUSTOM_CAMERA);

                } else{
                    float inchX = 3.303f;
                    float inchY = 2.051f;
                    imageView1.getLayoutParams().width = ((int) (inchX * getResources().getDisplayMetrics().densityDpi));
                    imageView1.getLayoutParams().height = ((int) (inchY * getResources().getDisplayMetrics().densityDpi));
                    imageView1.setImageBitmap(listRes.get(0));

                    imageView2.getLayoutParams().width = ((int) (inchX * getResources().getDisplayMetrics().densityDpi));
                    imageView2.getLayoutParams().height = ((int) (inchY * getResources().getDisplayMetrics().densityDpi));
                    imageView2.setImageBitmap(listRes.get(1));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
