package com.jayshreegopalapps.imagetopdf;

import android.os.Environment;

public class Constants {
    public static int REQUEST_CODE_OPEN_CAMERA = 0;
    public static int REQUEST_CODE_IMPORT_FROM_GALLERY = 1;
    public static int REQUEST_CODE_COPY_IMAGES = 2;

    public static String PDF_STORAGE_PATH = Environment.getExternalStorageDirectory() + "/Documents/";
}