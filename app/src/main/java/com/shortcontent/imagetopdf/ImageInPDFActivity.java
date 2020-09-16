package com.shortcontent.imagetopdf;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ImageInPDFActivity extends AppCompatActivity {
    Button btnSelectPdf, btnSelectImage;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_in_pdf);
        btnSelectPdf = findViewById(R.id.btn_select_pdf);
        btnSelectImage = findViewById(R.id.btn_select_image);
        imageView = findViewById(R.id.image_in_pdf_image);

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "Select image"), 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode==RESULT_OK) {
            if(data!=null) {
                    Uri selectedImage = data.getData();
                    imageView.setImageURI(selectedImage);
            }
        }
    }
}
