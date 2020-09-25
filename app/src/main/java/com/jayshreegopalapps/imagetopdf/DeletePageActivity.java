package com.jayshreegopalapps.imagetopdf;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class DeletePageActivity extends AppCompatActivity {
    ArrayList<Integer> arrayList = new ArrayList<>();
    DeletePageAdapter adapter;
    FloatingActionButton fab, fab_save;
    GridView gridView;
    Uri uri;
    PDDocument document;
    private int REQUEST_CODE_PDF = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_page);
        fab = findViewById(R.id.fab_delete_load_pdf);
        fab_save= findViewById(R.id.fab_save_delete_page);
        gridView = findViewById(R.id.grid_view_delete_page);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("application/pdf");
                startActivityForResult(i, REQUEST_CODE_PDF);
            }
        });
        fab_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SparseBooleanArray selectedItems = adapter.getSelectedList();

                    System.out.println(selectedItems.size());
                    for(int i = 0;i < selectedItems.size();i++) {
                        if (Objects.equals(selectedItems.get(i), Boolean.TRUE)) {
                            document.removePage(i);
                        }
                    }

                    String fileName = System.currentTimeMillis() + ".pdf";
                    document.save(Constants.PDF_DELETED_PAGES + fileName);
                    Toast.makeText(DeletePageActivity.this, "PDF Saved at " + Constants.PDF_DELETED_PAGES + fileName, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(DeletePageActivity.this, "Failed To Remove Pages", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_PDF) {
            if(resultCode == RESULT_OK) {
                if(data!=null) {
                    uri = data.getData();
                    try {
                        document=PDDocument.load(getContentResolver().openInputStream(uri));
                        loadArraylist();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void loadArraylist() {
        arrayList.clear();
        if(document!=null) {
            for (int i = 0; i < document.getNumberOfPages(); i++) {
                arrayList.add(i);
            }
            adapter = new DeletePageAdapter(DeletePageActivity.this, arrayList,uri);
            gridView.setAdapter(adapter);
        }
    }

    @Override
    protected void onDestroy() {
        try{
            document.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();

    }
}
