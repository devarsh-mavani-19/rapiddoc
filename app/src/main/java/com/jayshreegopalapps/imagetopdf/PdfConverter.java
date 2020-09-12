package com.jayshreegopalapps.imagetopdf;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

//import com.itextpdf.text.Document;
//import com.itextpdf.text.DocumentException;
//import com.itextpdf.text.Image;
//import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class PdfConverter {

//    public static String convertFrom(Context context, String path) { //path = folder name
//        String pdfName = System.currentTimeMillis() + "";
//        Document document = new Document();
//        try {
//            PdfWriter.getInstance(document, new FileOutputStream(context.getExternalFilesDir(null) + "/" + pdfName + ".pdf"));
//            PdfWriter.getInstance(document, new FileOutputStream(Constants.PDF_STORAGE_PATH + pdfName + ".pdf"));
//
//            document.open();
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        DatabaseManagment databaseManagment = new DatabaseManagment(context);
//        databaseManagment.useTable("FileDetails");
//        Cursor cursor = databaseManagment.prepare().where("parent", "=", "'"+path+"'").and().where("type", "=", "'FILE'").select(new String[]{"name"});
//
//        if (cursor.moveToNext()) {
//            do {
//                Image image = null;
//                try {
//                    image = Image.getInstance((context.getExternalFilesDir(null) + "/" + cursor.getString(0)));
////                    float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
////                            - document.rightMargin() - 0) / image.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
////                    image.scalePercent(scaler);
//                    int wid = (int) (document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin());
//                    int hei = (int) (document.getPageSize().getHeight() - document.topMargin() - document.bottomMargin());
////                            - document.rightMargin() - 0)
//                    image.scaleToFit(wid, hei);
//                    image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
//
//                    try {
//                        document.add(image);
//                    } catch (DocumentException e) {
//                        e.printStackTrace();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            while (cursor.moveToNext());
//            document.close();
//            cursor.close();
//        }
//        DatabaseManagment pdfStore = new DatabaseManagment(context);
//
//        ArrayList<String> cName = new ArrayList<>();
//        ArrayList<String> dTypes = new ArrayList<>();
//
//        cName.add("uid");
//        cName.add("name");
//        cName.add("creation");
//
//        dTypes.add("varchar(255)");
//        dTypes.add("varchar(255)");
//        dTypes.add("varchar(30)");
//
//        pdfStore.createTable("PDFDetails", cName, dTypes);
//        pdfStore.customQuery("insert into PDFDetails values('" + pdfName + "', '" + pdfName + "', '" + pdfName + "');");
//        return pdfName;
//    }

    public static void pdf2image(Context context, String pdfName) {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();

        try {
            PdfRenderer pdfRenderer = new PdfRenderer(ParcelFileDescriptor.open(new File(pdfName), ParcelFileDescriptor.MODE_READ_ONLY));
            int pageCount = pdfRenderer.getPageCount();

            for(int i = 0;i < pageCount;i++) {
                PdfRenderer.Page page = pdfRenderer.openPage(i);
                int width = context.getResources().getDisplayMetrics().densityDpi / 72 * page.getWidth();
                int height = context.getResources().getDisplayMetrics().densityDpi / 72 * page.getHeight();

                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                bitmaps.add(bitmap);
                page.close();

            }
            pdfRenderer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        //create a folder at root level
        DatabaseManagment filesTable = new DatabaseManagment(context);
        filesTable.useTable("FileDetails");

        String directoryName = DateTimeUtils.getDateTime();
        String creationTime = DateTimeUtils.getDate();

        ArrayList<String> insertDirArrayList = new ArrayList<>();
        insertDirArrayList.add("'" + directoryName + "'"); //name
        insertDirArrayList.add("'" + "ROOT" + "'"); //category
        insertDirArrayList.add(""); //parent
        insertDirArrayList.add("'" + "DIR" + "'"); //type
        insertDirArrayList.add(""); //order number
        insertDirArrayList.add(""); //extension
        insertDirArrayList.add("'" + creationTime + "'"); //creation
        insertDirArrayList.add("'" + creationTime + "'"); //modified
        filesTable.insertRecord(insertDirArrayList); //save


        for(int i=0;i<bitmaps.size();i++) {
            //save image in device
            String fileName = System.currentTimeMillis() + "";
            try {
                FileOutputStream fos = new FileOutputStream(context.getExternalFilesDir(null) + "/" + fileName + ".png");
                bitmaps.get(i).compress(Bitmap.CompressFormat.PNG, 100, fos);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            //save image at root level folder

            ArrayList<String> insertImageArrayList = new ArrayList<>();
            insertImageArrayList.add("'" + fileName + ".png'"); //name
            insertImageArrayList.add("'" + "CHILD" + "'"); //category
            insertImageArrayList.add("'" + directoryName + "'"); //parent
            insertImageArrayList.add("'" + "FILE" + "'"); //type
            insertImageArrayList.add("" + (i+1)); //order number
            insertImageArrayList.add("'" + "PNG" + "'"); //extension
            insertImageArrayList.add("'" + creationTime + "'"); //creation
            insertImageArrayList.add("'" + creationTime + "'"); //modified
            filesTable.insertRecord(insertImageArrayList); //save
        }

    }

    public static void pdf2imageFromUri(Context context, Uri pdfUri) {

        //create a folder at root level
        DatabaseManagment filesTable = new DatabaseManagment(context);
        filesTable.useTable("FileDetails");

        String directoryName = DateTimeUtils.getDateTime();
        String creationTime = DateTimeUtils.getDate();

        ArrayList<String> insertDirArrayList = new ArrayList<>();
        insertDirArrayList.add("'" + directoryName + "'"); //name
        insertDirArrayList.add("'" + "ROOT" + "'"); //category
        insertDirArrayList.add(""); //parent
        insertDirArrayList.add("'" + "DIR" + "'"); //type
        insertDirArrayList.add(""); //order number
        insertDirArrayList.add(""); //extension
        insertDirArrayList.add("'" + creationTime + "'"); //creation
        insertDirArrayList.add("'" + creationTime + "'"); //modified
        insertDirArrayList.add("'" + "'"); //
        insertDirArrayList.add("'N" + "'"); //
        filesTable.insertRecord(insertDirArrayList); //save



        try {
            PdfRenderer pdfRenderer = new PdfRenderer(context.getContentResolver().openFileDescriptor(pdfUri,"r"));
            int pageCount = pdfRenderer.getPageCount();

            for(int i = 0;i < pageCount;i++) {
                PdfRenderer.Page page = pdfRenderer.openPage(i);
                int width = context.getResources().getDisplayMetrics().densityDpi / 72 * page.getWidth();
                int height = context.getResources().getDisplayMetrics().densityDpi / 72 * page.getHeight();

                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT);

                String fileName = System.currentTimeMillis() + "";

                FileOutputStream fos = new FileOutputStream(context.getExternalFilesDir(null) + "/" + fileName + ".png");
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                bitmap.recycle();

                ArrayList<String> insertImageArrayList = new ArrayList<>();
                insertImageArrayList.add("'" + fileName + ".png'"); //name
                insertImageArrayList.add("'" + "CHILD" + "'"); //category
                insertImageArrayList.add("'" + directoryName + "'"); //parent
                insertImageArrayList.add("'" + "FILE" + "'"); //type
                insertImageArrayList.add("" + (i+1)); //order number
                insertImageArrayList.add("'" + "PNG" + "'"); //extension
                insertImageArrayList.add("'" + creationTime + "'"); //creation
                insertImageArrayList.add("'" + creationTime + "'"); //modified
                insertImageArrayList.add("'" + "'"); //
                insertImageArrayList.add("'N" + "'"); //
                filesTable.insertRecord(insertImageArrayList); //save

                page.close();
            }
            pdfRenderer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}

