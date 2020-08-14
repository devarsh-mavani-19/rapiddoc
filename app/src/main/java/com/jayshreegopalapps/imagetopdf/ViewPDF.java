package com.jayshreegopalapps.imagetopdf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

public class ViewPDF extends AppCompatActivity {
    DatabaseManagment pdfStore;
    RecyclerView recyclerView;
    ArrayList<PDFModel> arrayList=  new ArrayList<>();
    PdfViewAdapter arrayAdapter;
    private String callingActivityType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);

        initTable();

        initViews();

        extractBundle();

        refreshPage();

    }

    private void extractBundle() {
        if(getIntent().getExtras()!=null) {
            callingActivityType = getIntent().getExtras().getString("result");
            System.out.println("calling " + callingActivityType);
        }
        else{
            callingActivityType = null;
        }
    }

    private void refreshPage() {
        fetchPDFs(Environment.getExternalStorageDirectory());
        arrayAdapter = new PdfViewAdapter(getApplicationContext(), arrayList, callingActivityType, new PDFviewChainInterfface() {
            @Override
            public void setRes(PDFModel pdfModel) {
                System.out.println("hello world viewpdf activity");
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", pdfModel.name);
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });

        recyclerView.setAdapter(arrayAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
    }

    private void fetchPDFs(File dir) {
//        Cursor cursor = pdfStore.customSelect("select * from PDFDetails order by creation DESC");
//        if(cursor.moveToNext()) {
//            do{
//                PDFModel pdfModel = new PDFModel();
//                pdfModel.uid = cursor.getString(0);
//                pdfModel.name = cursor.getString(1);
//                pdfModel.uid = cursor.getString(2);
//
//                arrayList.add(pdfModel);
//            }
//            while (cursor.moveToNext());
//        }
//        cursor.close();
        String pdfPattern = ".pdf";

        File FileList[] = dir.listFiles();

        if (FileList != null) {
            for (int i = 0; i < FileList.length; i++) {

                if (FileList[i].isDirectory()) {
                    fetchPDFs(FileList[i]);
                } else {
                    if (FileList[i].getName().endsWith(pdfPattern)){
                        //here you have that file.
                        PDFModel pdfModel = new PDFModel();
                        pdfModel.name = FileList[i].getAbsolutePath();
                        arrayList.add(pdfModel);
                    }
                }
            }
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_pdf);
    }

    private void initTable() {

        pdfStore = new DatabaseManagment(getApplicationContext());

        ArrayList<String> cName = new ArrayList<>();
        ArrayList<String> dTypes = new ArrayList<>();

        cName.add("uid");
        cName.add("name");
        cName.add("creation");

        dTypes.add("varchar(255)");
        dTypes.add("varchar(255)");
        dTypes.add("varchar(30)");

        pdfStore.createTable("PDFDetails", cName, dTypes);
    }
}
