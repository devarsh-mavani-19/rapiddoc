package com.jayshreegopalapps.imagetopdf;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.scanlibrary.ScanConstants;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import me.dm7.barcodescanner.core.CameraPreview;

public class CustomCameraActivity extends AppCompatActivity {
    public Camera camera;
    FrameLayout frameLayout;
    ShowCamera showCamera;
    FloatingActionButton back, next, left, right;
    ImageView btn_capture, imageView,flash;
    private boolean isFlashOn = false;

    @Override
    protected void onResume() {
        super.onResume();
        RelativeLayout relativeLayout = findViewById(R.id.custom_camera_top);
        LoadSettings.setViewTheme(relativeLayout, CustomCameraActivity.this);
        LoadSettings.load(CustomCameraActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_custom_camera);
        frameLayout = findViewById(R.id.custom_camera_frame);
        camera = Camera.open();
        flash = findViewById(R.id.custom_camera_flash);

        showCamera = new ShowCamera(this, camera);
        imageView = findViewById(R.id.custom_camera_image);
        back = findViewById(R.id.custom_camera_back);
        next = findViewById(R.id.custom_camera_next);
        left = findViewById(R.id.custom_camera_rotate_left);
        right = findViewById(R.id.custom_camera_rotate_right);
        frameLayout.addView(showCamera);
        camera.getParameters().setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

        btn_capture = findViewById(R.id.custom_camera_capture);
        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });
        flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFlashOn) {
                    Camera.Parameters p = camera.getParameters();
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(p);
                    flash.setImageResource(R.drawable.ic_flash_off_black_24dp);
                    isFlashOn = false;
                }
                else {
                    Camera.Parameters p = camera.getParameters();
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    camera.setParameters(p);
                    flash.setImageResource(R.drawable.ic_flash_on_black_24dp);
                    isFlashOn = true;
                }
            }
        });
    }


    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
            camera.stopPreview();
            frameLayout.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);
            back.show();
            next.show();
            btn_capture.setVisibility(View.INVISIBLE);
            left.show();
            right.show();
            try {
                final AlertDialog d = new AlertDialog.Builder(CustomCameraActivity.this).setView(R.layout.layout_loading_dialog).create();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        d.show();
                    }
                });
                final Bitmap[][] b = new Bitmap[1][1];
                final Bitmap[][] n = new Bitmap[1][1];
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            b[0] = new Bitmap[]{BitmapFactory.decodeByteArray(data, 0, data.length)};
                            n[0] = new Bitmap[]{Bitmap.createScaledBitmap(b[0][0], (int) (b[0][0].getWidth() * 0.6), (int) (b[0][0].getHeight() * 0.3), false)};
                            File f = new File(ScanConstants.IMAGE_PATH);
                            if (!f.exists()) {
                                f.mkdir();
                            }
                            n[0][0].compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(ScanConstants.IMAGE_PATH + "/m_image.png"));
                            imageView.setImageBitmap(n[0][0]);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    d.dismiss();
                                }
                            });
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();


                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        b[0].recycle();
//                        n[0].recycle();
//                        System.gc();
                        next.hide();
                        back.hide();
                        imageView.setVisibility(View.INVISIBLE);
                        frameLayout.setVisibility(View.VISIBLE);
                        btn_capture.setVisibility(View.VISIBLE);
                        left.hide();
                        right.hide();
                        startCamera();
                    }
                });

                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            final AlertDialog dialog = new AlertDialog.Builder(CustomCameraActivity.this).setView(R.layout.layout_loading_dialog).setCancelable(false).create();
                            dialog.show();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        FileOutputStream fos = new FileOutputStream(ScanConstants.IMAGE_PATH + "/" + "m_image.png");
                                        b[0][0].compress(Bitmap.CompressFormat.PNG, 100, fos);
                                        fos.close();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.dismiss();
                                                Intent i = new Intent();
                                                i.putExtra("location", ScanConstants.IMAGE_PATH + "/" + "m_image.png");
                                                setResult(RESULT_OK, i);
                                                finish();
                                            }
                                        });
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
                left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Matrix mat = new Matrix();
                         mat.postRotate(-90.0f);
                       n[0][0] = Bitmap.createBitmap(n[0][0], 0, 0, n[0][0].getWidth(), n[0][0].getHeight(), mat, true);
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               imageView.setImageBitmap(n[0][0]);
                           }
                       });
                    }
                });
                right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Matrix mat = new Matrix();
                        mat.postRotate(90.0f);
                        n[0][0] = Bitmap.createBitmap(n[0][0], 0, 0, n[0][0].getWidth(), n[0][0].getHeight(), mat, true);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(n[0][0]);

                            }
                        });
                    }
                });
            } catch (Exception e) {
                Toast.makeText(CustomCameraActivity.this, getString(R.string.failed_to_save), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                setResult(RESULT_CANCELED);
            }
        }
    };

    private void startCamera() {
        camera.startPreview();
    }

    private void captureImage() {
        if(camera!=null) {
            camera.takePicture(null,null,mPictureCallback);
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(50);
            }
        }
    }
}
