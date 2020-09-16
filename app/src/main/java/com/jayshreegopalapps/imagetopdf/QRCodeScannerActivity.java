package com.jayshreegopalapps.imagetopdf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.Result;

import java.security.Permission;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRCodeScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    int MY_PERMISSIONS_REQUEST_CAMERA=0;

    ZXingScannerView scannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
    }

    @Override
    public void handleResult(Result result) {
//        BottomModalQr qr=new BottomModalQr(result.getText(),getApplicationContext());
//        qr.show(getSupportFragmentManager(),"Scanned Text");

        if(result.getText().startsWith("mailto:")) {
            //send mail
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/html");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] {result.getText().substring(7)});
//            intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
//            intent.putExtra(Intent.EXTRA_TEXT, "I'm email body.");
            startActivity(Intent.createChooser(intent, "Send Email"));
        }
        else if(result.getText().startsWith("tel:")) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(result.getText()));
            startActivity(intent);
        }
        else if(result.getText().startsWith("SMSTO:")) {
            Intent I =new Intent(Intent.ACTION_VIEW);
            I.setData(Uri.  parse("smsto:"));
            I.setType("vnd.android-dir/mms-sms");
            I.putExtra("address", new String (result.getText().substring(6)));
            I.putExtra( "sms_body","Enter your Sms here..");
            startActivity(I);
        }
        else {
            BottomModalQr modalQr = new BottomModalQr(result.getText(), getApplicationContext(), new BottomModalQr.BottomModalQrListener() {
                @Override
                public void close() {
                    scannerView.setResultHandler(QRCodeScannerActivity.this);
                    scannerView.startCamera();
                }
            });
            modalQr.show(getSupportFragmentManager(),"SCAN_QR");
        }



    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        scannerView.setResultHandler(this);
//        scannerView.startCamera();
//    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }
}
