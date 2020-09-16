package com.shortcontent.imagetopdf;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.FileNotFoundException;

public class OCRActivity extends AppCompatActivity {
    Button btnCamera, btnGallery, btnExtract;
    ImageView image;
    Bitmap bitmap;
    private boolean isImageSelected = false;
    private int REQUEST_CAMERA = 0;
    private int REQUEST_GALLERY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);
        btnCamera = findViewById(R.id.btn_capture_image);
        btnGallery = findViewById(R.id.btn_open_gallery);
        btnExtract = findViewById(R.id.btn_extract_text);
        image = findViewById(R.id.ocr_image);
        btnExtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isImageSelected) {
                    extractText();
                }
                else{
                    Toast.makeText(OCRActivity.this, "Please Select an Image", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OCRActivity.this, CustomCameraActivity.class);
                startActivityForResult(i, REQUEST_CAMERA);
            }
        });
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i, REQUEST_GALLERY);
            }
        });
    }

    private void extractText() {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(OCRActivity.this).build();
        if(!textRecognizer.isOperational()) {
            Toast.makeText(this, "Could Not Get The Text", Toast.LENGTH_SHORT).show();
        } else{
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> items = textRecognizer.detect(frame);
            StringBuilder builder = new StringBuilder();
            for(int i = 0;i < items.size();i++) {
                TextBlock mItems  = items.valueAt(i);
                builder.append(mItems.getValue());
                builder.append("\n");
            }
            new BottomModalQr(builder.toString(), OCRActivity.this, new BottomModalQr.BottomModalQrListener() {
                @Override
                public void close() {

                }
            }).show(getSupportFragmentManager(), "OCR");
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            if (data != null) {
                String location = data.getStringExtra("location");
                bitmap = BitmapFactory.decodeFile(location);
                image.setImageBitmap(bitmap);
                isImageSelected = true;
            }
        }
        if(requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            if(data!=null) {
                Uri uri = data.getData();
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                    image.setImageBitmap(bitmap);
                    isImageSelected = true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
