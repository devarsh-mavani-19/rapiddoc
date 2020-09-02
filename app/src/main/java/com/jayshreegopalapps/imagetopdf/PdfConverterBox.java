package com.jayshreegopalapps.imagetopdf;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.widget.Toast;

import com.tom_roush.pdfbox.cos.COSBase;
import com.tom_roush.pdfbox.multipdf.Overlay;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.common.PDMetadata;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.common.PDStream;
import com.tom_roush.pdfbox.pdmodel.font.PDFont;
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font;
import com.tom_roush.pdfbox.pdmodel.graphics.PDXObject;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDColor;
import com.tom_roush.pdfbox.pdmodel.graphics.color.PDColorSpace;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.pdmodel.PDPage;
//import org.apache.pdfbox.pdmodel.PDPageContentStream;
//import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class PdfConverterBox {
    public static String convertFrom(Context context, String path) { //path = folderName
        String pdfName = path + "_" + System.currentTimeMillis() + "";
        try {
            PDDocument document = new PDDocument();
            SQLiteDatabase database = context.openOrCreateDatabase("FileInformationDB", Context.MODE_PRIVATE, null);
            String select = "select * from FileDetails where parent = '"+ path + "' and type = 'FILE'";
            Cursor cursor = database.rawQuery(select,null);

            if(cursor.moveToNext()) {
                do{
                    PDPage page = new PDPage();
                    document.addPage(page);
                    PDImageXObject image = PDImageXObject.createFromFile(context.getExternalFilesDir(null) + "/" + cursor.getString(0), document);
                    PDPageContentStream contentStream = new PDPageContentStream(document,page);
                    contentStream.drawImage(image, 0, 0, PDRectangle.A4.getWidth(), PDRectangle.A4.getHeight());
                    contentStream.close();
                }
                while(cursor.moveToNext());
            }

            cursor.close();
            document.save(Constants.PDF_PATH + pdfName + ".pdf");
            document.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return pdfName;
    }

    public static void pdf2imageFromUri(Context context, Uri pdfUri) {

    }

    public static void image2pdf(Context context, Uri imageUri) {

    }

    public static String addWatermark(Context context, Uri pdfUri, String text) {
        try {
            String watermarkpath = createWatermarkPdf(text);
            System.out.println("Watermark Path " + watermarkpath);
            if(watermarkpath == null) {
                return null;
            }

            PDDocument document = PDDocument.load(context.getContentResolver().openInputStream(pdfUri));
            HashMap<Integer, String> overlayGuide = new HashMap<>();
            for(int i = 0;i < document.getNumberOfPages();i++) {
                overlayGuide.put(i + 1, watermarkpath);
            }
            Overlay overlay = new Overlay();
            overlay.setInputPDF(document);
            overlay.setOverlayPosition(Overlay.Position.BACKGROUND);
            String op = Constants.PDF_WATERMARK_PATH + "watermarked_" + System.currentTimeMillis() + ".pdf";
            overlay.setOutputFile(op);
            overlay.overlay(overlayGuide);

//            document.save(path);
            document.close();
            return op;
//            Toast.makeText(context, "PDF Saved at " + path, Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String createWatermarkPdf(String text) {
        try {
            PDDocument document = new PDDocument();

            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream stream = new PDPageContentStream(document, page);

            stream.beginText();
            PDFont font = PDType1Font.HELVETICA;
            int fontSize = 8;


            float titleWidth = (font.getStringWidth(text) / 1000) * fontSize;
            float titleHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;

            stream.setFont(font, fontSize);
            stream.setNonStrokingColor(.53,.53,.53,0.1);
            stream.newLineAtOffset((page.getMediaBox().getWidth() - titleWidth - 20),  0 + titleHeight);
            stream.showText(text);
            stream.endText();
            stream.close();
            String path = Constants.PDF_WATERMARK_PATH + "watermark_" + System.currentTimeMillis() + ".pdf";
            document.save(path);
            document.close();
            return path;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
