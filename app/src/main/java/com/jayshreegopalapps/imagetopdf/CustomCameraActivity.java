package com.jayshreegopalapps.imagetopdf;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.scanlibrary.ScanConstants;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CustomCameraActivity extends AppCompatActivity {
    Camera camera;
    FrameLayout frameLayout;
    ShowCamera showCamera;
    Button btn_capture;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_camera);
        frameLayout = findViewById(R.id.custom_camera_frame);
        camera = Camera.open();
        showCamera = new ShowCamera(this, camera);
        imageView = findViewById(R.id.custom_camera_image);
        frameLayout.addView(showCamera);
        btn_capture = findViewById(R.id.custom_camera_capture);
        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });

    }


    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            camera.stopPreview();
            frameLayout.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);
            try {
                FileOutputStream fos = new FileOutputStream(ScanConstants.IMAGE_PATH + "/" + "m_image.png");
                fos.write(data);
                fos.close();

            } catch (Exception e) {
                Toast.makeText(CustomCameraActivity.this, "Failed To Save", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    };

    private void captureImage() {
        if(camera!=null) {
            camera.takePicture(null,null,mPictureCallback);
        }
    }
}
