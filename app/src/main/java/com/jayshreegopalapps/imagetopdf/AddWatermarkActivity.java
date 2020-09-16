package com.jayshreegopalapps.imagetopdf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;

public class AddWatermarkActivity extends AppCompatActivity {
    PDFView pdfView;
    FloatingActionButton fab_add, fab_save, fab_settings;
    TextInputEditText editText;
    private int REQUEST_GET_PDF = 0;
    Uri mUri;

    int red = 0, blue = 0, green = 0;
    float fontSize = 10.0f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_watermark);
        initViews();
        refreshPage();
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFS();
            }
        });
        fab_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        fab_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog dialog = new AlertDialog.Builder(AddWatermarkActivity.this).setView(R.layout.add_watermark_settings).create();
                dialog.show();
                final TextInputEditText font = dialog.findViewById(R.id.font_size_watermark);
                Button save = dialog.findViewById(R.id.btn_save_settings_watermark);

                final Button colorPicker = dialog.findViewById(R.id.color_picker_indicator_watermak);
                colorPicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ColorPickerDialogBuilder
                                .with(AddWatermarkActivity.this)
                                .showAlphaSlider(false)
                                .setTitle("Choose color")
                                .initialColor(Color.argb(255, red, green, blue))
                                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                                .density(12)
                                .setOnColorSelectedListener(new OnColorSelectedListener() {
                                    @Override
                                    public void onColorSelected(int selectedColor) {

                                    }
                                })
                                .setPositiveButton("ok", new ColorPickerClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                        String colorHex = Integer.toHexString(selectedColor);
                                        Toast.makeText(AddWatermarkActivity.this, colorHex, Toast.LENGTH_SHORT).show();
                                        red = Integer.valueOf( colorHex.substring( 2, 4 ), 16 );
                                        green = Integer.valueOf( colorHex.substring( 4, 6 ), 16 );
                                        blue = Integer.valueOf( colorHex.substring( 6, 8 ), 16 );
                                        colorPicker.setBackgroundColor(Color.rgb(red, green, blue));

                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .build()
                                .show();
                    }
                });

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fontSize = Float.parseFloat(font.getText().toString());
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void save() {
        if(!isPermissionGranted()) {
            return;
        }

        if(editText.getText().toString().equals("")){
            Toast.makeText(this, "Select A PDF", Toast.LENGTH_SHORT).show();
            editText.setError("Enter Watermark Text");
            return;
        }
        AddWatermarkTask task = new AddWatermarkTask(AddWatermarkActivity.this);
        task.execute(editText.getText().toString(), mUri.toString(), fontSize + "", red + "", green + "", blue + "");
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
            createDirs();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("com.jayshreegopalapps.ImageToPdf", MODE_PRIVATE);
        if (prefs.getBoolean("watermark", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            prefs.edit().putBoolean("watermark", false).commit();
            FancyShowCaseView fancyShowCaseView1 =new FancyShowCaseView.Builder(this)
                    .focusOn(fab_add)
                    .title(" \n           " +
                            "\n   " +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n"+
                            "Add Pdf")

                    .build();



            FancyShowCaseView fancyShowCaseView2 =new FancyShowCaseView.Builder(this)
                    .focusOn(fab_save)
                    .title("Save watermarked PDF ")
                    .build()
                    ;
           /* new FancyShowCaseView.Builder(this)
                    .focusOn(getSupportActionBar().getCustomView().findViewById(R.menu.menu_main))
                    .title("Focus on View")
                    .build()
                    .show();*/
            new FancyShowCaseQueue()
                    .add(fancyShowCaseView1)
                    .add(fancyShowCaseView2)
                    .show();
        }
    }

    private void createDirs() {
        isPermissionGranted();
        File f = new File(Constants.PDF_MERGE_PATH);
        File f2 = new File(Constants.PDF_PATH);
        File f3 = new File(Constants.PDF_SPLIT_PATH);
        File f4 = new File(Constants.PDF_WATERMARK_PATH);
        File f5 = new File(Constants.PDF_STORAGE_PATH);

        if(!f.exists()) {
            f.mkdirs();
        }
        if(!f2.exists()) {
            f2.mkdirs();
        }
        if(!f3.exists()) {
            f3.mkdirs();
        }
        if(!f4.exists()) {
            f4.mkdirs();
        }
        if(!f5.exists()) {
            f5.mkdirs();
        }
    }

    private void openFS() {
        if(!isPermissionGranted()) {
            return;
        }
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select a PDF"), REQUEST_GET_PDF);
    }

    private boolean isPermissionGranted() {
        if ((ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED)|| (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED)|| (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},10);
            }
            return false;
        }
        else{
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_GET_PDF && resultCode == RESULT_OK) {
            if (data != null) {
                mUri = data.getData();
                pdfView.fromUri(mUri).pages(0).load();
                editText.setVisibility(View.VISIBLE);
            }

        }
    }

    private void refreshPage() {

    }

    private void initViews() {
        pdfView = findViewById(R.id.add_watermark_pdf_preview);
        fab_add = findViewById(R.id.add_watermark_add_pdf);
        fab_save = findViewById(R.id.add_watermark_save_pdf);
        fab_settings = findViewById(R.id.fab_settings_watermark);
        editText = findViewById(R.id.add_watermark_edit_text);
    }
}
