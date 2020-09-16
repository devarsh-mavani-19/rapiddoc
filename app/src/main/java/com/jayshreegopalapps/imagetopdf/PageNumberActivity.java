package com.jayshreegopalapps.imagetopdf;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.font.PDFont;
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDColor;

import java.io.IOException;
import java.util.ArrayList;

public class PageNumberActivity extends AppCompatActivity {
    int red = 0;
    int blue = 0;
    int green = 0;
    String colorHex = "#ffffffff";
    private int pageModeType = 0; //0 = single page 1 = facing page
    private int firstPageNumber = 0;
    private int fromRange = 0;
    private int toRange = 0;
    boolean isPdfSelected = false;
    TextView textView;
    FloatingActionButton fab_settings;
    FloatingActionButton fab_done;
    FloatingActionButton fab_add;
    PDFView pdfView;
    private int REQUEST_CODE_PDF = 0;
    PDDocument document;
    private float fontSizeValue = 18.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_number);
        fab_done = findViewById(R.id.page_number_done);
        fab_settings= findViewById(R.id.fab_settings);
        fab_add = findViewById(R.id.page_number_add);
        pdfView = findViewById(R.id.pdf_preview_page_number);
        textView = findViewById(R.id.text_view_page_number);
        pdfView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("application/pdf");
                startActivityForResult(i, REQUEST_CODE_PDF);
            }
        });
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("application/pdf");
                startActivityForResult(i, REQUEST_CODE_PDF);
            }
        });
        fab_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPdfSelected) {

                    PDFont font = PDType1Font.TIMES_ROMAN;
                    float fontSize = fontSizeValue;

                    if(toRange > document.getNumberOfPages())
                        toRange = document.getNumberOfPages();
                    if(fromRange < 0)
                        fromRange = 0;

                    final AlertDialog dialog = new AlertDialog.Builder(PageNumberActivity.this).setCancelable(false).setView(R.layout.layout_loading_dialog).create();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.show();
                        }
                    });

                    for(int i = fromRange;i < toRange;i++) {
                        try{

                            PDPage page =  document.getPage(i);
                            PDRectangle pageSize = page.getMediaBox();
                            float stringWidth = 0;
                            String message = String.valueOf((firstPageNumber));
                            firstPageNumber = firstPageNumber + 1;
                            try {
                                stringWidth = font.getStringWidth(message) * fontSize / 1000f;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            // calculate to center of the page
                            int rotation = page.getRotation();
                            boolean rotate = rotation == 90 || rotation == 270;
                            if(rotate) {
                                Toast.makeText(PageNumberActivity.this, "Please Turn off rotation for accurate result", Toast.LENGTH_SHORT).show();
                            }
                            float pageWidth = rotate ? pageSize.getHeight() : pageSize.getWidth();
                            float pageHeight = rotate ? pageSize.getWidth() : pageSize.getHeight();
                            double centeredXPosition = rotate ? pageHeight / 2f : (pageWidth - stringWidth) / 2f;
                            double centeredYPosition = rotate ? (pageWidth - stringWidth - 10)  : 70;
                            // append the content to the existing stream
                            PDPageContentStream contentStream = null;
                                contentStream = new PDPageContentStream(document, page, true, true, true);
                                contentStream.beginText();
                            // set font and font size
                                contentStream.setFont(font, fontSize);
                            // set text color to red
                                contentStream.setNonStrokingColor(red, green, blue);
                            if (rotate) {
                                // rotate the text according to the page rotation
                                    contentStream.setTextRotation(Math.PI / 2, centeredXPosition, centeredYPosition);
                            } else {
                                    contentStream.setTextTranslation(centeredXPosition, centeredYPosition);
                            }
                                contentStream.drawString(message);
                                contentStream.endText();
                                contentStream.close();
                            if(pageModeType != 0) {
                                i++;
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();

                        }
                    });
                    try {
                        String path = Constants.PDF_PATH + System.currentTimeMillis() + ".pdf";
                        document.save(path);
                        Intent in = new Intent(PageNumberActivity.this, OpenPDFActivity.class);
                        in.setDataAndType(Uri.parse("file:///" + path),"application/pdf");
                        startActivity(in);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(PageNumberActivity.this, "Please Select a pdf", Toast.LENGTH_SHORT).show();
                }
            }
        });
        fab_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog dialog = new AlertDialog.Builder(PageNumberActivity.this).setView(R.layout.page_number_settings_layout).create();
                dialog.show();
                final Spinner pageMode = dialog.findViewById(R.id.spinner_PageMode);
                final TextInputEditText fontSize = dialog.findViewById(R.id.edit_text_font_size);
                final TextInputEditText startingPage = dialog.findViewById(R.id.edit_text_starting_page);
                final TextInputEditText from = dialog.findViewById(R.id.edit_text_from);
                final TextInputEditText to = dialog.findViewById(R.id.edit_text_to);
                Button btnSave = dialog.findViewById(R.id.btn_save_settings);
                final Button colorPicker = dialog.findViewById(R.id.color_picker_indicator);
                ArrayList<String> pageModeList = new ArrayList<>();
                pageModeList.add("Single Page Mode");
                pageModeList.add("Facing Page Mode");
                ArrayAdapter adapter = new ArrayAdapter(PageNumberActivity.this, android.R.layout.simple_spinner_dropdown_item, pageModeList);
                pageMode.setAdapter(adapter);
                colorPicker.setBackgroundColor(Color.rgb(red, green, blue));
//                to.setText(String.valueOf(toRange));
//                from.setText(String.valueOf(fromRange));
//                startingPage.setText(String.valueOf(firstPageNumber));
//                pageMode.setSelection(pageModeType);
//                fontSize.setText(String.valueOf(fontSizeValue));
                colorPicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ColorPickerDialogBuilder
                                .with(PageNumberActivity.this)
                                .setTitle("Choose color")
                                .showAlphaSlider(false)
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
                                        colorHex = Integer.toHexString(selectedColor);
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
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(startingPage.getText().toString().equals("")) {
                            startingPage.setError("Please Enter Valid Page Number");
                        }
                        else if(from.getText().toString().equals("")) {
                            from.setError("Please Enter Valid Starting Range");
                        }
                        else if(to.getText().toString().equals("")) {
                            to.setError("Please Enter Valid Ending Range");
                        }
                        else if(fontSize.getText().toString().equals(""))
                        {
                            fontSize.setError("Select a Font Size");
                        }
                        else if(Integer.parseInt(from.getText().toString()) > Integer.parseInt(to.getText().toString())) {
                            Toast.makeText(PageNumberActivity.this, "Invalid Range", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            firstPageNumber = Integer.parseInt(startingPage.getText().toString());
                            fromRange = Integer.parseInt(from.getText().toString()) - 1;
                            toRange = Integer.parseInt(to.getText().toString()) - 1;
                            fontSizeValue = Float.parseFloat(fontSize.getText().toString());
                            pageModeType = pageMode.getSelectedItem().toString().equals("Single Page Mode") ? 0 : 1;
                            dialog.dismiss();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(PageNumberActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_PDF && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                pdfView.fromUri(uri).pages(0).load();
                isPdfSelected = true;
                try {
                    document = PDDocument.load(getContentResolver().openInputStream(uri));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                textView.setText("Preview");
            }
            else{
                Toast.makeText(this, "Failed To Fetch PDF", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        try {
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
