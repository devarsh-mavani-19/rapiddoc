package com.shortcontent.imagetopdf;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ImageRotateActivity extends AppCompatActivity {
    ImageView imageView;
    Bitmap bMap;
    String fileName;
    Button rotate_left, rotate_right, save, discard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_rotate);

        initViews();

        extractBundle();

        refreshPage();

        rotate_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Matrix mat = new Matrix();
                mat.postRotate(-90.0f);
                bMap = Bitmap.createBitmap(bMap, 0, 0,bMap.getWidth(),bMap.getHeight(), mat, true);
                imageView.setImageBitmap(bMap);
            }
        });
        rotate_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Matrix mat = new Matrix();
                mat.postRotate(90.0f);
                bMap = Bitmap.createBitmap(bMap, 0, 0,bMap.getWidth(),bMap.getHeight(), mat, true);
                imageView.setImageBitmap(bMap);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();
            }
        });
        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    private void saveImage() {
        if(fileName!=null) {
            try {
                FileOutputStream fos = new FileOutputStream(getExternalFilesDir(null) + "/" + fileName);
                bMap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                Picasso.get().invalidate(getExternalFilesDir(null) + "/" + fileName);
                finish();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    private void refreshPage() {
        bMap = BitmapFactory.decodeFile(getExternalFilesDir(null) + "/" + fileName);
        imageView.setImageBitmap(bMap);
    }

    private void extractBundle() {
        if(getIntent()!=null) {
            if(getIntent().getExtras()!=null) {
                fileName = getIntent().getExtras().getString("image");
            }
        }
    }

    private void initViews() {
        imageView = findViewById(R.id.image_open_image_g);
        rotate_left = findViewById(R.id.button_rotate_left_90_g);
        rotate_right = findViewById(R.id.button_rotate_right_90_g);
        save = findViewById(R.id.btn_save_openimage_g);
        discard = findViewById(R.id.btn_discard_openimage_g);
    }
}
