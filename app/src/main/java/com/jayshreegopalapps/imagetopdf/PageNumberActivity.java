package com.jayshreegopalapps.imagetopdf;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.font.PDFont;
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.util.ArrayList;

public class PageNumberActivity extends AppCompatActivity {
    private int pageModeType = 0; //0 = single page 1 = facing page
    private int firstPageNumber = 0;
    private int fromRange = 0;
    private int toRange = 0;
    boolean isPdfSelected = false;
    TextView textView;
    FloatingActionButton fab_settings;
    FloatingActionButton fab_done;
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
        fab_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPdfSelected) {
                    PDFont font = PDType1Font.HELVETICA_BOLD;
                    float fontSize = fontSizeValue;
                    String message = String.valueOf(firstPageNumber++);
                    if(toRange > document.getNumberOfPages())
                        toRange = document.getNumberOfPages();
                    for(int i = fromRange;i < toRange;i++) {
                        try{
                            PDPage page =  document.getPage(i);
                            PDRectangle pageSize = page.getMediaBox();
                            float stringWidth = 0;
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
                                contentStream.setNonStrokingColor(0, 0, 0);
                            if (rotate) {
                                // rotate the text according to the page rotation
                                    contentStream.setTextRotation(Math.PI / 2, centeredXPosition, centeredYPosition);
                            } else {
                                    contentStream.setTextTranslation(centeredXPosition, centeredYPosition);
                            }
                                contentStream.drawString(message);
                                contentStream.endText();
                                contentStream.close();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    try {
                        document.save(Constants.PDF_PATH + System.currentTimeMillis() + ".pdf");
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
                final EditText fontSize = dialog.findViewById(R.id.edit_text_font_size);
                final EditText startingPage = dialog.findViewById(R.id.edit_text_starting_page);
                final EditText from = dialog.findViewById(R.id.edit_text_from);
                final EditText to = dialog.findViewById(R.id.edit_text_to);
                Button btnSave = dialog.findViewById(R.id.btn_save_settings);
                ArrayList<String> pageModeList = new ArrayList<>();
                pageModeList.add("Single Page Mode");
                pageModeList.add("Facing Page Mode");
                ArrayAdapter adapter = new ArrayAdapter(PageNumberActivity.this, android.R.layout.simple_spinner_dropdown_item, pageModeList);
                pageMode.setAdapter(adapter);
                to.setText(String.valueOf(toRange));
                from.setText(String.valueOf(fromRange));
                startingPage.setText(String.valueOf(firstPageNumber));
                pageMode.setSelection(pageModeType);
                fontSize.setText(String.valueOf(fontSizeValue));
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
                            fromRange = Integer.parseInt(from.getText().toString());
                            toRange = Integer.parseInt(to.getText().toString());
                            fontSizeValue = Float.parseFloat(fontSize.getText().toString());
                            pageModeType = pageMode.getSelectedItem().toString().equals("Single Page Mode") ? 0 : 1;
                            dialog.dismiss();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
