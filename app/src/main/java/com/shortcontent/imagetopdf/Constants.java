package com.shortcontent.imagetopdf;

import android.os.Environment;

public class Constants {
    public static int REQUEST_CODE_OPEN_CAMERA = 0;
    public static int REQUEST_CODE_IMPORT_FROM_GALLERY = 1;
    public static int REQUEST_CODE_COPY_IMAGES = 2;

    public static String PDF_STORAGE_PATH = Environment.getExternalStorageDirectory() + "/Documents/RapidDoc/";
    public static String PDF_MERGE_PATH = PDF_STORAGE_PATH + "Merge/";
    public static String PDF_SPLIT_PATH = PDF_STORAGE_PATH + "Split/";
    public static String PDF_WATERMARK_PATH = PDF_STORAGE_PATH + "Watermark/";
    public static String PDF_PATH = PDF_STORAGE_PATH + "PDF/";
    public static String ESIGNATURE_PATH = PDF_STORAGE_PATH + "ESIGNATURE/";
    public static final String PDF_ENCRYPTED = PDF_STORAGE_PATH + "ENCRYPTED/";
    public static final String PDF_2SIDE = PDF_STORAGE_PATH + "2SIDES/";



}
